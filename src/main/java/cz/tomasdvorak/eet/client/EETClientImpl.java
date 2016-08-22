package cz.tomasdvorak.eet.client;

import java.util.Date;
import java.util.UUID;

import cz.etrzby.xml.BkpDigestType;
import cz.etrzby.xml.BkpElementType;
import cz.etrzby.xml.BkpEncodingType;
import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.PkpCipherType;
import cz.etrzby.xml.PkpDigestType;
import cz.etrzby.xml.PkpElementType;
import cz.etrzby.xml.PkpEncodingType;
import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaHlavickaType;
import cz.etrzby.xml.TrzbaKontrolniKodyType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import cz.tomasdvorak.eet.client.security.SecurityCodesGenerator;
import cz.tomasdvorak.eet.client.security.ServerKey;


class EETClientImpl extends SecureEETCommunication implements EETClient {


    public EETClientImpl(final ClientKey clientKey, final ServerKey serverKey) {
        super(clientKey, serverKey);
    }

    public OdpovedType submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException {
        return getPort(mode, endpointType).odeslaniTrzby(prepateData(receipt, mode, submissionType));
    }

    private TrzbaType prepateData(final TrzbaDataType data, final CommunicationMode mode, final SubmissionType submissionType) throws DataSigningException {
        return new TrzbaType()
                .withHlavicka(getHeader(mode, submissionType))
                .withData(data)
                .withKontrolniKody(getCheckCodes(data));

    }

    private TrzbaHlavickaType getHeader(final CommunicationMode mode, final SubmissionType submissionType) {
        return new TrzbaHlavickaType()
                .withUuidZpravy(UUID.randomUUID().toString())
                .withDatOdesl(new Date())
                .withOvereni(mode.isCheckOnly())
                .withPrvniZaslani(submissionType.isFirstSubmission());
    }

    private TrzbaKontrolniKodyType getCheckCodes(final TrzbaDataType data) throws DataSigningException {
        final SecurityCodesGenerator securityCodesGenerator = new SecurityCodesGenerator(getClientKey());

        final PkpElementType pkp = new PkpElementType()
                .withValue(securityCodesGenerator.getPKP(data))
                .withCipher(PkpCipherType.RSA_2048)
                .withDigest(PkpDigestType.SHA_256)
                .withEncoding(PkpEncodingType.BASE_64);

        final BkpElementType bkp = new BkpElementType()
                .withValue(securityCodesGenerator.getBKP(data))
                .withDigest(BkpDigestType.SHA_1)
                .withEncoding(BkpEncodingType.BASE_16);

        return new TrzbaKontrolniKodyType()
                .withPkp(pkp)
                .withBkp(bkp);
    }
}

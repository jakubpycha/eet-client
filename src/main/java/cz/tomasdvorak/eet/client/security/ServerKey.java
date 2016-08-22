package cz.tomasdvorak.eet.client.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;

/**
 * Keystore (=trustStore) holding root certificate of the CA used to create server's keys. The server public certificate
 * will be validated against the root CA (currently I.CA, not contained in the default java keystore).
 */
public class ServerKey {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ServerKey.class);

    static final String KEY_ALIAS = "SERVER";

    private final KeyStore trustStore;

    public ServerKey(final InputStream caCertificate) throws InvalidKeystoreException {
        try {
            this.trustStore = keystoreOf(caCertificate);

        } catch (CertificateException  e) {
            throw new InvalidKeystoreException(e);
        } catch (KeyStoreException e) {
        	throw new InvalidKeystoreException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new InvalidKeystoreException(e);
		} catch (IOException e) {
			throw new InvalidKeystoreException(e);
		}
    }

    private KeyStore keystoreOf(final InputStream caCertificate) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509Certificate certificate = (X509Certificate) cf.generateCertificate(caCertificate);

        logger.info("Server certificate serial number: " + certificate.getSerialNumber() + ", " + certificate.getIssuerDN().toString());

        ks.setCertificateEntry(KEY_ALIAS, certificate);
        return ks;
    }

    public Crypto getCrypto() {
        final Merlin merlin = new MerlinWithCRLDistributionPointsExtension();
        merlin.setTrustStore(this.trustStore);
        return merlin;
    }

    KeyStore getTrustStore() {
        return trustStore;
    }
}

//package cz.tomasdvorak.eet.client.security.crl;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.bouncycastle.asn1.ASN1Encodable;
//import org.bouncycastle.asn1.ASN1OctetString;
//import org.bouncycastle.asn1.DERIA5String;
//import org.bouncycastle.asn1.x509.CRLDistPoint;
//import org.bouncycastle.asn1.x509.DistributionPoint;
//import org.bouncycastle.asn1.x509.DistributionPointName;
//import org.bouncycastle.asn1.x509.Extension;
//import org.bouncycastle.asn1.x509.GeneralName;
//import org.bouncycastle.asn1.x509.GeneralNames;
//
//import java8.util.J8Arrays;
//import java8.util.Optional;
//import java8.util.function.Consumer;
//import java8.util.function.Function;
//import java8.util.function.Predicate;
//import java8.util.stream.Stream;
//import java8.util.stream.StreamSupport;
//
///**
// * Read all the CRLs from a X509Certificate instance. Based on bouncycastle rather than deprecated sun.security classes.
// */
//class CRLUtils {
//
//    static List<URI> getCRLs(final X509Certificate cert) {
//    	
//    	final List<URI> result=new ArrayList<URI>();
//    	
//    	getDistributionPoints(cert).forEach(new Consumer<DistributionPoint>() {
//
//			@Override
//			public void accept(DistributionPoint t) {
//				CRLUtils.parseURI(t).forEach(new Consumer<URI>() {
//
//					@Override
//					public void accept(URI t) {
//						result.add(t);
//						
//					}
//				});
//				
//			}
//		});
//    	
//         return result;
//    }
//
//    private static Stream<URI> parseURI(final DistributionPoint dp) {
//        
//    	return StreamSupport.stream(Arrays.asList(dp.getDistributionPoint()))
//                .filter(new Predicate<DistributionPointName>() {
//					@Override
//					public boolean test(DistributionPointName d) {
//						return CRLUtils.isValidDistributionPoint(d);
//					}
//				})
//                .flatMap(new Function<DistributionPointName, Stream<GeneralName>>() {
//					@Override
//					public Stream<GeneralName> apply(DistributionPointName o) {
//						return CRLUtils.getGeneralNames(o);
//					}
//				})
//                .filter(new Predicate<GeneralName>() {
//					@Override
//					public boolean test(GeneralName genName) {
//						return genName.getTagNo() == GeneralName.uniformResourceIdentifier;
//					}
//				})
//                .map(new Function<GeneralName, ASN1Encodable>() {
//					@Override
//					public ASN1Encodable apply(GeneralName g) {
//						return g.getName();
//					}
//				})
//                .map(new Function<ASN1Encodable, DERIA5String>() {
//					@Override
//					public DERIA5String apply(ASN1Encodable e) {
//						return DERIA5String.getInstance(e);
//					}
//				})
//                .map(new Function<DERIA5String, String>() {
//					@Override
//					public String apply(DERIA5String ds) {
//						return ds.getString();
//					}
//				})
//                .map(new Function<String, URI>() {
//					@Override
//					public URI apply(String s) {
//						return CRLUtils.toURI(s);
//					}
//				});
//    }
//
//    private static boolean isValidDistributionPoint(final DistributionPointName dpn) {
//        return dpn != null && dpn.getType() == DistributionPointName.FULL_NAME;
//    }
//
//    private static Stream<DistributionPoint> getDistributionPoints(final X509Certificate cert) {
//        return Optional.ofNullable(cert.getExtensionValue(Extension.cRLDistributionPoints.getId()))
//                .map(new Function<byte[], ASN1OctetString>() {
//					@Override
//					public ASN1OctetString apply(byte[] d) {
//						return ASN1OctetString.getInstance(d);
//					}
//				})
//                .map(new Function<ASN1OctetString, byte[]>() {
//					@Override
//					public byte[] apply(ASN1OctetString aos) {
//						return aos.getOctets();
//					}
//				})
//                .map(new Function<byte[], CRLDistPoint>() {
//					@Override
//					public CRLDistPoint apply(byte[] b) {
//						return CRLDistPoint.getInstance(b);
//					}
//				})
//                .map(new Function<CRLDistPoint, DistributionPoint[]>() {
//					@Override
//					public DistributionPoint[] apply(CRLDistPoint p) {
//						return p.getDistributionPoints();
//					}
//				})
//                .map(new Function<DistributionPoint[], Stream<DistributionPoint>>() {
//					@Override
//					public Stream<DistributionPoint> apply(DistributionPoint[] d) {
//						return J8Arrays.stream(d);
//					}
//				})
//                .orElse(StreamSupport.stream(new ArrayList<DistributionPoint>()));
//    }
//
//	private static Stream<GeneralName> getGeneralNames(final DistributionPointName dpn) {
//
//		return StreamSupport.stream(Arrays
//				.asList(GeneralNames.getInstance(dpn.getName()).getNames()));
//
//	}
//
//    private static URI toURI(final String uri) {
//        try {
//            return new URI(uri);
//        } catch (final URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}

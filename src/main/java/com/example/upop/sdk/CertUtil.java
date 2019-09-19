package com.example.upop.sdk;


import static com.example.upop.sdk.SDKConstants.UNIONPAY_CNNAME;
import static com.example.upop.sdk.SDKUtil.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: CertUtil
 * @Description: acpsdk certificate tools are mainly used to load and use certificates.
 * @date 2016-7-22 下午2:46:20
 *
 */
public class CertUtil {
	/** The certificate container is used to store the private key certificates for signing clients’ request messages. */
	private static KeyStore keyStore = null;
	/** Encryption public key and certificate for sensitive information */
	private static X509Certificate encryptCert = null;
	/** Encryption public key for magnetic tracks */
	private static PublicKey encryptTrackKey = null;
	/** Verify the messages, signatures, and certificates returned from China UnionPay. */
	private static X509Certificate validateCert = null;
	/** Authenticate the signatures of intermediate certificates */
	private static X509Certificate middleCert = null;
	/** Authenticate the signatures of root certificates */
	private static X509Certificate rootCert = null;
	/** The map used to store the public key certificates used to verify the messages and signatures returned from China UnionPay. */
	private static Map<String, X509Certificate> certMap = new HashMap<String, X509Certificate>();
	/** Map used to store clients’ private keys */
	private final static Map<String, KeyStore> keyStoreMap = new ConcurrentHashMap<String, KeyStore>();
	
	static {
		init();
	}
	
	/**
	 * Initialize all certificates.
	 */
	private static void init() {
		try {
			addProvider();//Add BC provider to the system
			initSignCert();//Initialize signed private key certificates
			initMiddleCert();//Initialize the intermediate certificates among the certificates for verifying certificates
			initRootCert();//Initialize the root certificates among the certificates for verifying certificates
			initEncryptCert();//Initialize encrypted public keys
			initTrackKey();//Construct encryption public key for magnetic tracks
			initValidateCertFromDir();//Initialize all certificates  for verifying certificates
		} catch (Exception e) {
			LogUtil.writeErrorLog("Initialization fails. (This can be regarded as an exception if a symmetric key is used for signature.)", e);
		}
	}
	
	/**
	 * Add signature, signature authentication, and encryption algorithm providers
	 */
	private static void addProvider(){
		if (Security.getProvider("BC") == null) {
			LogUtil.writeLog("add BC provider");
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		} else {
			Security.removeProvider("BC"); //When tomcat is automatically re-loaded during eclipse debugging, an exception occurs to BC for unknown reason.
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			LogUtil.writeLog("re-add BC provider");
		}
		printSysInfo();
	}
	
	/**
	 * Load signed certificates by using the private key paths and passwords configured in the configuration file acp_sdk.properties
	 */
	private static void initSignCert() {
		if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
			LogUtil.writeLog("No signed certificate will be loaded if the certificates are not signed based on RSA.");
			return;
		}
		if (SDKConfig.getConfig().getSignCertPath() == null 
				|| SDKConfig.getConfig().getSignCertPwd() == null
				|| SDKConfig.getConfig().getSignCertType() == null) {
			LogUtil.writeErrorLog("WARN: " + SDKConfig.SDK_SIGNCERT_PATH + "Or" + SDKConfig.SDK_SIGNCERT_PWD 
					+ "Or" + SDKConfig.SDK_SIGNCERT_TYPE + "Null.  Stop loading signed certificates.");
			return;
		}
		if (null != keyStore) {
			keyStore = null;
		}
		try {
			keyStore = getKeyInfo(SDKConfig.getConfig().getSignCertPath(),
					SDKConfig.getConfig().getSignCertPwd(), SDKConfig
							.getConfig().getSignCertType());
			LogUtil.writeLog("InitSignCert Successful. CertId=["
					+ getSignCertId() + "]");
		} catch (IOException e) {
			LogUtil.writeErrorLog("InitSignCert Error", e);
		}
	}

	/**
	 * Load the encryption certificates for sensitive information by using the paths configured in the configuration file acp_sdk.properties
	 */
	private static void initMiddleCert() {
		LogUtil.writeLog("Load intermediate certificates==>"+SDKConfig.getConfig().getMiddleCertPath());
		if (!isEmpty(SDKConfig.getConfig().getMiddleCertPath())) {
			middleCert = initCert(SDKConfig.getConfig().getMiddleCertPath());
			LogUtil.writeLog("Load MiddleCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.middle.path is empty");
		}
	}

	/**
	 * Load the encryption certificates for sensitive information by using the paths configured in the configuration file acp_sdk.properties
	 */
	private static void initRootCert() {
		LogUtil.writeLog("Load root certificates==>"+SDKConfig.getConfig().getRootCertPath());
		if (!isEmpty(SDKConfig.getConfig().getRootCertPath())) {
			rootCert = initCert(SDKConfig.getConfig().getRootCertPath());
			LogUtil.writeLog("Load RootCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.rootCert.path is empty");
		}
	}
	
	/**
	 * Load the public key superior certificates of China UnionPay by using the paths configured in the configuration file acp_sdk.properties
	 */
	private static void initEncryptCert() {
		LogUtil.writeLog("Load the encryption certificates for sensitive information==>"+SDKConfig.getConfig().getEncryptCertPath());
		if (!isEmpty(SDKConfig.getConfig().getEncryptCertPath())) {
			encryptCert = initCert(SDKConfig.getConfig().getEncryptCertPath());
			LogUtil.writeLog("Load EncryptCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptCert.path is empty");
		}
	}
	
	/**
	 * Load the public keys for magnetic tracks by using the paths configured in the configuration file acp_sdk.properties
	 */
	private static void initTrackKey() {
		if (!isEmpty(SDKConfig.getConfig().getEncryptTrackKeyModulus())
				&& !isEmpty(SDKConfig.getConfig().getEncryptTrackKeyExponent())) {
			encryptTrackKey = getPublicKey(SDKConfig.getConfig().getEncryptTrackKeyModulus(), 
					SDKConfig.getConfig().getEncryptTrackKeyExponent());
			LogUtil.writeLog("LoadEncryptTrackKey Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptTrackKey.modulus or acpsdk.encryptTrackKey.exponent is empty");
		}
	}

	/**
	 * Load the certificates for which signature authentication has been conducted by using the paths configured in the configuration file acp_sdk.properties
	 */
	private static void initValidateCertFromDir() {
		if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
			LogUtil.writeLog("No certificate  for verifying certificates will be loaded if the certificates are not signed based on RSA.");
			return;
		}
		certMap.clear();
		String dir = SDKConfig.getConfig().getValidateCertDir();
		LogUtil.writeLog("Load the catalog of certificates for which signature authentication has been conducted==>" + dir +" Note: If the version is 5.1.0 in the request message, the catalog of the certificates for verifying certificates will not be used and does not need to be set (this catalog, however, must be set if the version is 5.0.0).");
		if (isEmpty(dir)) {
			LogUtil.writeErrorLog("WARN: acpsdk.validateCert.dir is empty");
			return;
		}
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509", "BC");
			File fileDir = new File(dir);
			File[] files = fileDir.listFiles(new CerFilter());
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				in = new FileInputStream(file.getAbsolutePath());
				validateCert = (X509Certificate) cf.generateCertificate(in);
				certMap.put(validateCert.getSerialNumber().toString(),
						validateCert);
				// Print the information about certificate loading for debugging at the test phase
				LogUtil.writeLog("[" + file.getAbsolutePath() + "][CertId="
						+ validateCert.getSerialNumber().toString() + "]");
			}
			LogUtil.writeLog("LoadVerifyCert Successful");
		} catch (CertificateException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error", e);
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error File Not Found", e);
		} catch (NoSuchProviderException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error No BC Provider", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.toString());
				}
			}
		}
	}

	/**
	 * Load the signed certificates by using the paths and passwords that have been bound, and save the certificates in certKeyStoreMap.
	 * 
	 * @param certFilePath
	 * @param certPwd
	 */
	private static void loadSignCert(String certFilePath, String certPwd) {
		KeyStore keyStore = null;
		try {
			keyStore = getKeyInfo(certFilePath, certPwd, "PKCS12");
			keyStoreMap.put(certFilePath, keyStore);
			LogUtil.writeLog("LoadRsaCert Successful");
		} catch (IOException e) {
			LogUtil.writeErrorLog("LoadRsaCert Error", e);
		}
	}

	/**
	 * Initialize these certificates to public key certificates by means of their paths.
	 * @param path
	 * @return
	 */
	private static X509Certificate initCert(String path) {
		X509Certificate encryptCertTemp = null;
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509", "BC");
			in = new FileInputStream(path);
			encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
			// Print the information about certificate loading for debugging at the test phase
			LogUtil.writeLog("[" + path + "][CertId="
					+ encryptCertTemp.getSerialNumber().toString() + "]");
		} catch (CertificateException e) {
			LogUtil.writeErrorLog("InitCert Error", e);
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("InitCert Error File Not Found", e);
		} catch (NoSuchProviderException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error No BC Provider", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.toString());
				}
			}
		}
		return encryptCertTemp;
	}
	
	/**
	 * Obtain the PrivateKey objects of signed private key certificates by means of keyStore
	 * 
	 * @return
	 */
	public static PrivateKey getSignCertPrivateKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,
					SDKConfig.getConfig().getSignCertPwd().toCharArray());
			return privateKey;
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		}
	}
	/**
	 * Obtain PrivateKey objects by means of the private key certificates at the specified path
	 * 
	 * @return
	 */
	public static PrivateKey getSignCertPrivateKeyByStoreMap(String certPath,
			String certPwd) {
		if (!keyStoreMap.containsKey(certPath)) {
			loadSignCert(certPath, certPwd);
		}
		try {
			Enumeration<String> aliasenum = keyStoreMap.get(certPath)
					.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) keyStoreMap.get(certPath)
					.getKey(keyAlias, certPwd.toCharArray());
			return privateKey;
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		}
	}

	/**
	 * Obtain the PublicKey of the encryption certificate for sensitive information
	 * 
	 * @return
	 */
	public static PublicKey getEncryptCertPublicKey() {
		if (null == encryptCert) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getPublicKey();
			} else {
				LogUtil.writeErrorLog("acpsdk.encryptCert.path is empty");
				return null;
			}
		} else {
			return encryptCert.getPublicKey();
		}
	}
	
	/**
	 * Reset the public key of the encryption certificate for sensitive information
	 */
	public static void resetEncryptCertPublicKey() {
		encryptCert = null;
	}
	
	/**
	 * Obtain the PublicKey of the encryption certificate for magnetic tracks
	 * 
	 * @return
	 */
	public static PublicKey getEncryptTrackPublicKey() {
		if (null == encryptTrackKey) {
			initTrackKey();
		}
		return encryptTrackKey;
	}
	
	/**
	 * Obtain the corresponding certificate PublicKey in the Map for certificates  for verifying certificates by means of certID
	 * 
	 * @param certID: Physical serial number of the certificate
	 * @return Public keys obtained by means of certificate numbers
	 */
	public static PublicKey getValidatePublicKey(String certId) {
		X509Certificate cf = null;
		if (certMap.containsKey(certId)) {
			// The certificate object corresponding to the certID exists.
			cf = certMap.get(certId);
			return cf.getPublicKey();
		} else {
			// Re-load the catalog of certificates if such certificate object does not exist.
			initValidateCertFromDir();
			if (certMap.containsKey(certId)) {
				// The certificate object corresponding to the certID exists.
				cf = certMap.get(certId);
				return cf.getPublicKey();
			} else {
				LogUtil.writeErrorLog("There is a lack certId=[" + certId + "] of the corresponding certificate for verifying certificates. ");
				return null;
			}
		}
	}
	
	/**
	 * Obtain the certID of the signed private key certificate configured in the configuration file acp_sdk.properties.
	 * 
	 * @return Physical number of the certificate
	 */
	public static String getSignCertId() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (Exception e) {
			LogUtil.writeErrorLog("getSignCertId Error", e);
			return null;
		}
	}

	/**
	 * Obtain the certId of the encryption certificate for sensitive information
	 * 
	 * @return
	 */
	public static String getEncryptCertId() {
		if (null == encryptCert) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getSerialNumber().toString();
			} else {
				LogUtil.writeErrorLog("acpsdk.encryptCert.path is empty");
				return null;
			}
		} else {
			return encryptCert.getSerialNumber().toString();
		}
	}

	/**
	 * Read the signed private key certificate as the storage object of the certificate.
	 * 
	 * @param pfxkeyfile
	 *            Certificate name
	 * @param keypwd
	 *            Certificate password
	 * @param type
	 *            Certificate type
	 * @return Certificate object
	 * @throws IOException 
	 */
	private static KeyStore getKeyInfo(String pfxkeyfile, String keypwd,
			String type) throws IOException {
		LogUtil.writeLog("Load signed certificates==>" + pfxkeyfile);
		FileInputStream fis = null;
		try {
			KeyStore ks = KeyStore.getInstance(type, "BC");
			LogUtil.writeLog("Load RSA CertPath=[" + pfxkeyfile + "],Pwd=["+ keypwd + "],type=["+type+"]");
			fis = new FileInputStream(pfxkeyfile);
			char[] nPassword = null;
			nPassword = null == keypwd || "".equals(keypwd.trim()) ? null: keypwd.toCharArray();
			if (null != ks) {
				ks.load(fis, nPassword);
			}
			return ks;
		} catch (Exception e) {
			LogUtil.writeErrorLog("getKeyInfo Error", e);
			return null;
		} finally {
			if(null!=fis){
				fis.close();}
		}
	}
	
	/**
	 * Obtain the certId of the signed private key certificate by means of its path and password.
	 * @param certPath
	 * @param certPwd
	 * @return
	 */
	public static String getCertIdByKeyStoreMap(String certPath, String certPwd) {
		if (!keyStoreMap.containsKey(certPath)) {
			// If such certID is not found in the cache, load an RSA certificate.
			loadSignCert(certPath, certPwd);
		}
		return getCertIdIdByStore(keyStoreMap.get(certPath));
	}
	
	/**
	 * Obtain the certID value of the private key certificate by means of keyStore.
	 * @param keyStore
	 * @return
	 */
	private static String getCertIdIdByStore(KeyStore keyStore) {
		Enumeration<String> aliasenum = null;
		try {
			aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getCertIdIdByStore Error", e);
			return null;
		}
	}
	
	/**
	 * Generate an RSA public key by means of module and exponent. Note: This code uses RSA/None/PKCS1Padding as the default padding method. The default padding methods for different JDKs may be different.
	 * 
	 * @param modulus
	 *            Module
	 * @param exponent
	 *            Exponent
	 * @return
	 */
	private static PublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			LogUtil.writeErrorLog("Fail to construct an RSA public key:" + e);
			return null;
		}
	}
	
	/**
	 * Convert the string into X509Certificate object.
	 * 
	 * @param x509CertString
	 * @return
	 */
	public static X509Certificate genCertificateByStr(String x509CertString) {
		X509Certificate x509Cert = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC"); 
			InputStream tIn = new ByteArrayInputStream(
					x509CertString.getBytes("ISO-8859-1"));
			x509Cert = (X509Certificate) cf.generateCertificate(tIn);
		} catch (Exception e) {
			LogUtil.writeErrorLog("gen certificate error", e);			
		}
		return x509Cert;
	}
	
	/**
	 * Obtain the intermediate certificate used to conduct signature authentication for public keys from the configuration file acp_sdk.properties.
	 * @return
	 */
	public static X509Certificate getMiddleCert() {
		if (null == middleCert) {
			String path = SDKConfig.getConfig().getMiddleCertPath();
			if (!isEmpty(path)) {
				initMiddleCert();
			} else {
				LogUtil.writeErrorLog(SDKConfig.SDK_MIDDLECERT_PATH + " not set in " + SDKConfig.FILE_NAME);
				return null;
			}
		}
		return middleCert;
	}
	
	/**
	 * Obtain the root certificate used to conduct signature authentication for public keys from the configuration file acp_sdk.properties.
	 * @return
	 */
	public static X509Certificate getRootCert() {
		if (null == rootCert) {
			String path = SDKConfig.getConfig().getRootCertPath();
			if (!isEmpty(path)) {
				initRootCert();
			} else {
				LogUtil.writeErrorLog(SDKConfig.SDK_ROOTCERT_PATH + " not set in " + SDKConfig.FILE_NAME);
				return null;
			}
		}
		return rootCert;
	}

	/**
	 * Obtain the CN of the certificate
	 * @param aCert
	 * @return
	 */
	private static String getIdentitiesFromCertficate(X509Certificate aCert) {
		String tDN = aCert.getSubjectDN().toString(); 
		String tPart = "";
		if ((tDN != null)) {
			String tSplitStr[] = tDN.substring(tDN.indexOf("CN=")).split("@");
			if (tSplitStr != null && tSplitStr.length > 2
					&& tSplitStr[2] != null){
				tPart = tSplitStr[2];}
		}
		return tPart;
	}
	
	/**
	 * Verify the certificate chain.
	 * @param cert
	 * @return
	 */
	private static boolean verifyCertificateChain(X509Certificate cert){
		
		if ( null == cert) {
			LogUtil.writeErrorLog("cert must Not null");
			return false;
		}
		
		X509Certificate middleCert = CertUtil.getMiddleCert();
		if (null == middleCert) {
			LogUtil.writeErrorLog("middleCert must Not null");
			return false;
		}
		
		X509Certificate rootCert = CertUtil.getRootCert();
		if (null == rootCert) {
			LogUtil.writeErrorLog("rootCert or cert must Not null");
			return false;
		}
		
		try {
		
	        X509CertSelector selector = new X509CertSelector();
	        selector.setCertificate(cert);
	        
	        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
	        trustAnchors.add(new TrustAnchor(rootCert, null));
	        PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(
			        trustAnchors, selector);
	
	        Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
	        intermediateCerts.add(rootCert);
	        intermediateCerts.add(middleCert);
	        intermediateCerts.add(cert);
	        
	        pkixParams.setRevocationEnabled(false);
	
	        CertStore intermediateCertStore = CertStore.getInstance("Collection",
	                new CollectionCertStoreParameters(intermediateCerts), "BC");
	        pkixParams.addCertStore(intermediateCertStore);
	
	        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
	        
        	@SuppressWarnings("unused")
			PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder
                .build(pkixParams);
			LogUtil.writeLog("verify certificate chain succeed.");
			return true;
        } catch (java.security.cert.CertPathBuilderException e){
			LogUtil.writeErrorLog("verify certificate chain fail.", e);
		} catch (Exception e) {
			LogUtil.writeErrorLog("verify certificate chain exception: ", e);
		}
		return false;
	}
	
	/**
	 * Check the certificate chain.
	 * 
	 * @param rootCerts
	 *            Root certificates
	 * @param cert
	 *            Certificates to be authenticated
	 * @return
	 */
	public static boolean verifyCertificate(X509Certificate cert) {
		
		if ( null == cert) {
			LogUtil.writeErrorLog("cert must Not null");
			return false;
		}
		try {
			cert.checkValidity();//Authentication expiry date
//			cert.verify(middleCert.getPublicKey());
			if(!verifyCertificateChain(cert)){
				return false;
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog("verifyCertificate fail", e);
			return false;
		}
		
		if(SDKConfig.getConfig().isIfValidateCNName()){
			// Check whether the public key belongs to China UnionPay.
			if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert))) {
				LogUtil.writeErrorLog("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
				return false;
			}
		} else {
			// Check whether the public key belongs to China UnionPay.
			if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert)) 
					&& !"00040000:SIGN".equals(CertUtil.getIdentitiesFromCertficate(cert))) {
				LogUtil.writeErrorLog("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
				return false;
			}
		}
		return true;		
	}

	/**
	 * Print the system environment information
	 */
	private static void printSysInfo() {
		LogUtil.writeLog("================= SYS INFO begin====================");
		LogUtil.writeLog("os_name:" + System.getProperty("os.name"));
		LogUtil.writeLog("os_arch:" + System.getProperty("os.arch"));
		LogUtil.writeLog("os_version:" + System.getProperty("os.version"));
		LogUtil.writeLog("java_vm_specification_version:"
				+ System.getProperty("java.vm.specification.version"));
		LogUtil.writeLog("java_vm_specification_vendor:"
				+ System.getProperty("java.vm.specification.vendor"));
		LogUtil.writeLog("java_vm_specification_name:"
				+ System.getProperty("java.vm.specification.name"));
		LogUtil.writeLog("java_vm_version:"
				+ System.getProperty("java.vm.version"));
		LogUtil.writeLog("java_vm_name:" + System.getProperty("java.vm.name"));
		LogUtil.writeLog("java.version:" + System.getProperty("java.version"));
		LogUtil.writeLog("java.vm.vendor=[" + System.getProperty("java.vm.vendor") + "]");
		LogUtil.writeLog("java.version=[" + System.getProperty("java.version") + "]");
		printProviders();
		LogUtil.writeLog("================= SYS INFO end=====================");
	}
	
	/**
	 * Print the list of algorithm providers
	 */
	private static void printProviders() {
		LogUtil.writeLog("Providers List:");
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			LogUtil.writeLog(i + 1 + "." + providers[i].getName());
		}
	}

	/**
	 * Certificate file filter
	 * 
	 */
	static class CerFilter implements FilenameFilter {
		public boolean isCer(String name) {
			if (name.toLowerCase().endsWith(".cer")) {
				return true;
			} else {
				return false;
			}
		}
		@Override
    public boolean accept(File dir, String name) {
			return isCer(name);
		}
	}

}

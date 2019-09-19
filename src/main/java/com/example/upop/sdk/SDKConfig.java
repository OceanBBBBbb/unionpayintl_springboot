package com.example.upop.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @ClassName SDKConfig
 * @Description acpsdk configuration file acp_sdk.properties configuration information class
 * @date 2016-7-22 下午4:04:55
 *
 */
public class SDKConfig {
	public static final String FILE_NAME = "acp_sdk.properties";
	/** Foreground request URL. */
	private String frontRequestUrl;
	/** Background request URL. */
	private String backRequestUrl;
	/** Single query */
	private String singleQueryUrl;
	/** Batch query */
	private String batchQueryUrl;
	/** Batch transaction */
	private String batchTransUrl;
	/** File transmission */
	private String fileTransUrl;
	/** Path of signed certificate. */
	private String signCertPath;
	/** Password of signed certificate. */
	private String signCertPwd;
	/** Type of signed certificate. */
	private String signCertType;
	/** Path of encrypted public key certificate. */
	private String encryptCertPath;
	/** Authenticate the catalog of signed public key certificates. */
	private String validateCertDir;
	/** Read the catalog of specified signed certificates according to client codes. */
	private String signCertDir;
	/** Path of encrypted certificates for magnetic tracks. */
	private String encryptTrackCertPath;
	/** Module of encrypted public keys for magnetic tracks. */
	private String encryptTrackKeyModulus;
	/** Exponent of encrypted public keys for magnetic tracks. */
	private String encryptTrackKeyExponent;
	/** Card transaction. */
	private String cardRequestUrl;
	/** App transaction */
	private String appRequestUrl;
	/** Certificate usage mode (single certificate/multi-certificate) */
	private String singleMode;
	/** Security key (used in calculation of SHA256 and SM3) */
	private String secureKey;
	/** Path of intermediate certificates  */
	private String middleCertPath;
	/** Path of root certificates  */
	private String rootCertPath;
	/** For whether to verify the CNs of the certificates for verifying certificates, all certificates except the ones for which this parameter has been set to false should be authenticated.  */
	private boolean ifValidateCNName = true;
	/** For whether to authenticate the https certificate, all certificates need not to be authenticated by default.  */
	private boolean ifValidateRemoteCert = false;
	/** For signMethod, use the method corresponding to 01 if this parameter has not been set.  */
	private String signMethod = "01";
	/** For version, use the version 5.0.0 if this parameter has not been set.  */
	private String version = "5.0.0";
	/** frontUrl  */
	private String frontUrl;
	/** backUrl  */
	private String backUrl;

	/*Payment-related addresses*/
	private String jfFrontRequestUrl;
	private String jfBackRequestUrl;
	private String jfSingleQueryUrl;
	private String jfCardRequestUrl;
	private String jfAppRequestUrl;
	
	private String qrcBackTransUrl;
	private String qrcB2cIssBackTransUrl;
	private String qrcB2cMerBackTransUrl;

	/** Foreground URL constant in the configuration file. */
	public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
	/** Background URL constant in the configuration file. */
	public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
	/** URL constant for single transaction query in the configuration file. */
	public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
	/** URL constant for batch transaction query in the configuration file. */
	public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
	/** URL constant for batch transactions in the configuration file. */
	public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
	/** URL constant for file transactions in the configuration file. */
	public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
	/** URL constant for card transactions in the configuration file. */
	public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
	/** URL constant for app transactions in the configuration file. */
	public static final String SDK_APP_URL = "acpsdk.appTransUrl";

	/** Use the following payment products and neglect the remaining products which will not be used. */
	// Foreground request address
	public static final String JF_SDK_FRONT_TRANS_URL= "acpsdk.jfFrontTransUrl";
	// Background request address
	public static final String JF_SDK_BACK_TRANS_URL="acpsdk.jfBackTransUrl";
	// Request address for single query
	public static final String JF_SDK_SINGLE_QUERY_URL="acpsdk.jfSingleQueryUrl";
	// Address for card transaction
	public static final String JF_SDK_CARD_TRANS_URL="acpsdk.jfCardTransUrl";
	// Address for App transaction
	public static final String JF_SDK_APP_TRANS_URL="acpsdk.jfAppTransUrl";
	// Person to person
	public static final String QRC_BACK_TRANS_URL="acpsdk.qrcBackTransUrl";
	// Person to person
	public static final String QRC_B2C_ISS_BACK_TRANS_URL="acpsdk.qrcB2cIssBackTransUrl";
	// Person to person
	public static final String QRC_B2C_MER_BACK_TRANS_URL="acpsdk.qrcB2cMerBackTransUrl";
	
	
	/** Path constant of signed certificates in the configuration file. */
	public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
	/** Password constant of signed certificates in the configuration file. */
	public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
	/** Type constant of signed certificates in the configuration file. */
	public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
	/** Path constant of password-encrypted certificates in the configuration file. */
	public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
	/** Path constant of magnetic track-encrypted certificates in the configuration file. */
	public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
	/** Mould constant of magnetic track-encrypted public keys in the configuration file. */
	public static final String SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk.encryptTrackKey.modulus";
	/** Exponent constant of magnetic track-encrypted public keys in the configuration file. */
	public static final String SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk.encryptTrackKey.exponent";
	/** Catalog constant of certificates for which signature authentication has been conducted in the configuration file. */
	public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";

	/** Whether to encrypt the cvn2 constant in the configuration file. */
	public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
	/** Whether to encrypt the cvn2 expiry date constant in the configuration file. */
	public static final String SDK_DATE_ENC = "acpsdk.date.enc";
	/** Whether to encrypt the card number constant in the configuration file. */
	public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
	/** Usage pattern of certificates in the configuration file */
	public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
	/** Security key in the configuration file */
	public static final String SDK_SECURITYKEY = "acpsdk.secureKey";
	/** Path constant of root certificates in the configuration file.  */
	public static final String SDK_ROOTCERT_PATH = "acpsdk.rootCert.path";
	/** Path constant of root certificates in the configuration file.  */
	public static final String SDK_MIDDLECERT_PATH = "acpsdk.middleCert.path";
	/** When configuring whether to verify the CNs of the certificates for verifying certificates, treat all values except “false” as “true”. */
	public static final String SDK_IF_VALIDATE_CN_NAME = "acpsdk.ifValidateCNName";
	/** When configuring whether to authenticate https certificates, treat all values except “true” as “false”. */
	public static final String SDK_IF_VALIDATE_REMOTE_CERT = "acpsdk.ifValidateRemoteCert";
	/** signmethod */
	public static final String SDK_SIGN_METHOD ="acpsdk.signMethod";
	/** version */
	public static final String SDK_VERSION = "acpsdk.version";
	/** Address of background notifications  */
	public static final String SDK_BACKURL = "acpsdk.backUrl";
	/** Address of foreground notifications  */
	public static final String SDK_FRONTURL = "acpsdk.frontUrl";
	/** Operation object. */
	private static SDKConfig config = new SDKConfig();
	/** Property file object. */
	private Properties properties;

	private SDKConfig() {
		super();
	}

	/**
	 * Obtain config objects.
	 * @return
	 */
	public static SDKConfig getConfig() {
		return config;
	}

	/**
	 * Load from properties files
	 * 
	 * @param rootPath
	 *            Catalog containing no file name.
	 */
	public void loadPropertiesFromPath(String rootPath) {
		if (StringUtils.isNotBlank(rootPath)) {
			LogUtil.writeLog("Read the configuration file from the path: " + rootPath+File.separator+FILE_NAME);
			File file = new File(rootPath + File.separator + FILE_NAME);
			InputStream in = null;
			if (file.exists()) {
				try {
					in = new FileInputStream(file);
					properties = new Properties();
					properties.load(in);
					loadProperties(properties);
				} catch (FileNotFoundException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (IOException e) {
							LogUtil.writeErrorLog(e.getMessage(), e);
						}
					}
				}
			} else {
				// As the log may have not been loaded at this time, standard output is used to print log information.
				LogUtil.writeErrorLog(rootPath + FILE_NAME + "Parameter loading fails because the parameter does not exist.");
			}
		} else {
			loadPropertiesFromSrc();
		}

	}

	/**
	 * Load configuration parameters from the classpath path.
	 */
	public void loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			LogUtil.writeLog("From classpath: " +SDKConfig.class.getClassLoader().getResource("").getPath()+" Obtain the property file."+FILE_NAME);
			in = SDKConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
			if (null != in) {
				properties = new Properties();
				try {
					properties.load(in);
				} catch (IOException e) {
					throw e;
				}
			} else {
				LogUtil.writeErrorLog(FILE_NAME + "No property file is found under the specified directory at the classpath! ");
				return;
			}
			loadProperties(properties);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Set the configuration parameters according to the transferred {@link #load(Properties)} object.
	 * 
	 * @param pro
	 */
	public void loadProperties(Properties pro) {
		LogUtil.writeLog("Start to load configuration items from the property file.");
		String value = null;
		
		value = pro.getProperty(SDK_SIGNCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPath = value.trim();
			LogUtil.writeLog("Configuration item: Path of signed private key certificate==>"+SDK_SIGNCERT_PATH +"==>"+ value+" loaded ");
		}
		value = pro.getProperty(SDK_SIGNCERT_PWD);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPwd = value.trim();
			LogUtil.writeLog("Configuration item: Password of signed private key certificate==>"+SDK_SIGNCERT_PWD +" loaded");
		}
		value = pro.getProperty(SDK_SIGNCERT_TYPE);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertType = value.trim();
			LogUtil.writeLog("Configuration item: Type of signed private key certificate==>"+SDK_SIGNCERT_TYPE +"==>"+ value+" loaded ");
		}
		value = pro.getProperty(SDK_ENCRYPTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptCertPath = value.trim();
			LogUtil.writeLog("Configuration item: Encryption certificate for sensitive information==>"+SDK_ENCRYPTCERT_PATH +"==>"+ value+" loaded ");
		}
		value = pro.getProperty(SDK_VALIDATECERT_DIR);
		if (!SDKUtil.isEmpty(value)) {
			this.validateCertDir = value.trim();
			LogUtil.writeLog("Configuration item: Path of certificates for which signature authentication has been conducted (as a directory is configured here, do not specify it to a public key file)==>"+SDK_VALIDATECERT_DIR +"==>"+ value+" loaded ");
		}
		value = pro.getProperty(SDK_FRONT_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACK_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATTRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_FILETRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_SIGNQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_CARD_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_APP_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_ENCRYPTTRACKCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPath = value.trim();
		}

		value = pro.getProperty(SDK_SECURITYKEY);
		if (!SDKUtil.isEmpty(value)) {
			this.secureKey = value.trim();
		}
		value = pro.getProperty(SDK_ROOTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.rootCertPath = value.trim();
		}
		value = pro.getProperty(SDK_MIDDLECERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.middleCertPath = value.trim();
		}

		/**Payment part**/
		value = pro.getProperty(JF_SDK_FRONT_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfFrontRequestUrl = value.trim();
		}

		value = pro.getProperty(JF_SDK_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfBackRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_SINGLE_QUERY_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfSingleQueryUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_CARD_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfCardRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_APP_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfAppRequestUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_ISS_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cIssBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_MER_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cMerBackTransUrl = value.trim();
		}

		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_EXPONENT);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyExponent = value.trim();
		}

		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_MODULUS);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyModulus = value.trim();
		}
		
		value = pro.getProperty(SDK_IF_VALIDATE_CN_NAME);
		if (!SDKUtil.isEmpty(value)) {
			if( SDKConstants.FALSE_STRING.equals(value.trim())) {
				this.ifValidateCNName = false;
			}
		}
		
		value = pro.getProperty(SDK_IF_VALIDATE_REMOTE_CERT);
		if (!SDKUtil.isEmpty(value)) {
			if( SDKConstants.TRUE_STRING.equals(value.trim())) {
				this.ifValidateRemoteCert = true;
			}
		}
		
		value = pro.getProperty(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}
		
		value = pro.getProperty(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}
		value = pro.getProperty(SDK_VERSION);
		if (!SDKUtil.isEmpty(value)) {
			this.version = value.trim();
		}
		value = pro.getProperty(SDK_FRONTURL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACKURL);
		if (!SDKUtil.isEmpty(value)) {
			this.backUrl = value.trim();
		}
	}


	public String getFrontRequestUrl() {
		return frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPath() {
		return signCertPath;
	}

	public void setSignCertPath(String signCertPath) {
		this.signCertPath = signCertPath;
	}

	public String getSignCertPwd() {
		return signCertPwd;
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public String getSignCertType() {
		return signCertType;
	}

	public void setSignCertType(String signCertType) {
		this.signCertType = signCertType;
	}

	public String getEncryptCertPath() {
		return encryptCertPath;
	}

	public void setEncryptCertPath(String encryptCertPath) {
		this.encryptCertPath = encryptCertPath;
	}
	
	public String getValidateCertDir() {
		return validateCertDir;
	}

	public void setValidateCertDir(String validateCertDir) {
		this.validateCertDir = validateCertDir;
	}

	public String getSingleQueryUrl() {
		return singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public String getSignCertDir() {
		return signCertDir;
	}

	public void setSignCertDir(String signCertDir) {
		this.signCertDir = signCertDir;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getCardRequestUrl() {
		return cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}
	
	public String getEncryptTrackCertPath() {
		return encryptTrackCertPath;
	}

	public void setEncryptTrackCertPath(String encryptTrackCertPath) {
		this.encryptTrackCertPath = encryptTrackCertPath;
	}
	
	public String getJfFrontRequestUrl() {
		return jfFrontRequestUrl;
	}

	public void setJfFrontRequestUrl(String jfFrontRequestUrl) {
		this.jfFrontRequestUrl = jfFrontRequestUrl;
	}

	public String getJfBackRequestUrl() {
		return jfBackRequestUrl;
	}

	public void setJfBackRequestUrl(String jfBackRequestUrl) {
		this.jfBackRequestUrl = jfBackRequestUrl;
	}

	public String getJfSingleQueryUrl() {
		return jfSingleQueryUrl;
	}

	public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
		this.jfSingleQueryUrl = jfSingleQueryUrl;
	}

	public String getJfCardRequestUrl() {
		return jfCardRequestUrl;
	}

	public void setJfCardRequestUrl(String jfCardRequestUrl) {
		this.jfCardRequestUrl = jfCardRequestUrl;
	}

	public String getJfAppRequestUrl() {
		return jfAppRequestUrl;
	}

	public void setJfAppRequestUrl(String jfAppRequestUrl) {
		this.jfAppRequestUrl = jfAppRequestUrl;
	}

	public String getSingleMode() {
		return singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}

	public String getEncryptTrackKeyExponent() {
		return encryptTrackKeyExponent;
	}

	public void setEncryptTrackKeyExponent(String encryptTrackKeyExponent) {
		this.encryptTrackKeyExponent = encryptTrackKeyExponent;
	}

	public String getEncryptTrackKeyModulus() {
		return encryptTrackKeyModulus;
	}

	public void setEncryptTrackKeyModulus(String encryptTrackKeyModulus) {
		this.encryptTrackKeyModulus = encryptTrackKeyModulus;
	}
	
	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String securityKey) {
		this.secureKey = securityKey;
	}
	
	public String getMiddleCertPath() {
		return middleCertPath;
	}

	public void setMiddleCertPath(String middleCertPath) {
		this.middleCertPath = middleCertPath;
	}
	
	public boolean isIfValidateCNName() {
		return ifValidateCNName;
	}

	public void setIfValidateCNName(boolean ifValidateCNName) {
		this.ifValidateCNName = ifValidateCNName;
	}

	public boolean isIfValidateRemoteCert() {
		return ifValidateRemoteCert;
	}

	public void setIfValidateRemoteCert(boolean ifValidateRemoteCert) {
		this.ifValidateRemoteCert = ifValidateRemoteCert;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}
	public String getQrcBackTransUrl() {
		return qrcBackTransUrl;
	}

	public void setQrcBackTransUrl(String qrcBackTransUrl) {
		this.qrcBackTransUrl = qrcBackTransUrl;
	}

	public String getQrcB2cIssBackTransUrl() {
		return qrcB2cIssBackTransUrl;
	}

	public void setQrcB2cIssBackTransUrl(String qrcB2cIssBackTransUrl) {
		this.qrcB2cIssBackTransUrl = qrcB2cIssBackTransUrl;
	}

	public String getQrcB2cMerBackTransUrl() {
		return qrcB2cMerBackTransUrl;
	}

	public void setQrcB2cMerBackTransUrl(String qrcB2cMerBackTransUrl) {
		this.qrcB2cMerBackTransUrl = qrcB2cMerBackTransUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getRootCertPath() {
		return rootCertPath;
	}

	public void setRootCertPath(String rootCertPath) {
		this.rootCertPath = rootCertPath;
	}
	
}

package com.example.upop.sdk;

import static com.example.upop.sdk.SDKConstants.CERTTYPE_01;
import static com.example.upop.sdk.SDKConstants.CERTTYPE_02;
import static com.example.upop.sdk.SDKConstants.POINT;
import static com.example.upop.sdk.SDKConstants.SIGNMETHOD_RSA;
import static com.example.upop.sdk.SDKConstants.SIGNMETHOD_SHA256;
import static com.example.upop.sdk.SDKConstants.SIGNMETHOD_SM3;
import static com.example.upop.sdk.SDKConstants.VERSION_1_0_0;
import static com.example.upop.sdk.SDKConstants.VERSION_5_0_0;
import static com.example.upop.sdk.SDKConstants.VERSION_5_0_1;
import static com.example.upop.sdk.SDKConstants.VERSION_5_1_0;
import static com.example.upop.sdk.SDKConstants.param_signMethod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @ClassName SDKUtil
 * @Description acpsdk tools
 * @date 2016-7-22 下午4:06:18
 *
 */
public class SDKUtil {

	/**
	 * Three methods are provided for calculating signatures according to the value of signMethod.
	 * 
	 * @param data
	 *            For the data to be signed, its key in Map format is equal to value.
	 * @param encoding
	 *            Encoding
	 * @return Signing succeeds or not
	 */
	public static boolean sign(Map<String, String> data, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = data.get(param_signMethod);
		String version = data.get(SDKConstants.param_version);
		if (!VERSION_1_0_0.equals(version) && !VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		
		if (isEmpty(version)) {
			LogUtil.writeErrorLog("version must Not null");
			return false;
		}
		if (SIGNMETHOD_RSA.equals(signMethod)|| VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			if (VERSION_5_0_0.equals(version)|| VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				// Set the serial number of the signed certificate
				data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(data);
				LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// Summarize it via SHA1 and convert it into a hexadecimal one
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// Set the value of the signed domain
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// Set the serial number of the signed certificate
				data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(data);
				LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// Summarize it via SHA256 and convert it into a hexadecimal one
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// Set the value of the signed domain
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			}
		} else if (SIGNMETHOD_SHA256.equals(signMethod)) {
			return signBySecureKey(data, SDKConfig.getConfig()
					.getSecureKey(), encoding);
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			return signBySecureKey(data, SDKConfig.getConfig()
					.getSecureKey(), encoding);
		}
		return false;
	}

	/**
	 * Read the certificates to be signed, sign them, and return the signature values by means of the transferred certificate absolute paths and certificate passwords.<br>
	 * 
	 * @param data
	 *            For the data to be signed, its key in Map format is equal to value.
	 * @param encoding
	 *            Encoding
	 * @param certPath
	 *            Certificate absolute path
	 * @param certPwd
	 *            Certificate password
	 * @return Signature value
	 */
	public static boolean signBySecureKey(Map<String, String> data, String secureKey,
			String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(secureKey)) {
			LogUtil.writeErrorLog("secureKey is empty");
			return false;
		}
		String signMethod = data.get(param_signMethod);
		if (isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		
		if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// Convert Map information into the string “key1=value1&key2=value2”.
			String stringData = coverMap2String(data);
			LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			// Set the value of the signed domain
			data.put(SDKConstants.param_signature, strAfterSha256);
			return true;
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			String stringData = coverMap2String(data);
			LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(secureKey, encoding);
			String strAfterSM3 = SecureUtil.sm3X16Str(strBeforeSM3, encoding);
			// Set the value of the signed domain
			data.put(SDKConstants.param_signature, strAfterSM3);
			return true;
		}
		return false;
	}

	/**
	 * Sign and return the signature value by means of the transferred signature private key<br>
	 * 
	 * @param data
	 *            For the data to be signed, its key in Map format is equal to value.
	 * @param encoding
	 *            Encoding
	 * @param certPath
	 *            Certificate absolute path
	 * @param certPwd
	 *            Certificate password
	 * @return Signature value
	 */
	public static boolean signByCertInfo(Map<String, String> data,
			String certPath, String certPwd, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(certPath) || isEmpty(certPwd)) {
			LogUtil.writeErrorLog("CertPath or CertPwd is empty");
			return false;
		}
		String signMethod = data.get(param_signMethod);
		String version = data.get(SDKConstants.param_version);
		if (!VERSION_1_0_0.equals(version) && !VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		if (isEmpty(version)) {
			LogUtil.writeErrorLog("version must Not null");
			return false;
		}
		
		if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				// Set the serial number of the signed certificate
				data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(data);
				LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// Summarize it via SHA1 and convert it into a hexadecimal one
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// Set the value of the signed domain
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// Set the serial number of the signed certificate
				data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(data);
				LogUtil.writeLog("Request message string to be signed:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// Summarize it via SHA256 and convert it into a hexadecimal one
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// Set the value of the signed domain
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			}
			
		} 
		return false;
	}
	
	/**
	 * Signature authentication
	 * 
	 * @param resData
	 *            Returned message data
	 * @param encoding
	 *            Encoding format
	 * @return
	 */
	public static boolean validateBySecureKey(Map<String, String> resData, String secureKey, String encoding) {
		LogUtil.writeLog("Signature authentication starts");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(SDKConstants.param_signMethod);
		if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1. Conduct SHA256 authentication
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
			// Convert Map information into the string “key1=value1&key2=value2”.
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			return stringSign.equals(strAfterSha256);
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			// 1. Conduct SM3 authentication
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
			// Convert Map information into the string “key1=value1&key2=value2”.
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(secureKey, encoding);
			String strAfterSM3 = SecureUtil
					.sm3X16Str(strBeforeSM3, encoding);
			return stringSign.equals(strAfterSM3);
		}
		return false;
	}
	
	/**
	 * Signature authentication
	 * 
	 * @param resData
	 *            Returned message data
	 * @param encoding
	 *            Encoding format
	 * @return
	 */
	public static boolean validate(Map<String, String> resData, String encoding) {
		LogUtil.writeLog("Signature authentication starts");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(SDKConstants.param_signMethod);
		String version = resData.get(SDKConstants.param_version);
		if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			// Obtain the version number of the returned message
			if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				String stringSign = resData.get(SDKConstants.param_signature);
				LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
				// Obtain the certID from the returned message. Then, query the object of the corresponding certificate for verifying certificates in the static Map of certificates.
				String certId = resData.get(SDKConstants.param_certId);
				LogUtil.writeLog("Serial number of the certificate verification public key used to conduct certificate verification for returned messages:["+certId+"]");
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(resData);
				LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
				try {
					// The public key certificate issued to the client by China UnionPay is required in a signature authentication.
					return SecureUtil.validateSignBySoft(CertUtil
							.getValidatePublicKey(certId), SecureUtil
							.base64Decode(stringSign.getBytes(encoding)),
							SecureUtil.sha1X16(stringData, encoding));
				} catch (UnsupportedEncodingException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (Exception e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// 1. Obtain the public key information from the returned message and convert it into a public key object.
				String strCert = resData.get(SDKConstants.param_signPubKeyCert);
//				LogUtil.writeLog("Conduct signature authentication for public key certificates:["+strCert+"]");
				X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
				if(x509Cert == null) {
					LogUtil.writeErrorLog("convert signPubKeyCert failed");
					return false;
				}
				// 2. Authenticate the certificate chain.
				if (!CertUtil.verifyCertificate(x509Cert)) {
					LogUtil.writeErrorLog("Fail to authenticate the public key certificate and certificate information:["+strCert+"]");
					return false;
				}
				
				// 3. Conduct signature authentication
				String stringSign = resData.get(SDKConstants.param_signature);
				LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
				// Convert Map information into the string “key1=value1&key2=value2”.
				String stringData = coverMap2String(resData);
				LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
				try {
					// The public key certificate issued to the client by China UnionPay is required in a signature authentication.
					boolean result = SecureUtil.validateSignBySoft256(x509Cert
							.getPublicKey(), SecureUtil.base64Decode(stringSign
							.getBytes(encoding)), SecureUtil.sha256X16(
							stringData, encoding));
					LogUtil.writeLog("Signature authentication" + (result? "Succeed":"Fail"));
					return result;
				} catch (UnsupportedEncodingException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (Exception e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}

		} else if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1. Conduct SHA256 authentication
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
			// Convert Map information into the string “key1=value1&key2=value2”.
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(SDKConfig.getConfig()
							.getSecureKey(), encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			boolean result =  stringSign.equals(strAfterSha256);
			LogUtil.writeLog("Signature authentication" + (result? "Succeed":"Fail"));
			return result;
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			// 1. Conduct SM3 authentication
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("Original text of the signature:["+stringSign+"]");
			// Convert Map information into the string “key1=value1&key2=value2”.
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("Message strings to be signature-authenticated and returned:["+stringData+"]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(SDKConfig.getConfig()
							.getSecureKey(), encoding);
			String strAfterSM3 = SecureUtil
					.sm3X16Str(strBeforeSM3, encoding);
			boolean result =  stringSign.equals(strAfterSM3);
			LogUtil.writeLog("Signature authentication" + (result? "Succeed":"Fail"));
			return result;
		}
		return false;
	}

	/**
	 * Convert the data in Map into the string “key1=value1&key2=value2”, excluding the signature domain signature.
	 * 
	 * @param data
	 *            Map data to be merged
	 * @return Merged string
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (SDKConstants.param_signature.equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + SDKConstants.EQUAL + en.getValue()
					+ SDKConstants.AMPERSAND);
		}
		return sf.substring(0, sf.length() - 1);
	}


	/**
	 * This method is compatible with the old one, and can be used to convert a string like “key=value&key=value” into the corresponding Map object.
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	/**
	 * Convert a string like “key=value&key=value” into the corresponding Map object.
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map =null;
		try {

			if (StringUtils.isNotBlank(result)) {
				if (result.startsWith("{") && result.endsWith("}")) {
					result = result.substring(1, result.length() - 1);
				}
				map = parseQString(result);
			}

		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return map;
	}

	
	/**
	 * Parse the response string and generate a response element
	 * 
	 * @param str
	 *            String to be parsed
	 * @return Parsed resulting map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str)
			throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//Nesting exists in the value
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// Traverse the entire string to be parsed
				curChar = str.charAt(i);// Use the current string
				if (isKey) {// If what is generated currently is a key
					
					if (curChar == '=') {// If a separator “=” is read 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// If what is generated currently is a value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//If nesting has not been enabled
						if(curChar == '{'){//If the value of the original code is the content in ‘’, enable nesting
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					if (curChar == '&' && !isOpen) {// If a separator “&” is read and this separator is not a value domain, add this separator to Map.
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					}else{
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 
	 * Obtain the encryption public key and certificate in the response message and store them locally. Meanwhile, back up the original certificates.<br>
	 * Return “1” in case of a successful update, “0” in case of no update, and “-1” in case of a failure or exception.
	 * 
	 * @param resData
	 * @param encoding
	 * @return
	 */
	public static int getEncryptCert(Map<String, String> resData,
			String encoding) {
		String strCert = resData.get(SDKConstants.param_encryptPubKeyCert);
		String certType = resData.get(SDKConstants.param_certType);
		if (isEmpty(strCert) || isEmpty(certType)){
			return -1;}
		X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
		if (CERTTYPE_01.equals(certType)) {
			// Update the encryption public key for sensitive information
			if (!CertUtil.getEncryptCertId().equals(
					x509Cert.getSerialNumber().toString())) {
				// When the IDs are different, update the local certificate.
				String localCertPath = SDKConfig.getConfig().getEncryptCertPath();
				String newLocalCertPath = genBackupName(localCertPath);
				// 1. Back up and store the local certificate.
				if (!copyFile(localCertPath, newLocalCertPath)){
					return -1;}
				// 2. After the backup succeeds, update the encryption certificate.
				if (!writeFile(localCertPath, strCert, encoding)){
					return -1;}
				LogUtil.writeLog("save new encryptPubKeyCert success");
				CertUtil.resetEncryptCertPublicKey();
				return 1;
			}else {
				return 0;
			}

		} else if (CERTTYPE_02.equals(certType)) {
//			// Update the encryption public key for magnetic tracks
//			if (!CertUtil.getEncryptTrackCertId().equals(
//					x509Cert.getSerialNumber().toString())) {
//				// When the IDs are different, update the local certificate.
//				String localCertPath = SDKConfig.getConfig().getEncryptTrackCertPath();
//				String newLocalCertPath = genBackupName(localCertPath);
//				// 1. Back up and store the local certificate.
//				if (!copyFile(localCertPath, newLocalCertPath))
//					return -1;
//				// 2. After the backup succeeds, update the encryption certificate.
//				if (!writeFile(localCertPath, strCert, encoding))
//					return -1;
//				LogUtil.writeLog("save new encryptPubKeyCert success");
//				CertUtil.resetEncryptTrackCertPublicKey();
//				return 1;
//			}else {
				return 0;
//			}
		}else {
			LogUtil.writeLog("unknown cerType:"+certType);
			return -1;
		}
	}
	
	/**
	 * Method for copying files
	 * 
	 * @param srcFile
	 *            Source file
	 * @param destFile
	 *            Destination file
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(String srcFile, String destFile) {
		boolean flag = false;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// Obtain the input/output stream of the source/destination file
			fin = new FileInputStream(srcFile);
			fout = new FileOutputStream(destFile);
			// Obtain the input/output channel
			fcin = fin.getChannel();
			fcout = fout.getChannel();
			// Create a buffer
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// Create another buffer by using clear method so that the read data can be accepted.
				buffer.clear();
				// Read the data from the input channel to the buffer.
				int r = fcin.read(buffer);
				// The read method will return the number of read bytes, which may be 0. It returns “-1” if the channel has reached the end of the stream.
				if (r == -1) {
					flag = true;
					break;
				}
				// By using flip method, a buffer can write the newly-read data to another channel.
				buffer.flip();
				// Write the data from the output channel to a buffer.
				fcout.write(buffer);
			}
			fout.flush();
		} catch (IOException e) {
			LogUtil.writeErrorLog("CopyFile fail", e);
		} finally {
			try {
				if (null != fin){
					fin.close();}
				if (null != fout){
					fout.close();}
				if (null != fcin){
					fcin.close();}
				if (null != fcout){
					fcout.close();}
			} catch (IOException ex) {
				LogUtil.writeErrorLog("Releases any system resources fail", ex);
			}
		}
		return flag;
	}
	
	/**
	 * Method for writing files
	 * 
	 * @param filePath
	 *            File path
	 * @param fileContent
	 *            File content
	 * @param encoding
	 *            Encoding
	 * @return
	 */
	public static boolean writeFile(String filePath, String fileContent,
			String encoding) {
		FileOutputStream fout = null;
		FileChannel fcout = null;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		
		try {
			fout = new FileOutputStream(filePath);
			// Obtain the output channel
			fcout = fout.getChannel();
			// Create a buffer
			// ByteBuffer buffer = ByteBuffer.allocate(1024);
			ByteBuffer buffer = ByteBuffer.wrap(fileContent.getBytes(encoding));
			fcout.write(buffer);
			fout.flush();
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("WriteFile fail", e);
			return false;
		} catch (IOException ex) {
			LogUtil.writeErrorLog("WriteFile fail", ex);
			return false;
		} finally {
			try {
				if (null != fout){
					fout.close();}
				if (null != fcout){
					fcout.close();}
			} catch (IOException ex) {
				LogUtil.writeErrorLog("Releases any system resources fail", ex);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Rename the transferred file <br>
	 * The result is: xxx_backup.cer
	 * 
	 * @param fileName
	 * @return
	 */
	public static String genBackupName(String fileName) {
		if (isEmpty(fileName)){
			return "";}
		int i = fileName.lastIndexOf(POINT);
		String leftFileName = fileName.substring(0, i);
		String rightFileName = fileName.substring(i + 1);
		String newFileName = leftFileName + "_backup" + POINT + rightFileName;
		return newFileName;
	}
	

	public static byte[] readFileByNIO(String filePath) {
		FileInputStream in = null;
		FileChannel fc = null;
		ByteBuffer bf = null;
		try {
			in = new FileInputStream(filePath);
			fc = in.getChannel();
			bf = ByteBuffer.allocate((int) fc.size());
			fc.read(bf);
			return bf.array();
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage());
			return null;
		} finally {
			try {
				if (null != fc) {
					fc.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
				LogUtil.writeErrorLog(e.getMessage());
				return null;
			}
		}
	}
	
	/**
	 * Filter the null strings in the request message or null strings
	 * @param contentData
	 * @return
	 */
	public static Map<String, String> filterBlank(Map<String, String> contentData){
		LogUtil.writeLog("Print the request message domain:");
		Map<String, String> submitFromData = new HashMap<String, String>();
		Set<String> keyset = contentData.keySet();
		
		for(String key:keyset){
			String value = contentData.get(key);
			if (StringUtils.isNotBlank(value)) {
				// Conduct trimming for the value.
				submitFromData.put(key, value.trim());
				LogUtil.writeLog(key + "-->" + String.valueOf(value));
			}
		}
		return submitFromData;
	}
	
	/**
	 * Decompress
	 * 
	 * @param inputByte
	 *            Data of byte[] array type
	 * @return Decompressed data
	 * @throws IOException
	 */
	public static byte[] inflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Inflater compresser = new Inflater(false);
		compresser.setInput(inputByte, 0, inputByte.length);
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.inflate(result);
				if (compressedDataLength == 0) {
					break;
				}
				o.write(result, 0, compressedDataLength);
			}
		} catch (Exception ex) {
			System.err.println("Data format error!\n");
			ex.printStackTrace();
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}

	/**
	 * 压缩.
	 * 
	 * @param inputByte
	 *            Byte[] arrays to be decompressed
	 * @return Compressed data
	 * @throws IOException
	 */
	public static byte[] deflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Deflater compresser = new Deflater();
		compresser.setInput(inputByte);
		compresser.finish();
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.deflate(result);
				o.write(result, 0, compressedDataLength);
			}
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}
	
	/**
	 * Judge whether there is a NULL string or there is no string.
	 * 
	 * @param s
	 *            String data to be judged
	 * @return Result: true-yes; false-no
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim());
	}
	
}

package com.example.upop.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName AcpService
 * @Description acpsdkInterface service class: To integrated access clients, directly refer to the methods in this class.
 * @date 2016-7-22 下午2:44:37
 */
public class AcpService {

	/**
	 * Request message signature (use the private key certificate configured in the configuration file or the symmetric key signature.)<br>
	 * Function: To sign request messages, and to calculate and assign values for such fields as certid and signature, and to return the result.<br>
	 * @param reqData: Request message map<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return　map object after the signature<br>
	 */
	public static Map<String, String> sign(Map<String, String> reqData,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.sign(reqData, encoding);
		return reqData;
	}
	
	/**
	 * Same as signByCertInfo.<br>
	 * @param reqData
	 * @param certPath
	 * @param certPwd
	 * @param encoding
	 * @return
	 * @deprecated
	 */
	public static Map<String, String> sign(Map<String, String> reqData, String certPath, 
			String certPwd,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signByCertInfo(reqData,certPath,certPwd,encoding);
		return reqData;
	}
	
	/**
	 * Multi-certificate signature (by transferring the path, password, and signature of the private key certificate)<br>
	 * Function: In scenarios where multiple clients access UnionPay and each client number corresponds to different certificates, this method can be used to transfer the private key certificate and its password (and to set acpsdk.singleMode to false in acp_sdk.properties).<br>
	 * @param reqData: Request message map<br>
	 * @param certPath: Private key file of the signature (with a path)<br>
	 * @param certPwd: Private key password of the signature<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return　map object after the signature<br>
	 */
	public static Map<String, String> signByCertInfo(Map<String, String> reqData, String certPath, 
			String certPwd,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signByCertInfo(reqData,certPath,certPwd,encoding);
		return reqData;
	}
	
	/**
	 * Multi-key signature (by transferring the key and signature)<br>
	 * Function: In scenarios where multiple clients access UnionPay and each client number corresponds to different certificates, this method can be used to transfer the private key certificate and its password (and to set acpsdk.singleMode to false in acp_sdk.properties).<br>
	 * @param reqData: Request message map<br>
	 * @param secureKey: Symmetric key of the signature<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return　map object after the signature<br>
	 */
	public static Map<String, String> signBySecureKey(Map<String, String> reqData, String secureKey, String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signBySecureKey(reqData, secureKey, encoding);
		return reqData;
	}
	
	/**
	 * Signature authentication (SHA-1 digest algorithm)<br>
	 * @param resData: Returned message data<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return True: pass; false: fail<br>
	 */
	public static boolean validate(Map<String, String> rspData, String encoding) {
		return SDKUtil.validate(rspData, encoding);
	}
	
	/**
	 * Multi-key signature authentication (by transferring the key and signature)<br>
	 * @param resData: Returned message data<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return True: pass; false: fail<br>
	 */
	public static boolean validateBySecureKey(Map<String, String> rspData, String secureKey, String encoding) {
		return SDKUtil.validateBySecureKey(rspData, secureKey, encoding);
	}
	

	/**
	 * @deprecated This method has been deleted in 5.1.0 Software Development Kit (SDK), please directly refer to the VerifyAppData.java in 5.1.0 SDK for signature authentication.
	 * Conduct signature authentication for the data domain in the message returned after a successful control payment (the response message obtained from the control)<br>
	 * @param Data in format of jsonData json, for example:{"sign" : "J6rPLClQ64szrdXCOtV1ccOMzUmpiOKllp9cseBuRqJ71pBKPPkZ1FallzW18gyP7CvKh1RxfNNJ66AyXNMFJi1OSOsteAAFjF5GZp0Xsfm3LeHaN3j/N7p86k3B1GrSPvSnSw1LqnYuIBmebBkC1OD0Qi7qaYUJosyA1E8Ld8oGRZT5RR2gLGBoiAVraDiz9sci5zwQcLtmfpT5KFk/eTy4+W9SsC0M/2sVj43R9ePENlEvF8UpmZBqakyg5FO8+JMBz3kZ4fwnutI5pWPdYIWdVrloBpOa+N4pzhVRKD4eWJ0CoiD+joMS7+C0aPIEymYFLBNYQCjM0KV7N726LA==",  "data" : "pay_result=success&tn=201602141008032671528&cert_id=68759585097"}
	 * @return Succeed or not
	 */
	public static boolean validateAppResponse(String jsonData, String encoding) {
		LogUtil.writeLog("Signature authentication for control response message starts:[" + jsonData + "]");
		if (SDKUtil.isEmpty(encoding)) {
			encoding = "UTF-8";
		}

        Pattern p = Pattern.compile("\\s*\"sign\"\\s*:\\s*\"([^\"]*)\"\\s*");
		Matcher m = p.matcher(jsonData);
		if(!m.find()) return false;
		String sign = m.group(1);

		p = Pattern.compile("\\s*\"data\"\\s*:\\s*\"([^\"]*)\"\\s*");
		m = p.matcher(jsonData);
		if(!m.find()) return false;
		String data = m.group(1);

		p = Pattern.compile("cert_id=(\\d*)");
		m = p.matcher(jsonData);
		if(!m.find()) return false;
		String certId = m.group(1);

		try {
			// The public key certificate issued to the client by China UnionPay is required in a signature authentication.
			return SecureUtil.validateSignBySoft(CertUtil
					.getValidatePublicKey(certId), SecureUtil.base64Decode(sign
					.getBytes(encoding)), SecureUtil.sha1X16(data,
					encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Function: To submit request messages and receive synchronous response messages in background transactions.<br>
	 * @param reqData: Request message<br>
	 * @param rspData: Response message<br>
	 * @param reqUrl: Request address<br>
	 * @param encoding<br>
	 * @return Return “true” if http 200 is responded. Otherwise, return “false”.<br>
	 */
	public static Map<String,String> post(
			Map<String, String> reqData,String reqUrl,String encoding) {
		Map<String, String> rspData = new HashMap<String,String>();
		LogUtil.writeLog("Request the UnionPay address:" + reqUrl);
		//Send the data requested by the background.
		HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// Convert the returned result into map.
					Map<String,String> tmpRspData  = SDKUtil.convertResultStringToMap(resultString);
					rspData.putAll(tmpRspData);
				}
			}else{
				LogUtil.writeLog("If the http state code ["+status+"] is returned, please check whether the request message or request address is correct.");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return rspData;
	}
	
	/**
	 * Function: A method used to get the http and used in convenience-for-people payment products.<br>
	 * @param reqUrl: Request address<br>
	 * @param encoding<br>
	 * @return
	 */
	public static String get(String reqUrl,String encoding) {
		
		LogUtil.writeLog("Request the UnionPay address:" + reqUrl);
		//Send the data requested by the background.
		HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.sendGet(encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					return resultString;
				}
			}else{
				LogUtil.writeLog("If the http state code ["+status+"] is returned, please check whether the request message or request address is correct.");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * Function: To construct HTTP POST for foreground transactions and to automatically submit forms.<br>
	 * @param action: address for submitting forms<br>
	 * @param hiddens: Form key values stored in form of MAP<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>
	 * @return Transaction forms for constructed HTTP POST<br>
	 */
	public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	
	/**
	 * Function: To use the DEFLATE compression algorithm to compress the content of a batch of file, and to generate and return a string by means of Base64 encoding.<br>
	 * Applicable transactions: Batch paying, batch collecting, and batch refunding<br>
	 * @param filePath: Full-path file name of batch file<br>
	 * @return
	 */
	public static String enCodeFileContent(String filePath,String encoding){
		String baseFileContent = "";
		
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int fl = in.available();
			if (null != in) {
				byte[] s = new byte[fl];
				in.read(s, 0, fl);
				// Compression and encoding.
				baseFileContent = new String(SecureUtil.base64Encode(SDKUtil.deflater(s)),encoding);
			}
		} catch (Exception e) {
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
		return baseFileContent;
	}
	
	/**
	 * Function: To parse the fileContent string returned by a transaction and locate it (conduct Base64 decoding for the string and decompress it by using the DEFLATE compression algorithm. Then, locate it)<br>
	 * Applicable transactions: Downloading of reconciliation files, querying of batch transaction state<br>
	 * @param resData: Returned message map<br>
	 * @param fileDirectory: Directory of the located file (absolute path)
	 * @param encoding: Send the value of the field encoding in the request message domain<br>	
	 */
	public static String deCodeFileContent(Map<String, String> resData,String fileDirectory,String encoding) {
		// Parse returned files
		String filePath = null;
		String fileContent = resData.get(SDKConstants.param_fileContent);
		if (null != fileContent && !"".equals(fileContent)) {
			FileOutputStream out = null;
			try {
				byte[] fileArray = SDKUtil.inflater(SecureUtil
						.base64Decode(fileContent.getBytes(encoding)));
				if (SDKUtil.isEmpty(resData.get("fileName"))) {
					filePath = fileDirectory + File.separator + resData.get("merId")
							+ "_" + resData.get("batchNo") + "_"
							+ resData.get("txnTime") + ".txt";
				} else {
					filePath = fileDirectory + File.separator + resData.get("fileName");
				}
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
			    out = new FileOutputStream(file);
				out.write(fileArray, 0, fileArray.length);
				out.flush();
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}

	/**
	 * Function: To convert the content of the result file into a plain text string: conduct Base64 decoding for the string and then decompress it.<br>
	 * Applicable transactions: Querying of batch transaction state<br>
	 * @param fileContent: The content of the file returned after a query of batch transaction state.<br>
	 * @return Plain text content<br>
	 */
	public static String getFileContent(String fileContent,String encoding){
		String fc = "";
		try {
			fc = new String(SDKUtil.inflater(SecureUtil.base64Decode(fileContent.getBytes())),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return fc;
	}
	
	
	/**
	 * Function: To construct the information domain customerInfo of the cardholder<br>
	 * Note: If the right to encrypt sensitive information, the old method will be used to construct the customerInfo domain and the sensitive information will not be encrypted (such fields as phoneNo, cvn2, and expired will not be encrypted). The sensitive information, however, will be encrypted if a pin is to be sent.<br>
	 * @param Request parameter of the customerInfoMap information domain. A domain name will be sent if it is set to key and a value will be sent if it is set to value. Mandatory.<br>
	 *        For example:customerInfoMap.put("certifTp", "01");					//Certificate type<br>
				  customerInfoMap.put("certifId", "341126197709218366");	//Certificate number<br>
				  customerInfoMap.put("customerNm", "Internet");				//Name<br>
				  customerInfoMap.put("phoneNo", "13552535506");			//Phone number<br>
				  customerInfoMap.put("smsCode", "123456");					//SMS authentication code<br>
				  customerInfoMap.put("pin", "111111");						//Password (encrypted)<br>
				  customerInfoMap.put("cvn2", "123");           			//The three digits corresponding to cvn2 at the back of the card (not encrypt)<br>
				  customerInfoMap.put("expired", "1711");  				    //Expiry date: The year should come before the month (not encrypt)<br>
	 * @param accNo: If a PIN has been sent for customerInfoMap, then the card number must be sent. If no PIN has been sent for customerInfoMap, then you can choose not to send this field.<br>
	 * @param encoding: Send the value of the field encoding in the request message domain<br>				  
	 * @return Cardholder information domain field after a Base64 encoding<br>
	 */
	public static String getCustomerInfo(Map<String,String> customerInfoMap,String accNo,String encoding) {
		
		if(customerInfoMap.isEmpty())
			return "{}";
		StringBuffer sf = new StringBuffer("{");
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("pin")){
				if(null == accNo || "".equals(accNo.trim())){
					LogUtil.writeLog("If a PIN has been sent, then the card number must be uploaded in the getCustomerInfo parameter.");
					throw new RuntimeException("If you have encrypted the PIN but sent no card number, the subsequent treatment cannot be conducted.");
				}else{
					value = encryptPin(accNo,value,encoding);
				}
			}
			sf.append(key).append(SDKConstants.EQUAL).append(value);
			if(it.hasNext())
				sf.append(SDKConstants.AMPERSAND);
		}
		String customerInfo = sf.append("}").toString();
		LogUtil.writeLog("Assembled plain text customerInfo:"+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return customerInfo;
	}
	
	/**
	 * Function: To construct the cardholder information domain customerInfo. If you have checked the right to encrypt the sensitive information, you can encrypt such fields as PIN, phoneNo, cvn2, and expired according to the new encryption specifications. <br>
	 * Applicable transactions: <br>
	 * @param Request parameter of the customerInfoMap information domain. A domain name will be sent if it is set to key and a value will be sent if it is set to value. Mandatory. <br>
	 *        For example:customerInfoMap.put("certifTp", "01");					//Certificate type <br>
				  customerInfoMap.put("certifId", "341126197709218366");	//Certificate number <br>
				  customerInfoMap.put("customerNm", "Internet");				//Name <br>
				  customerInfoMap.put("smsCode", "123456");					//SMS authentication code <br>
				  customerInfoMap.put("pin", "111111");						//Password (encrypted) <br>
				  customerInfoMap.put("phoneNo", "13552535506");			//Phone number (encrypted) <br>
				  customerInfoMap.put("cvn2", "123");           			//The three digits corresponding to cvn2 at the back of the card (encrypt) <br>
				  customerInfoMap.put("expired", "1711");  				    //Expiry date: The year should come before the month (encrypt) <br>
	 * @param accNo: If a PIN has been sent for customerInfoMap, then the card number must be sent. If no PIN has been sent for customerInfoMap, then you can choose not to send this field.<br>
	 * @param encoding: Send the value of the field encoding in the request message domain
	 * @return Cardholder information domain field after a Base64 encoding <br>
	 */
	public static String getCustomerInfoWithEncrypt(Map<String,String> customerInfoMap,String accNo,String encoding) {
		if(customerInfoMap.isEmpty())
			return "{}";
		StringBuffer sf = new StringBuffer("{");
		//Encryption domain for sensitive information
		StringBuffer encryptedInfoSb = new StringBuffer("");
		
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("phoneNo") || key.equals("cvn2") || key.equals("expired")){
				encryptedInfoSb.append(key).append(SDKConstants.EQUAL).append(value).append(SDKConstants.AMPERSAND);
			}else{
				if(key.equals("pin")){
					if(null == accNo || "".equals(accNo.trim())){
						LogUtil.writeLog("If a PIN has been sent, then the card number must be uploaded in the getCustomerInfoWithEncrypt parameter.");
						throw new RuntimeException("If you have encrypted the PIN but sent no card number, the subsequent treatment cannot be conducted.");
					}else{
						value = encryptPin(accNo,value,encoding);
					}
				}
				sf.append(key).append(SDKConstants.EQUAL).append(value).append(SDKConstants.AMPERSAND);
			}
		}
		
		if(!encryptedInfoSb.toString().equals("")){
			encryptedInfoSb.setLength(encryptedInfoSb.length()-1);//Delete the last symbol “&”
			LogUtil.writeLog("Assembled plain textcustomerInfo encryptedInfo:"+ encryptedInfoSb.toString());
			sf.append("encryptedInfo").append(SDKConstants.EQUAL).append(encryptData(encryptedInfoSb.toString(), encoding));
		}else{
			sf.setLength(sf.length()-1);
		}
		
		String customerInfo = sf.append("}").toString();
		LogUtil.writeLog("Assembled plain text customerInfo:"+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(encoding)),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return customerInfo;
	}
	
	/**
	 * Parse and return the customerInfo domain in the message (background notification):<br>
	 * Conduct Base64 decoding for the string. If the right to encrypt the sensitive information is checked for encryptedInfo, the sensitive information will be decrypted and then the domain in encryptedInfo will be put into customerInfoMap and be returned.<br>
	 * @param customerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String,String> parseCustomerInfo(String customerInfo,String encoding){
		Map<String,String> customerInfoMap = null;
		try {
				byte[] b = SecureUtil.base64Decode(customerInfo.getBytes(encoding));
				String customerInfoNoBase64 = new String(b,encoding);
				LogUtil.writeLog("after a Base64 decoding has been conducted for the string===>" +customerInfoNoBase64);
				//Delete the fore-and-aft {}
				customerInfoNoBase64 = customerInfoNoBase64.substring(1, customerInfoNoBase64.length()-1);
				customerInfoMap = SDKUtil.parseQString(customerInfoNoBase64);
				if(customerInfoMap.containsKey("encryptedInfo")){
					String encInfoStr = customerInfoMap.get("encryptedInfo");
					customerInfoMap.remove("encryptedInfo");
					String encryptedInfoStr = decryptData(encInfoStr, encoding);
					Map<String,String> encryptedInfoMap = SDKUtil.parseQString(encryptedInfoStr);
					customerInfoMap.putAll(encryptedInfoMap);
				}
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		return customerInfoMap;
	}
	
	/**
	 * Parse and return the customerInfo domain in the message (background notification):<br>
	 * Conduct Base64 decoding for the string. If the right to encrypt the sensitive information is checked for encryptedInfo, the sensitive information will be decrypted and then the domain in encryptedInfo will be put into customerInfoMap and be returned.<br>
	 * @param customerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String,String> parseCustomerInfo(String customerInfo, String certPath, 
			String certPwd, String encoding){
		Map<String,String> customerInfoMap = null;
		try {
				byte[] b = SecureUtil.base64Decode(customerInfo.getBytes(encoding));
				String customerInfoNoBase64 = new String(b,encoding);
				LogUtil.writeLog("after a Base64 decoding has been conducted for the string===>" +customerInfoNoBase64);
				//Delete the fore-and-aft {}
				customerInfoNoBase64 = customerInfoNoBase64.substring(1, customerInfoNoBase64.length()-1);
				customerInfoMap = SDKUtil.parseQString(customerInfoNoBase64);
				if(customerInfoMap.containsKey("encryptedInfo")){
					String encInfoStr = customerInfoMap.get("encryptedInfo");
					customerInfoMap.remove("encryptedInfo");
					String encryptedInfoStr = decryptData(encInfoStr, certPath, certPwd, encoding);
					Map<String,String> encryptedInfoMap = SDKUtil.parseQString(encryptedInfoStr);
					customerInfoMap.putAll(encryptedInfoMap);
				}
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		return customerInfoMap;
	}

	/**
	 * Encrypt the password and conduct a Base64 encoding<br>
	 * @param accNo: Account number<br>
	 * @param pwd: Password<br>
	 * @param encoding<br>
	 * @return Encrypted content<br>
	 */
	public static String encryptPin(String accNo, String pin, String encoding) {
		return SecureUtil.encryptPin(accNo, pin, encoding, CertUtil
				.getEncryptCertPublicKey());
	}
	
	/**
	 * Encrypt the sensitive information and conduct a Base64 encoding (card number, phone number, cvn2, expiry date)<br>
	 * @param phoneNo, cvn2, and expiry date will be sent in case of data.<br>
	 * @param encoding<br>
	 * @return Encrypted ciphertext<br>
	 */
	public static String encryptData(String data, String encoding) {
		return SecureUtil.encryptData(data, encoding, CertUtil
				.getEncryptCertPublicKey());
	}
	
	/**
	 * Decrypt the sensitive information by using the configuration file acp_sdk.properties<br>
	 * @param base64EncryptedInfo: Encrypted information<br>
	 * @param encoding<br>
	 * @return Decrypted plain text<br>
	 */
	public static String decryptData(String base64EncryptedInfo, String encoding) {
		return SecureUtil.decryptData(base64EncryptedInfo, encoding, CertUtil
				.getSignCertPrivateKey());
	}
	
	/**
	 * Decrypt the sensitive information b using the transferred private key.<br>
	 * @param base64EncryptedInfo: Encrypted information<br>
	 * @param certPath: Private key file (with a full path)<br>
	 * @param certPwd: Private key password<br>
	 * @param encoding<br>
	 * @return
	 */
	public static String decryptData(String base64EncryptedInfo, String certPath, 
			String certPwd, String encoding) {
		return SecureUtil.decryptData(base64EncryptedInfo, encoding, CertUtil
				.getSignCertPrivateKeyByStoreMap(certPath, certPwd));
	}

	/**
	 * Use the 5.0.0 interface instead of the 5.1.0 interface to encrypt magnetic track information.<br>
	 * @param trackData: Magnetic track data to be encrypted<br>
	 * @param encoding: Encoding format<br>
	 * @return Encrypted ciphertext<br>
	 * @deprecated
	 */
	public static String encryptTrack(String trackData, String encoding) {
		return SecureUtil.encryptData(trackData, encoding,
				CertUtil.getEncryptTrackPublicKey());
	}
	
	/**
	 * Obtain the physical serial number of the encryption certificate for sensitive information<br>
	 * @return
	 */
	public static String getEncryptCertId(){
		return CertUtil.getEncryptCertId();
	}
	
	/**
	 * Conduct a Base64 encoding for the string<br>
	 * @param rawStr<br>
	 * @param encoding<br>
	 * @return<br>
	 * @throws IOException
	 */
	public static String base64Encode(String rawStr,String encoding) throws IOException{
		byte [] rawByte = rawStr.getBytes(encoding);
		return new String(SecureUtil.base64Encode(rawByte),encoding);
	}
	/**
	 * Decode the string for which a Base64 encoding has been conducted<br>
	 * @param base64Str<br>
	 * @param encoding<br>
	 * @return<br>
	 * @throws IOException
	 */
	public static String base64Decode(String base64Str,String encoding) throws IOException{
		byte [] rawByte = base64Str.getBytes(encoding);
		return new String(SecureUtil.base64Decode(rawByte),encoding);	
	}


	/**
	 * 
	 * Construct information domain for card transactions (cardTransData)<br>
	 * Use “{}” to encompass all subdomains and the symbol “&” to connect two subdomains. The format is as follows: {subdomain name 1=value&subdomain name 2-value&subdomain name 3=value}<br>
	 * Note: This example is only for reference. Please assemble according to the message elements in the interface document in actual development.<br>
	 * 
	 * @param The data of cardTransDataMap cardTransData<br>
	 * @param requestData must include merId, orderId, txnTime, and txnAmt, which are required in encryption of magnetic tracks.<br>
	 * @param encoding: Encoding<br>
	 * @return
	 */
	public static String getCardTransData(Map<String, String> cardTransDataMap, 
			Map<String, String> requestData,
			String encoding) { {

		StringBuffer cardTransDataBuffer = new StringBuffer();
		
		if(cardTransDataMap.containsKey("track2Data")){
			StringBuffer track2Buffer = new StringBuffer();
			track2Buffer.append(requestData.get("merId"))
					.append(SDKConstants.COLON).append(requestData.get("orderId"))
					.append(SDKConstants.COLON).append(requestData.get("txnTime"))
					.append(SDKConstants.COLON).append(requestData.get("txnAmt")==null?0:requestData.get("txnAmt"))
					.append(SDKConstants.COLON).append(cardTransDataMap.get("track2Data"));
			cardTransDataMap.put("track2Data", 
					AcpService.encryptData(track2Buffer.toString(),	encoding));
		}
		
		if(cardTransDataMap.containsKey("track3Data")){
			StringBuffer track3Buffer = new StringBuffer();
			track3Buffer.append(requestData.get("merId"))
				.append(SDKConstants.COLON).append(requestData.get("orderId"))
				.append(SDKConstants.COLON).append(requestData.get("txnTime"))
				.append(SDKConstants.COLON).append(requestData.get("txnAmt")==null?0:requestData.get("txnAmt"))
				.append(SDKConstants.COLON).append(cardTransDataMap.get("track3Data"));
			cardTransDataMap.put("track3Data", 
					AcpService.encryptData(track3Buffer.toString(),	encoding));
		}

		return cardTransDataBuffer.append(SDKConstants.LEFT_BRACE)
				.append(SDKUtil.coverMap2String(cardTransDataMap))
				.append(SDKConstants.RIGHT_BRACE).toString();
		}
	
	}
	
	/**
	 * Obtain the encryption public key and certificate in the response message and store them locally. Back up the original certificates and automatically replace certificates.<br>
	 * Return “1” in case of a successful update, “0” in case of no update, and “-1” in case of a failure or exception.<br>
	 * @param resData: Returned message
	 * @param encoding
	 * @return
	 */
	public static int updateEncryptCert(Map<String, String> resData,
			String encoding) {
		return SDKUtil.getEncryptCert(resData, encoding);
	}
	
	/**
	 * Obtain
	 * @param number
	 * @return
	 */
	public static int genLuhn(String number){
		return SecureUtil.genLuhn(number);
	}
}

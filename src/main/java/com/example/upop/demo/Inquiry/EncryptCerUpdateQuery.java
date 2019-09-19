package com.example.upop.demo.Inquiry;

import com.example.upop.demo.DemoBase;
import com.example.upop.sdk.AcpService;
import com.example.upop.sdk.LogUtil;
import com.example.upop.sdk.SDKConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Update and query of UnionPay encryption public key (this transaction is applicable only to the scenario where an RSA certificate is used for encryption, that is, signMethod=01, not to scenarios where signMethod=11 and signMethod=12).
 * The client regularly (daily) initiates a transaction to obtain the information about the encryption public key for the UnionPay all-channel system.
 * After this transaction succeeds, the file specified by acpsdk.encryptCert.path in the configuration file acp_sdk.properties will be replaced automatically, needing no manual operation.
 */
public class EncryptCerUpdateQuery {

	public static void main(String[] args) {
		//Load the content of the acp_sdk.properties file under classpath.
		SDKConfig.getConfig().loadPropertiesFromSrc();
		
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("version", DemoBase.version);                  		     //Version number
		contentData.put("encoding", DemoBase.encoding);            		 //Character set code: Both UTF-8 and GBK can be used.
		contentData.put("signMethod", SDKConfig.getConfig().getSignMethod());    //Signature method 01: RSA certificate  11: Hashed authentication SHA-256 supported  12: Hashed authentication SM3 supported
		contentData.put("txnType", "95");                              			 //Transaction type 95 - Update and query UnionPay encryption public key
		contentData.put("txnSubType", "00");                           			 //Transaction subtype  00 by default
		contentData.put("bizType", "000201");                          			 //Business type  default
		contentData.put("channelType", "07");                          			 //Channel type
		
		contentData.put("certType", "01");							   			     //01: Encryption public key for sensitive information (only 01 can be used)
		contentData.put("merId", "777290058110048");                   		 //Client number (client number 777290058110097 is used only for transaction test and debugging and this client has configured to encrypt sensitive information). In the test, please modify this client number to the one you have applied [the test client number starting with 777 you have registered does not support product collecting]
		contentData.put("accessType", "1");                            			    
		contentData.put("orderId", DemoBase.getOrderId());             	 //Client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed	
		contentData.put("txnTime", DemoBase.getCurrentTime());         //Order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.                         //Account number type
		
		Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			   //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();				 			       //At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding);  //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.

		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, DemoBase.encoding)){
				LogUtil.writeLog("Signature authentication succeeds");
				String respCode = rspData.get("respCode") ;
				if(("00").equals(respCode)){
					int resultCode = AcpService.updateEncryptCert(rspData,"UTF-8");
					if (resultCode == 1) {
						LogUtil.writeLog("Encryption public key updated successfully");
					} else if (resultCode == 0) {
						LogUtil.writeLog("Encryption public key not updated");
					} else {
						LogUtil.writeLog("Encryption public key fails to be updated");
					}
					
				}else{
					//Other response codes are failure. Please find the cause.
					//TODO
				}
			}else{
				LogUtil.writeErrorLog("Signature authentication fails");
				//TODO Find the reason why the signature authentication fails
			}
		}else{
			//The returned http state code is incorrect.
			LogUtil.writeErrorLog("No returned message is obtained or the returned http state code is not 200.");
		}
	}
}

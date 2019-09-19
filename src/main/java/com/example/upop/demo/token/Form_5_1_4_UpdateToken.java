package com.example.upop.demo.token;

import com.example.upop.demo.DemoBase;
import com.example.upop.sdk.AcpService;
import com.example.upop.sdk.CertUtil;
import com.example.upop.sdk.LogUtil;
import com.example.upop.sdk.SDKConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * Update token transaction
 * 
 *
 */
public class Form_5_1_4_UpdateToken  extends HttpServlet  {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String merId = req.getParameter("merId");
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		String token = req.getParameter("token");
		
		Map<String, String> contentData = new HashMap<String, String>();

		/***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
		contentData.put("version", DemoBase.version);                  //M version number
		contentData.put("encoding", DemoBase.encoding);           //M character set code: Both UTF-8 and GBK can be used.
		contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M signature method
		// Value: 79
		contentData.put("txnType", "79");// M
		// Value: 03
		contentData.put("txnSubType", "03");// M
		// Product type
		contentData.put("bizType", "000201");// M
		// 0: Merchant direct access
		// 1: Acquirer Access
		contentData.put("accessType", "1");// M
		// Channel type
		contentData.put("channelType", "07");// M
		// Acquirer Code (Applied if access type is acquirer access)
		contentData.put("acqInsCode", "00010000");//C
		// Merchant Category (Applied if access type is acquirer access)
		contentData.put("merCatCode", "");// C
		// Merchant Name (Applied if access type is acquirer access)
		contentData.put("merName", "");// C
		// Merchant Abbreviation (Applied if access type is acquirer access)
		contentData.put("merAbbr", "");// C
		
		contentData.put("merId", merId);                   			   //M client number (this client number is used only for transaction test and debugging and this client has configured to encrypt sensitive information). In the test, please modify this client number to the one you have applied [the test client number starting with 777 you have registered does not support product collecting]
		
		contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed	
		
		contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
		
		//Support only credit cards. Mandatory: Card number, phone number, CVN2, and expiry date. The authentication code depends on the service configuration (by default, no SMS authentication code is required. For the test client 777290058110097, the configuration is that an SMS authentication code is required).
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		//customerInfoMap.put("certifTp", "01");						//Certificate type
		//customerInfoMap.put("certifId", "341126197709218366");		//Certificate number
		//customerInfoMap.put("customerNm", "All-channel");					//Name
		customerInfoMap.put("phoneNo", "13552535506");			        //Phone number
		customerInfoMap.put("cvn2", "123");           			        //The three digits corresponding to cvn2 at the back of the card
		customerInfoMap.put("expired", "1711");  				        //Expiry date: The year should come before the month.
		customerInfoMap.put("smsCode", "111111");  				        //SMS authentication code: Always fill in 111111 in test environment.
		
		contentData.put("encryptCertId",CertUtil.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
		String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,DemoBase.encoding);
		
		contentData.put("customerInfo", customerInfoStr); //M
		//token number (obtain from the background notification provisioned from the foreground or obtain from the returned message provisioned from the background)
		contentData.put("tokenPayData", "{token="+token+"&trId=62000000001&tokenType=01}");
		
		/**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
		Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			   //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   						   //At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding);   //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
		
		/**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
		//For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
		StringBuffer parseStr = new StringBuffer("");
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, DemoBase.encoding)){
				LogUtil.writeLog("Signature authentication succeeds");
				String respCode = rspData.get("respCode") ;
				if(("00").equals(respCode)){
					//Succeed
					parseStr.append("<br>To parse encrypted sensitive information (if any), do as follows:<br>");
					String customerInfo = rspData.get("customerInfo");
					if(null!=customerInfo){
						Map<String,String>  cm = AcpService.parseCustomerInfo(customerInfo, "UTF-8");
						parseStr.append("Plain text customerInfo: " + cm+"<br>");
					}
					//If you have configured encryption for sensitive information, you can use the method below to decrypt the card number to obtain the plain text card number.
					String an = rspData.get("accNo");
					if(null!=an){
						an = AcpService.decryptData(an, "UTF-8");
						parseStr.append("Plain text accNo: " + an);
					}
					//TODO
				}else{
					//Other response codes are failure. Please find the cause or treat it as a failure.
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
		String reqMessage = DemoBase.genHtmlResult(reqData);
		String rspMessage = DemoBase.genHtmlResult(rspData);
		resp.getWriter().write("Request message:<br/>"+reqMessage+"<br/>" + "Response message:</br>"+rspMessage+parseStr);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}

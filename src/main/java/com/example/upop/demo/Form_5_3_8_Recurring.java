package com.example.upop.demo;

import com.example.upop.sdk.AcpService;
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
 * Recurring
 * 
 *
 */
public class Form_5_3_8_Recurring  extends HttpServlet  {

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
		String txnAmt = req.getParameter("txnAmt");
		Map<String, String> contentData = new HashMap<String, String>();

		/***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
		contentData.put("version", DemoBase.version);                  //M version number
		contentData.put("encoding", DemoBase.encoding);           //M character set code: Both UTF-8 and GBK can be used.
		contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M signature method
		// Value: 11
		contentData.put("txnType", "11");// M
		// 00: Default
		contentData.put("txnSubType", "00");// M
		// 000501: Recurring
		contentData.put("bizType", "000201");// M
		// 0: Merchant direct access
		// 1: Acquirer Access
		// 2: Platform merchant access
		contentData.put("accessType", "1");// M
		// 07: Internet
		// 08: Mobile
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
		contentData.put("currencyCode", "156");					   //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
		contentData.put("txnAmt", txnAmt);						   //M transaction amount: in cents, without any decimal point.
	
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.

		contentData.put("backUrl", DemoBase.backUrl);


		/**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
		Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
		
		/**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
		//For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
		StringBuffer parseStr = new StringBuffer("");
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, DemoBase.encoding)){
				LogUtil.writeLog("Signature authentication succeeds");
				String respCode = rspData.get("respCode") ;
				if(("00").equals(respCode)){
					//Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to update the order state after receiving the background notification, or actively initiate a transaction query to determine the transaction state.
					//TODO

				}else if(("03").equals(respCode)||
						 ("04").equals(respCode)||
						 ("05").equals(respCode)){
					//Also, you can initiate a transaction state query later to determine the transaction state.
					//TODO
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

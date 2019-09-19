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
 * Purchase transaction
 * 
 *
 */
public class Form_5_3_1_Purchase  extends HttpServlet  {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String merId = req.getParameter("merId");
		String IIN = req.getParameter("IIN");
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		String txnAmt = req.getParameter("txnAmt");
		Map<String, String> contentData = new HashMap<String, String>();

		/***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
		// Constant value: 5.1.0
		contentData.put("version", DemoBase.version);                  //M
		// Default value: UTF-8
		contentData.put("encoding", DemoBase.encoding);           //M
		// Value: 01 (RSA)
		contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
		// Value: 01
		contentData.put("txnType", "01");                              //M
		// 01: Purchase, to differentiate the front-end purchase or back-end purchase through transaction request URL
		// 02: MOTO
		//05: Purchase with authentication (Applied to Product type 000301)
		contentData.put("txnSubType", "01");                           //Transaction sub-type 01-purchase
		//000301: Merchant-hosted
		//000000: ExpressPay
		//000902: Token payment
		//001001: MOTO
		contentData.put("bizType", "000201");         //M      	
		// 0: Merchant direct access
		// 1: Acquirer Access
		// 2: Platform merchant access
		contentData.put("accessType", "1");// M
		// 07: Internet
		// 08: Mobile
		contentData.put("channelType", "07");// M
		// Acquirer Code (Applied if access type is acquirer access)
		contentData.put("acqInsCode", IIN);//C
		// Merchant Category (Applied if access type is acquirer access)
		contentData.put("merCatCode", "5411");// C
		// Merchant Name (Applied if access type is acquirer access)
		contentData.put("merName", "UPI Test Merchant");// C
		// Merchant Abbreviation (Applied if access type is acquirer access)
		contentData.put("merAbbr", "UPI Test");// C
		// Merchant ID
		contentData.put("merId", merId);                   			  //M
		// Merchant order No
		contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed	
		// Date and time when merchant sends transaction
		contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
		// Default value is 156.
		contentData.put("currencyCode", "156");						   //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
		// The unit of transaction amount is cent.
		contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.
	
		//Consumption: The transaction element card number and authentication code depend on the service configuration (by default, an SMS authentication code is required).
		//Map<String,String> customerInfoMap = new HashMap<String,String>();
		//customerInfoMap.put("smsCode", "111111");			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
		
		////////////If the client has enabled the right [encrypt sensitive information by the client], you need to encrypt accNo, pin, phoneNo, cvn2, and expired (if these fields will be sent later) for encryption of sensitive information.
		//String accNo = AcpService.encryptData("6216261000000000018", DemoBase.encoding);  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
		//contentData.put("accNo", accNo);
		//contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
		//String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,"6216261000000000018",DemoBase.encoding);

		//contentData.put("customerInfo", customerInfoStr);//M
		
		contentData.put("payTimeout", "");// O
		
		contentData.put("backUrl", DemoBase.backUrl);
		
		contentData.put("frontUrl", DemoBase.frontUrl);

		/**All request parameters have been set. Now, sign the request parameters and generate an html form. Then, write the form to the browser and jump to and open the UnionPay page.**/
		Map<String, String> submitFromData = AcpService.sign(contentData,DemoBase.encoding);  //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.

		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //Obtain the requested foreground UnionPay address: acpsdk.frontTransUrl in the corresponding property file acp_sdk.properties
		String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,DemoBase.encoding);   //Generate an html form which can jump to UnionPay page automatically
		
		LogUtil.writeLog("Print the request HTML, which is a request message and the basis for problem joint debugging and troubleshooting"+html);
		//Write the generated html to the browser to automatically jump to and open the UnionPay payment page. Here, do not modify the names and values of the form items in the html after calling signData or before writing the html to the browser. Such modification may cause failure of the signature authentication.
		resp.getWriter().write(html);
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}

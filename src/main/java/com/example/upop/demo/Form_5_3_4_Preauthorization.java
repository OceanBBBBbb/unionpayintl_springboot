package com.example.upop.demo;

import com.example.upop.sdk.AcpService;
import com.example.upop.sdk.LogUtil;
import com.example.upop.sdk.SDKConfig;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Pre-authorization
 * 
 *
 */
public class Form_5_3_4_Preauthorization extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/**
		 * Initialize the parameters related to requesting UnionPay access address as well as obtaining certificate files and certificate paths to the SDKConfig class.
		 * Be sure to load such parameters each time you run them in manner of java main.
		 * In case of Web application development, this method can be written into cache by means of monitoring, and does not need to appear here.
		 */
		//Here, the method for loading a property file has been moved to web/AutoLoadServlet.java.
		//SDKConfig.getConfig().loadPropertiesFromSrc(); //Load the acp_sdk.properties file from classpath
		
		//It is transferred from a foreground page.
		String merId = req.getParameter("merId");
		String txnAmt = req.getParameter("txnAmt");
		String IIN = req.getParameter("IIN");
		
		Map<String, String> requestData = new HashMap<String, String>();
		
		/***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
		requestData.put("version", DemoBase.version);   			  //M
		requestData.put("encoding", DemoBase.encoding); 			  //M
		requestData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
		//01: Pre-authorization
		//02: MOTO pre-authorization
		requestData.put("txnType", "02");               			  //M
		requestData.put("txnSubType", "01");            			  //M
		//000301: Merchant-hosted
		//000000: ExpressPay
		//000902: Token payment
		//001001: MOTO
		requestData.put("bizType", "000201");           			  //M
		//07: Internet
		//08: Mobile
		requestData.put("channelType", "07");           			  //M
		//0: Merchant direct access
		//1: Acquirer access
		//2: Platform merchant access
		requestData.put("accessType", "1");                        
		// Acquirer Code (Applied if access type is acquirer access)
		requestData.put("acqInsCode", IIN);//C
		// Merchant Category (Applied if access type is acquirer access)
		requestData.put("merCatCode", "4511");// C
		// Merchant Name (Applied if access type is acquirer access)
		requestData.put("merName", "UPI Test Merchant");// C
		// Merchant Abbreviation (Applied if access type is acquirer access)
		requestData.put("merAbbr", "UPI Test");// C
		
		requestData.put("merId", merId);    	          			  //M client number: Please modify it to the formal client number you have applied or the test client number 777 you have registered at open.unionpay.com.
		requestData.put("orderId",DemoBase.getOrderId());             //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed		
		requestData.put("txnTime", DemoBase.getCurrentTime());        //M order delivery time: It must be system time in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
		requestData.put("currencyCode", "156");         			  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)		
		requestData.put("txnAmt", txnAmt);             			      //M transaction amount: in cents, without any decimal point.

		requestData.put("frontUrl", DemoBase.frontUrl);   //C
		

		requestData.put("backUrl", DemoBase.backUrl);//M
		

		requestData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 * 1000));
	
		
		/**All request parameters have been set. Now, sign the request parameters and generate an html form. Then, write the form to the browser and jump to and open the UnionPay page.**/
		Map<String, String> submitFromData = AcpService.sign(requestData,DemoBase.encoding);  //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.

		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //Obtain the requested foreground UnionPay address: acpsdk.frontTransUrl in the corresponding property file acp_sdk.properties
		String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,DemoBase.encoding);   //Generate an html form which can jump to UnionPay page automatically
		
		LogUtil.writeLog("Print the request HTML, which is a request message and the basis for problem joint debugging and troubleshooting"+html);
		//Write the generated html to the browser to automatically jump to and open the UnionPay payment page. Here, do not modify the names and values of the form items in the html after calling signData or before writing the html to the browser. Such modification may cause failure of the signature authentication.
		resp.getWriter().write(html);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doPost(req, resp);
	}
}

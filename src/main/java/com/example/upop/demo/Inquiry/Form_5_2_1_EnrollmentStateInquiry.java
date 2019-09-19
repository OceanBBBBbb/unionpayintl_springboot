package com.example.upop.demo.Inquiry;

import com.example.upop.demo.DemoBase;
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
 * Enrollment state inquiry transaction
 * 
 *
 */
public class Form_5_2_1_EnrollmentStateInquiry  extends HttpServlet  {

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
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
		data.put("version", DemoBase.version);                 //M
		data.put("encoding", DemoBase.encoding);               //M
		data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
		// Value: 78
		data.put("txnType", "78");// M
		// 00: PAN query (default)
		// 01: Mobile number query (Merchant direct access mode)
		// 02: Merchant order number query
		//.11: SecurePlus enrollment state inquiry according to ordernumber
		data.put("txnSubType", "00");// M
		// Product type
		data.put("bizType", "000201");// M
		// 0: Merchant direct access
		// 1: Acquirer Access
		data.put("accessType", "1");// M
		// 07: Internet
		// 08: Mobile
		data.put("channelType", "07");// M
		// Acquirer Code (Applied if access type is acquirer access)
		data.put("acqInsCode", "00010000");//C
		// Merchant Category (Applied if access type is acquirer access)
		data.put("merCatCode", "");// C
		// Merchant Name (Applied if access type is acquirer access)
		data.put("merName", "");// C
		// Merchant Abbreviation (Applied if access type is acquirer access)
		data.put("merAbbr", "");// C
		
		data.put("merId", merId);                  			   //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.
		
		/***To debug a transaction so that it runs properly, you must modify the fields below.***/
		data.put("orderId", orderId);                 //M**** client order number: It should be modified to the order number of the transaction queried for each transaction test.
		data.put("txnTime", txnTime);                 //C  （Required if Transaction query number is not submitted） ****Order delivery time: It should be modified to the order delivery time of the transaction queried for each transaction test.
		// Card No.
		data.put("accNo", "6250946000000016");// C （Required when transaction subtype is 00.）
		/**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.------------->**/
		
		Map<String, String> reqData = AcpService.sign(data,DemoBase.encoding);           //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
		String url = SDKConfig.getConfig().getSingleQueryUrl();						          //At the url of the transaction request, you can read the acpsdk.singleQueryUrl in the corresponding property file acp_sdk.properties from the configuration file.
		Map<String, String> rspData = AcpService.post(reqData,url,DemoBase.encoding);     //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
		
		/**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
		//For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, DemoBase.encoding)){
				LogUtil.writeLog("Signature authentication succeeds");
				if(("00").equals(rspData.get("respCode"))){//If the transaction query succeeds,
					String origRespCode = rspData.get("origRespCode");
					if(("00").equals(origRespCode)){
						//Update the state of the client order if the transaction succeeds.
						//TODO
					}else if(("03").equals(origRespCode)||
							 ("04").equals(origRespCode)||
							 ("05").equals(origRespCode)){
						//The order is being processed or the transaction state is unknown. In this case, you need to initiate a transaction state query later. [If you cannot determine whether the transaction is successful at the end, please use the transaction state in the reconciliation file.]
						//TODO
					}else{
						//Other response codes are transaction failure.
						//TODO
					}
				}else if(("34").equals(rspData.get("respCode"))){
					//The order does not exist. In this case, you can initiate a transaction state query later, or use the transaction state in the reconciliation file.
					
				}else{//If the transaction query fails and the response code is 10/11, you need to check whether the message is correct.
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
		resp.getWriter().write("Enrollment state inquiry</br>Request :<br/>"+reqMessage+"<br/>" + "Response:</br>"+rspMessage+"");								    //Write the generated html to the browser to automatically jump to and open the UnionPay payment page. Here, do not modify the names and values of the form items in the html after calling signData or before writing the html to the browser. Such modification may cause failure of the signature authentication.

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}

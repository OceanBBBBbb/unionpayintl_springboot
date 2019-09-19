package com.example.upop.demo;

import com.example.upop.sdk.AcpService;
import com.example.upop.sdk.LogUtil;
import com.example.upop.sdk.SDKConstants;
import com.example.upop.sdk.SDKUtil;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Important: Be sure to read the comments carefully before conducting an alignment test!
 * 
 * Product:UPOP<br>
 * Function: To give examples of how to receive and process background notifications <br>
 * Date:  2015-09<br>
 * Version:  1.0.0 
 * Copyright:  China UnionPay<br>
 * Note: The following codes are only example codes provided to facilitate clients to conduct tests, and each client can compile these codes according to the technical documents as required. These codes are for reference only.<br>
 * Transaction description: 	A background notification is sent only after a foreground transaction has ended successfully. A notification will be sent after the ending of a background transaction (an interface for background notification is available) no matter whether it succeeds or fails.
 * 			For the purpose of security, you are recommended to query the interface to confirm that the transaction is successful after receiving a notification for a transaction with capital involved. For a transaction with no capital involved, you can determine that it is successful if the respCode of the notification interface is 00.
 * 			In case no notification is received, you can query the time point when the interface is called by referring to this FAQ.https://open.unionpay.com/ajweb/help/faq/list?id=77&level=0&from=0
 */
public class BackRcvResponse extends HttpServlet{

	@Override
	public void init() throws ServletException {
		/**
		 * Initialize the parameters related to requesting UnionPay access address as well as obtaining certificate files and certificate paths to the SDKConfig class.
		 * Be sure to load such parameters each time you run them in manner of java main.
		 * In case of Web application development, this method can be written into cache by means of monitoring, and does not need to appear here.
		 */
		//Here, the method for loading a property file has been moved to web/AutoLoadServlet.java.
		//SDKConfig.getConfig().loadPropertiesFromSrc(); //Load the acp_sdk.properties file from classpath
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		LogUtil.writeLog("BackRcvResponse starts to receive background notifications.");

		String encoding = req.getParameter(SDKConstants.param_encoding);
		// Obtain the background notification parameters sent by the UnionPay notification server.
		Map<String, String> reqParam = getAllRequestParam(req);

		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				
				valideData.put(key, value);
			}
		}

		//Important! Do not modify the content of the key-value pair in reqParam before signature authentication. Otherwise, the signature authentication may fail.
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("Result of signature authentication [Fail].");
			//The signature authentication fails and the problem should be solved.
			
		} else {
			LogUtil.writeLog("Result of signature authentication [Succeed].");
			//Update the state of the client order if the transaction succeeds.
			
			String orderId =valideData.get("orderId"); //Obtain the data of background notifications and you can obtain other fields similarly.
			String customerInfo = valideData.get("customerInfo");
			if(null!=customerInfo){
				Map<String,String>  customerInfoMap = AcpService.parseCustomerInfo(customerInfo, "UTF-8");
				LogUtil.writeLog("Plain text customerInfoMap: "+ customerInfoMap);
			}
			
			String accNo = valideData.get("accNo");
			//If an encryption certificate has been configured for sensitive information, you can use the method below for decryption. No decryption is required if the sensitive has not been encrypted.
			if(null!=accNo){
				accNo = AcpService.decryptData(accNo, "UTF-8");
				LogUtil.writeLog("Plain text accNo: "+ accNo);
			}
			
			String tokenPayData = valideData.get("tokenPayData");
			if(null!=tokenPayData){
				Map<String,String> tokenPayDataMap = SDKUtil.parseQString(tokenPayData.substring(1, tokenPayData.length() - 1));
				String token = tokenPayDataMap.get("token");//Obtain
				LogUtil.writeLog("Plain text tokenPayDataMap: " + tokenPayDataMap);
			}
			
			String respCode = valideData.get("respCode");
			//After determining that respCode=00 or A6, you are recommended to query the interface and update the database after confirming that the transaction is successful for a transaction with capital involved.
		}
		LogUtil.writeLog("BackRcvResponse ends to receive background notifications.");
		//Return the status code http 200 to the UnionPay server
		resp.getWriter().print("ok");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	/**
	 * Obtain all the information in the requested parameters
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//When sending messages, do not send the content below if the value of the field is null: What should be done next is deleting this field if you determine that its value is null when obtaining all parameter data.
				//System.out.println("Line 247 of the ServletUtil class  key=="+en+"      and value ==="+value of temp data );
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
}

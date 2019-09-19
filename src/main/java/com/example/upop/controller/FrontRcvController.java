package com.example.upop.controller;

import com.example.upop.demo.DemoBase;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author binhy
 * @desc TODO
 * @date 2019/9/18
 */
@RestController
@RequestMapping("/frontRcvResponse")
public class FrontRcvController {

  @PostMapping
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    LogUtil.writeLog("FrontRcvResponse: The foreground receives a message and returns “Start”.");

    String encoding = req.getParameter(SDKConstants.param_encoding);
    LogUtil.writeLog("in the returned message encoding=[" + encoding + "]");
    String pageResult = "";
    if (DemoBase.encoding.equalsIgnoreCase(encoding)) {
      pageResult = "/utf8_result.jsp";
    } else {
      pageResult = "/gbk_result.jsp";
    }
    Map<String, String> respParam = getAllRequestParam(req);

    // Print the request message
    LogUtil.printRequestLog(respParam);

    Map<String, String> valideData = null;
    StringBuffer page = new StringBuffer();
    if (null != respParam && !respParam.isEmpty()) {
      Iterator<Entry<String, String>> it = respParam.entrySet()
          .iterator();
      valideData = new HashMap<String, String>(respParam.size());
      while (it.hasNext()) {
        Entry<String, String> e = it.next();
        String key = (String) e.getKey();
        String value = (String) e.getValue();

        page.append("<tr><td width=\"30%\" align=\"right\">" + key
            + "(" + key + ")</td><td>" + value + "</td></tr>");
        valideData.put(key, value);
      }
    }
    if (!AcpService.validate(valideData, encoding)) {
      page.append("<tr><td width=\"30%\" align=\"right\">Result of signature authentication</td><td>Fail</td></tr>");
      LogUtil.writeLog("Result of signature authentication [Fail].");
    } else {
      page.append("<tr><td width=\"30%\" align=\"right\">Result of signature authentication</td><td>Succeed</td></tr>");
      LogUtil.writeLog("Result of signature authentication [Succeed].");
      System.out.println(valideData.get("orderId")); //Other fields can be obtained similarly.

      //To obtain a transaction with a token number, you need to parse the tokenPayData domain.
      String customerInfo = valideData.get("customerInfo");
      if(null!=customerInfo){
        Map<String,String>  customerInfoMap = AcpService.parseCustomerInfo(customerInfo, "UTF-8");
        page.append("Plain text customerInfo: "+customerInfoMap + "<br>");
      }

//			String accNo = valideData.get("accNo");
//			//If the returned card number is a ciphertext one, you can use the method below for decryption. This step can be neglected if the returned card number is not a ciphertext one.
//			if(null!= accNo){
//				accNo = AcpService.decryptData(accNo, "UTF-8");
//				page.append("<br>Plain text accNo: "+accNo);
//			}

      String tokenPayData = valideData.get("tokenPayData");
      if(null!=tokenPayData){
        Map<String,String> tokenPayDataMap = SDKUtil.parseQString(tokenPayData.substring(1, tokenPayData.length() - 1));
        String token = tokenPayDataMap.get("token");//Obtain
        page.append("Plain text tokenPayDataMap: " + tokenPayDataMap + "<br>");
      }

      String respCode = valideData.get("respCode");
      //After determining that respCode=00 or A6, you are recommended to query the interface and update the database after confirming that the transaction is successful for a transaction with capital involved.

    }
    req.setAttribute("result", page.toString());
    req.getRequestDispatcher(pageResult).forward(req, resp);

    LogUtil.writeLog("FrontRcvResponse: The foreground receives a message and returns “End”.");
  }

  @GetMapping
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    //Get the address of the foreground notification in case of provisioning failure.
    doPost(req,resp);
    resp.getWriter().write("Provisioning failure");
  }

  /**
   * Obtain all the information in the requested parameters
   *
   * @param request
   * @return
   */
  public static Map<String, String> getAllRequestParam(
      final HttpServletRequest request) {
    Map<String, String> res = new HashMap<String, String>();
    Enumeration<?> temp = request.getParameterNames();
    if (null != temp) {
      while (temp.hasMoreElements()) {
        String en = (String) temp.nextElement();
        String value = request.getParameter(en);
        res.put(en, value);
        // When sending messages, do not send the content below if the value of the field is null: What should be done next is deleting this field if you determine that its value is null when obtaining all parameter data.
        if (res.get(en) == null || "".equals(res.get(en))) {
          // System.out.println("======A null field name===="+en);
          res.remove(en);
        }
      }
    }
    return res;
  }
}

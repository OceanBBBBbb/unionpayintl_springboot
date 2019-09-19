package com.example.upop.demo;

import com.example.upop.sdk.SDKConfig;
import com.example.upop.sdk.SDKConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Name:  Method used in demo<br>
 * Date:  2015-09<br>
 * Version:  1.0.0 
 * Copyright:  China UnionPay<br>
 * Note: The following codes are only example codes provided to facilitate clients to conduct tests, and each client can compile these codes according to the technical documents as required. These codes are for reference only.<br>
 */
public class DemoBase {

	//By default, UTF-8 is configured.
	public static String encoding = "UTF-8";

	//All-channel fixed value
	public static String version = SDKConfig.getConfig().getVersion();
	
	//For the compilation corresponding to a foreground service, refer to FrontRcvResponse.java.
	public static String frontUrl = SDKConfig.getConfig().getFrontUrl();

	//For the compilation corresponding to a background service, refer to BackRcvResponse.java.
	public static String backUrl = SDKConfig.getConfig().getBackUrl();//An optional domain for the acquirer and issuer [O]--background notification address

	// Format of transaction time to be used by the client: YYYYMMDDhhmmss
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	// AN8..40 Client order number, no “-” or “_” is allowed.
	public static String getOrderId() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
   /**
	 * Assembly request, and the returned message string is used to dispaly
	 * @param data
	 * @return
	 */
    public static String genHtmlResult(Map<String, String> data){

    	TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			String key = en.getKey(); 
			String value =  en.getValue();
			if("respCode".equals(key)){
				sf.append("<b>"+key + SDKConstants.EQUAL + value+"</br></b>");
			}else
				sf.append(key + SDKConstants.EQUAL + value+"</br>");
		}
		return sf.toString();
    }
    /**
	 * Function: To parse the ZM reconciliation files of omni-channel clients and return a List<Map>
	 * Applicable transaction: To view a downloaded reconciliation file
	 * @param filePath: Full path of a ZM file
	 * @return: A map sequence which contains the S/N and value of each transaction
	 */
	public static List<Map> parseZMFile(String filePath){
		int lengthArray[] = {3,11,11,6,10,19,12,4,2,21,2,32,2,6,10,13,13,4,15,2,2,6,2,4,32,1,21,15,1,15,32,13,13,8,32,13,13,12,2,1,32,98};
		return parseFile(filePath,lengthArray);
	}
	
	/**
	 * Function: To parse the ZME reconciliation files of omni-channel clients and return a List<Map>
	 * Applicable transaction: To view a downloaded reconciliation file
	 * @param filePath: Full path of a ZME file
	 * @return: A map sequence which contains the S/N and value of each transaction
	 */
	public static List<Map> parseZMEFile(String filePath){
		int lengthArray[] = {3,11,11,6,10,19,12,4,2,2,6,10,4,12,13,13,15,15,1,12,2,135};
		return parseFile(filePath,lengthArray);
	}
	
	/**
	 * Function: To parse ZM and ZME reconciliation files of omni-channel clients
	 * @param filePath
	 * @param lengthArray: For the length of the created int array in this type of format, refer to Section 6.1 “AM Files” and Section 6.2 “ZME Files” in “Reconciliation Files of Omni-Channel Clients” in  Access Interface Specifications of All-Channel Platform, Part 3 File Interface.
	 * @return
	 */
	private static List<Map> parseFile(String filePath,int lengthArray[]){
	 	List<Map> ZmDataList = new ArrayList<Map>();
	 	try {
            String encoding="gbk"; //The file adopts gbk codes
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //Determine whether the file exists
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file), "iso-8859-1");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	byte[] bs = lineTxt.getBytes("iso-8859-1");
                	//The parse result is a MAP, where key is the S/N of the reconciliation file and value is the parse value.
        		 	Map<Integer,String> ZmDataMap = new LinkedHashMap<Integer,String>();
                    //Left cursor
                    int leftIndex = 0;
                    //Right cursor
                    int rightIndex = 0;
                    for(int i=0;i<lengthArray.length;i++){
                    	rightIndex = leftIndex + lengthArray[i];
                    	String filed = new String(Arrays.copyOfRange(bs, leftIndex,rightIndex), encoding);
                    	leftIndex = rightIndex+1;
                    	ZmDataMap.put(i, filed);
                    }
                    ZmDataList.add(ZmDataMap);
                }
                read.close();
        }else{
            System.out.println("Fail to find the specified file");
        }
        } catch (Exception e) {
            System.out.println("An error occurs when reading the file");
            e.printStackTrace();
        }
	 	
		return ZmDataList;	
	}
	 
    public static String getFileContentTable(List<Map> dataList,String file){
    	StringBuffer  tableSb = new StringBuffer("For the specifications for reconciliation files, refer to Product Interface Specifications > Platform Interface Specifications: File Interface </br> File ["+file + "] at https://open.unionpay.com/ajweb/help/file/. The parsed content is as follows:");
    	tableSb.append("<table border=\"1\">");
    	if(dataList.size() > 0){
    		Map<Integer,String> dataMapTmp = dataList.get(0);
    		tableSb.append("<tr>");
	 		for(Iterator<Integer> it = dataMapTmp.keySet().iterator();it.hasNext();){
	 			Integer key = it.next();
	 			String value = dataMapTmp.get(key);
		 		System.out.println("S/N:"+ (key+1) + " Value: '"+ value +"'");
		 		tableSb.append("<td>S/N"+(key+1)+"</td>");
		 	}
	 		tableSb.append("</tr>");
    	}
    	
    	for(int i=0;i<dataList.size();i++){
	 		System.out.println("Number of lines: "+ (i+1));
	 		Map<Integer,String> dataMapTmp = dataList.get(i);
	 		tableSb.append("<tr>");
	 		for(Iterator<Integer> it = dataMapTmp.keySet().iterator();it.hasNext();){
	 			Integer key = it.next();
	 			String value = dataMapTmp.get(key);
		 		System.out.println("S/N:"+ (key+1) + " Value: '"+ value +"'");
		 		tableSb.append("<td>"+value+"</td>");
		 	}
	 		tableSb.append("</tr>");
	 	}
    	tableSb.append("</table>");
    	return tableSb.toString();
    }

	
	public static List<String> unzip(String zipFilePath,String outPutDirectory){
		List<String> fileList = new ArrayList<String>();
		try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFilePath));//Input the source zip path  
            BufferedInputStream bin = new BufferedInputStream(zin);
            BufferedOutputStream bout = null;
            File file=null;  
            ZipEntry entry;
            try {
                while((entry = zin.getNextEntry())!=null && !entry.isDirectory()){
                	file = new File(outPutDirectory,entry.getName());  
                    if(!file.exists()){  
                        (new File(file.getParent())).mkdirs();  
                    }
                    bout = new BufferedOutputStream(new FileOutputStream(file));  
                    int b;
                    while((b=bin.read())!=-1){  
                    	bout.write(b);  
                    }
                    bout.flush();
                    fileList.add(file.getAbsolutePath());
                    System.out.println(file+"Unzipping succeeds");
                }
            } catch (IOException e) {  
                e.printStackTrace();  
            }finally{
                try {
					bin.close();
					zin.close();
					if(bout!=null){
						bout.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}  
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        }
		return fileList;
	}

}

package com.example.upop.config;

import com.example.upop.sdk.SDKConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class AutoLoadServlet extends HttpServlet {
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		SDKConfig.getConfig().loadPropertiesFromSrc();// Load the acp_sdk.properties file from classpath
		super.init();
	}
}

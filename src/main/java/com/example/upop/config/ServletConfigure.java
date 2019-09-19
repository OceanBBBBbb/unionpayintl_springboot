package com.example.upop.config;

/**
 * @author binhy
 * @desc TODO
 * @date 2019/9/18
 */

import com.example.upop.sdk.SDKConfig;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfigure {

  /**
   * 代码注册servlet
   */
  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    SDKConfig.getConfig().loadPropertiesFromSrc();// 实际是这句起的作用
    return new ServletRegistrationBean(new AutoLoadServlet(), "/autoLoadServlet");// ServletName默认值为首字母小写，即myServlet1
  }

//    /**
//     * 多个servlet就注册多个bean
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean servletRegistrationBean1() {
//        return new ServletRegistrationBean(new MyServlet(), "/servlet/myServlet");// ServletName默认值为首字母小写，即myServlet
//    }


}

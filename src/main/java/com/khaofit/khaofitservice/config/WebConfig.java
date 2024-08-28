package com.khaofit.khaofitservice.config;

import com.khaofit.khaofitservice.utilities.IpRateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web Config rate limit implementation.
 *
 * @author kousik manik.
 */
@Configuration
public class WebConfig {

  /**
   * Web Config bean creation for ratelimiter.
   */
  @Bean
  public FilterRegistrationBean<IpRateLimitFilter> rateLimitFilter() {
    FilterRegistrationBean<IpRateLimitFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new IpRateLimitFilter());
    registrationBean.addUrlPatterns(
        "/auth/*",
        "/abha-address/*",
        "/register-aadhar/*",
        "/abha-login/*",
        "/profile/*"
    );
    return registrationBean;
  }
}

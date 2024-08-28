package com.khaofit.khaofitservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter that validates the host of incoming requests against a predefined set of allowed hosts.
 * If the host is not allowed, the filter will send a 403 Forbidden response with a detailed error message.
 * This filter is used to prevent unauthorized hosts from accessing the application.
 * It checks the "Host" header of the HTTP request and matches it against a pattern of allowed hosts.
 * If the host does not match the allowed pattern, an error response is returned and the request is not processed further.
 *
 * @author kousik manik
 */
public class HostValidationFilter extends GenericFilterBean {

  private final RequestMatcher requestMatcher;

  public HostValidationFilter(RequestMatcher requestMatcher) {
    this.requestMatcher = requestMatcher;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    if (!requestMatcher.matches(httpRequest)) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
      httpResponse.setContentType("application/json");

      JSONObject errorDetails = new JSONObject();
      errorDetails.put("error", "Forbidden host");
      errorDetails.put("message", "Access denied for the host: " + httpRequest.getHeader("Host"));
      errorDetails.put("status", HttpServletResponse.SC_FORBIDDEN);

      try (PrintWriter writer = httpResponse.getWriter()) {
        writer.write(errorDetails.toString());
      }

      logger.error("Host validation failed, sending 403 Forbidden: " + errorDetails.toString());
      return; // Stop further processing
    }

    // logger.info("Host validation passed");
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // Cleanup code if necessary
  }
}
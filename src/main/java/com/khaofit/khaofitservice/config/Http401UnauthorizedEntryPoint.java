package com.khaofit.khaofitservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Exception handler for authentication errors.
 *
 * @author Kousik Manik.
 */
@Component
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

  private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);

  /**
   * Always returns a 401 error code to the client.
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
      throws IOException {

    log.debug("Pre-authenticated entry point called. Rejecting access");
    response.sendError(
        HttpServletResponse.SC_UNAUTHORIZED,
        arg2.getMessage() != null ? arg2.getMessage() : "Access Denied"
    );
  }

}

package com.khaofit.khaofitservice.config;


import com.khaofit.khaofitservice.utilities.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Request Host validation.
 *
 * @author Kousik Manik.
 */
@Slf4j
public class ApprovedHostsRequestMatcher implements RequestMatcher {
  private final Pattern allowedHostPattern = Pattern.compile(
      "^(localhost:9090|khaofir-service\\.onrender\\.com)$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public boolean matches(HttpServletRequest request) {
    String host = request.getHeader("Host").trim();
    if (!StringUtils.isNotNullAndNotEmpty(host)) {
      log.error("Host header is empty or null");
      return false;
    }
    boolean isHostAllowed = allowedHostPattern.matcher(host).matches();
    if (!isHostAllowed) {
      log.error("host=[{}] is not allowed", host);
    }
    return isHostAllowed;
  }
}
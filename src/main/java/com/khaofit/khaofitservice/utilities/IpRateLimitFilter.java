package com.khaofit.khaofitservice.utilities;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Ip Rate limit filter.
 *
 * @author Praveen.
 */
@Component
@Slf4j
public class IpRateLimitFilter implements Filter {

  private Bucket bucket;

  private static final int REQUEST_PER_MINUTE = 120;
  private static final int DURATION_IN_MINUTES = 1;

  /**
   * Initialize Bucket.
   */
  public IpRateLimitFilter() {
    Refill refill = Refill.intervally(REQUEST_PER_MINUTE, Duration.ofMinutes(DURATION_IN_MINUTES));
    Bandwidth limit = Bandwidth.classic(REQUEST_PER_MINUTE, refill);
    this.bucket = Bucket.builder()
        .addLimit(limit)
        .build();
  }

  /**
   * Rate limit filter.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String ipAddress = request.getRemoteAddr();
    //    log.info("Rate limit check for ip=[{}], host=[{}], local-ip=[{}]",
    //        ipAddress,
    //        request.getRemoteHost(),
    //        request.getLocalAddr()
    //    );
    if (bucket.tryConsume(1)) {
      chain.doFilter(request, response);
    } else {
      log.info("Rate limit exceeded for ip=[{}], host=[{}], local-ip=[{}]",
          ipAddress,
          request.getRemoteHost(),
          request.getLocalAddr()
      );
      response.getWriter().write("Rate limit exceeded for this IP address.");
      ((HttpServletResponse) response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }
  }
}

package com.khaofit.khaofitservice.utilities;

import com.khaofit.khaofitservice.dto.jwt.JwtPayloadDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for checking the auth request token.
 *
 * @author Praveen
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtAuthUtils jwtAuthUtils;

  public JwtTokenFilter(JwtAuthUtils jwtAuthUtils) {
    this.jwtAuthUtils = jwtAuthUtils;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws IOException, ServletException {
    try {
      // Get authorization header and validate
      final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (header == null || !header.startsWith("Bearer ")) {
        chain.doFilter(request, response);
        return;
      }
      // Get jwt token and validate
      final String token = header.split(" ")[1].trim();
      try {
        if (!jwtAuthUtils.validateToken(token)) {
          chain.doFilter(request, response);
          return;
        }
      } catch (Exception ex) {
        chain.doFilter(request, response);
        return;
      }
      JwtPayloadDto jwtPayloadDto = jwtAuthUtils.decodeToken(token);
      request.setAttribute("payload", jwtPayloadDto);
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails user = new User(jwtPayloadDto.getMobileNumber(), "", new ArrayList<>());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            user, null,
            user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
      chain.doFilter(request, response);
    } catch (ExpiredJwtException eje) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      chain.doFilter(request, response);
    } catch (Exception ex) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      chain.doFilter(request, response);
    }
  }

}
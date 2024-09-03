package com.khaofit.khaofitservice.config;

import com.khaofit.khaofitservice.utilities.JwtTokenFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Web security middleware for request verification and header security.
 *
 * @author Kousik Manik.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtTokenFilter jwtTokenFilter;

  @Autowired
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http = headerConfig(http);
    http.addFilterBefore(new HostValidationFilter(new ApprovedHostsRequestMatcher()),
        UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
            .authenticationEntryPoint(customAuthenticationEntryPoint))
        .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable);
    http = requestMatchers(http);
    return http.build();
  }

  private HttpSecurity requestMatchers(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
            authorizationManagerRequestMatcherRegistry
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/health-check/**").permitAll()
                .requestMatchers("/bmi/**").fullyAuthenticated()
                .requestMatchers("/user/**").fullyAuthenticated()
                .requestMatchers("/subscription/**").fullyAuthenticated()
                .anyRequest().permitAll());
  }


  private HttpSecurity headerConfig(HttpSecurity http) throws Exception {
    return http
        .headers(headers -> headers
            .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
            .httpStrictTransportSecurity(htps -> htps
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000))
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://checkout.razorpay.com;")
                .policyDirectives("style-src 'self' 'unsafe-inline'")
                .policyDirectives("img-src 'self' https:")
                .policyDirectives("font-src 'self'")
                .policyDirectives("connect-src 'self' https:")
                .policyDirectives("form-action 'self'")
                .policyDirectives("media-src 'self'")
                .policyDirectives("object-src 'none'")
                .policyDirectives("worker-src 'self'")
                .policyDirectives("child-src 'none'")
                .policyDirectives("plugin-types 'application/pdf'")
                .policyDirectives("upgrade-insecure-requests"))
            .xssProtection(xss -> xss
                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED))
            .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
            .permissionsPolicy(permissions -> permissions
                .policy("geolocation=(self), microphone=()")));
  }

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for the application.
   * Defines the allowed origins, headers, methods, and credentials for CORS requests.
   *
   * @return The configured CorsConfigurationSource.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Set allowed origin patterns to permit requests from specified origins
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD"));
    // Allow credentials in CORS requests
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }


}

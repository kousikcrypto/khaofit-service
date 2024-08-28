package com.khaofit.khaofitservice.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaofit.khaofitservice.dto.jwt.JwtPayloadDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

/**
 * Jwt utilities for create or validate and decode token.
 *
 * @author Praveen
 */
@Component
public class JwtAuthUtils {

  Logger logger = LoggerFactory.getLogger(JwtAuthUtils.class);
  private static final String AUTHORITIES_KEY = "auth";
  @Value("${jwt.expiration-hours}")
  private Long expireHours;

  @Value("${jwt.secret-key}")
  private String plainSecret;

  private Key encodedSecret;

  @Autowired
  private ObjectMapper objectMapper;


  @PostConstruct
  protected void init() {
    this.encodedSecret = generateEncodedSecret(this.plainSecret);
  }

  protected Date getExpirationTime() {
    Date now = new Date();
    Long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
    return new Date(expireInMilis + now.getTime());
  }

  protected Key generateEncodedSecret(String plainSecret) {
    if (StringUtils.isEmpty(plainSecret)) {
      throw new IllegalArgumentException("JWT secret cannot be null or empty.");
    }
    byte[] keyBytes = Decoders.BASE64.decode(plainSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Generate JWT Token.
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   * @throws JsonProcessingException If Json processing fails.
   */
  public String generateToken(Authentication authentication, JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {
    Map<String, Object> payload = new HashMap<>();
    payload.put("userStatus", jwtPayloadDto.getUserStatus());
    payload.put("mobileNumber", jwtPayloadDto.getMobileNumber());
    payload.put("firstName", jwtPayloadDto.getFirstName());
    payload.put("middleName", jwtPayloadDto.getMiddleName());
    payload.put("lastName", jwtPayloadDto.getLastName());
    payload.put("userId", jwtPayloadDto.getUserId());

    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    try {
      String authToken = Jwts.builder()
          .id(jwtPayloadDto.getUserId())
          .claim(AUTHORITIES_KEY, authorities)
          .subject(authentication.getName())
          .claims(payload)
          .issuedAt(new Date())
          .expiration(getExpirationTime())
          .signWith(encodedSecret)
          .compact();

      logger.info("Generating auth Token = {}", authToken);
      return authToken;
    } catch (Exception ex) {
      logger.error("Getting error at generating token = {}", ex.getMessage());
      throw ex;
    }
  }

  /**
   * this class for validate ambula token .
   *
   * @param authToken @{@link String}
   * @return @{@link Boolean}
   */
  public boolean validateToken(String authToken) {
    //Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(authToken);
    Jwts.parser().verifyWith((SecretKey) encodedSecret).build().parseSignedClaims(authToken);
    return true;
  }

  /**
   * Decode JWT token to its original payload data.
   *
   * @param authToken @{@link String}
   * @return @JwtPayloadDto
   */
  public JwtPayloadDto decodeToken(String authToken) {
    Claims claims = Jwts.parser().verifyWith((SecretKey) encodedSecret).build()
            .parseSignedClaims(authToken).getPayload();
    JwtPayloadDto dto = new JwtPayloadDto();
    dto.setUserStatus((String) claims.get("userStatus"));
    dto.setMobileNumber((String) claims.get("mobileNumber"));
    dto.setFirstName((String) claims.get("firstName"));
    dto.setMiddleName((String) claims.get("middleName"));
    dto.setLastName((String) claims.get("lastName"));
    dto.setUserId((String) claims.get("userId"));
    return dto;
  }


}

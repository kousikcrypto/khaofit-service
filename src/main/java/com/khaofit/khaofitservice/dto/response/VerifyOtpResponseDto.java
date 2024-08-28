package com.khaofit.khaofitservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * this is a verifyOtpResponse dto .
 *
 * @author kousik manik .
 */
@Data
public class VerifyOtpResponseDto {

  private String token;

  private String refreshToken;

  private Object user;

  @JsonProperty("new")
  private boolean isNew;

}

package com.khaofit.khaofitservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * this is a user subscription request dto .
 *
 * @author kousik manik
 */
@Data
public class UserSubscriptionRequestDto {

  @NotNull(message = "userUlid cannot be null")
  @NotBlank(message = "userUlid cannot be blank")
  @NotEmpty(message = "userUlid cannot be empty")
  private String userUlid;

  @NotNull(message = "planUlid cannot be null")
  @NotBlank(message = "planUlid cannot be blank")
  @NotEmpty(message = "planUlid cannot be empty")
  private String planUlid;

  private Integer days;
  private Integer months;
  private Integer years;


}

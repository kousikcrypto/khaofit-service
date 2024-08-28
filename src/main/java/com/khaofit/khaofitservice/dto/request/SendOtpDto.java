package com.khaofit.khaofitservice.dto.request;

import com.khaofit.khaofitservice.constant.KhaoFitConstantService;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a send otp request dto .
 *
 * @author kousik manik
 */
@Data
public class SendOtpDto {

  @Pattern(regexp = KhaoFitConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect phone number.")
  private String mobileNumber;

}

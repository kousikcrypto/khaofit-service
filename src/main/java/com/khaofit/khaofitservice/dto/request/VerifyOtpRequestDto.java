package com.khaofit.khaofitservice.dto.request;

import com.khaofit.khaofitservice.constant.KhaoFitConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a verify otp request dto .
 *
 * @author kousik mnaik
 */
@Data
public class VerifyOtpRequestDto {

  @Pattern(regexp = KhaoFitConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect phone number.")
  private String mobileNumber;

  @Pattern(regexp = "^\\d{4}$", message = "OTP must be a 4-digit number")
  private String otp;

  @NotNull(message = "txnId cannot be null")
  @NotEmpty(message = "txnId cannot be empty")
  @NotBlank(message = "txnId cannot be blank")
  private String txnId;

}

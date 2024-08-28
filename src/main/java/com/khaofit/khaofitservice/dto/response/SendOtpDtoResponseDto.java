package com.khaofit.khaofitservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this is a Send otp dto response dto .
 *
 * @author kousik manik
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOtpDtoResponseDto {

  private String mobileNumber;

  private String txnId;
}

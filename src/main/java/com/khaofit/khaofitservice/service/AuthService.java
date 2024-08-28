package com.khaofit.khaofitservice.service;

import com.khaofit.khaofitservice.dto.request.SendOtpDto;
import com.khaofit.khaofitservice.dto.request.UserProfileRequestDto;
import com.khaofit.khaofitservice.dto.request.VerifyOtpRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a auth service .
 *
 * @author kousik manik
 */
public interface AuthService {

  public ResponseEntity<?> sendMobileOtp(SendOtpDto sendOtpDto);

  public ResponseEntity<?> verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto);

  public ResponseEntity<?> saveKhaoFitUserProfile(UserProfileRequestDto userProfileRequestDto);

}

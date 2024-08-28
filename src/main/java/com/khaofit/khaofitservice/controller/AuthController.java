package com.khaofit.khaofitservice.controller;

import com.khaofit.khaofitservice.dto.request.SendOtpDto;
import com.khaofit.khaofitservice.dto.request.UserProfileRequestDto;
import com.khaofit.khaofitservice.dto.request.VerifyOtpRequestDto;
import com.khaofit.khaofitservice.dto.response.ResponseDto;
import com.khaofit.khaofitservice.service.AuthService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a auth controller .
 *
 * @author kousik mnaik
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/send-otp")
  public ResponseEntity<?> sendMobileOtp(@Valid @RequestBody SendOtpDto sendOtpDto) {
    return authService.sendMobileOtp(sendOtpDto);
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
    return authService.verifyOtp(verifyOtpRequestDto);
  }

  @PostMapping("/save/profile")
  public ResponseEntity<?> saveKhaoFitUserProfile(@Valid @RequestBody UserProfileRequestDto userProfileRequestDto) {
    return authService.saveKhaoFitUserProfile(userProfileRequestDto);
  }


  /**
   * Method to handle the validation issues.
   *
   * @param ex invalid argument.
   * @return ResponseDto.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseDto handleValidationExceptions(MethodArgumentNotValidException ex) {
    ResponseDto responseDto = new ResponseDto();
    responseDto.setResponse(false);
    responseDto.setTimestamp(LocalDateTime.now());
    responseDto.setStatus(HttpStatus.BAD_REQUEST);
    responseDto.setMessage("Validation Error.");
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = "message";
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    responseDto.setData(errors);
    return responseDto;
  }

  /**
   * Exception filter.
   *
   * @param ex {@link ConstraintViolationException}
   * @return @ResponseEntity
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseDto handleConstraintViolationExceptions(ConstraintViolationException ex) {
    ResponseDto responseDto = new ResponseDto();
    responseDto.setResponse(false);
    responseDto.setTimestamp(LocalDateTime.now());
    responseDto.setStatus(HttpStatus.BAD_REQUEST);
    responseDto.setMessage("Validation Error.");
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String fieldName = "message";
      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    }
    responseDto.setData(errors);
    return responseDto;
  }

}

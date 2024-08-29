package com.khaofit.khaofitservice.service;

import com.khaofit.khaofitservice.dto.request.BmiRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a bmi service class .
 *
 * @author kousik manik
 */
public interface BmiService {

  public ResponseEntity<?> calculateBmi(String mobileNumber, BmiRequestDto bmiRequestDto);

}

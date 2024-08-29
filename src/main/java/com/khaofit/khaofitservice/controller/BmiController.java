package com.khaofit.khaofitservice.controller;


import com.khaofit.khaofitservice.constant.KhaoFitConstantService;
import com.khaofit.khaofitservice.dto.request.BmiRequestDto;
import com.khaofit.khaofitservice.service.BmiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a bmi controller class .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/bmi")
public class BmiController {

  @Autowired
  private BmiService bmiService;

  @PostMapping("/calculate")
  public ResponseEntity<?> calculateBmi(@Valid @RequestParam(name = "mobileNumber")
                                        @NotBlank(message = "Mobile number is required")
                                        @Pattern(regexp = KhaoFitConstantService.MOBILE_NUMBER_REGEX,
                                            message = "Invalid mobile number format")
                                        String mobileNumber, @Valid @RequestBody BmiRequestDto bmiRequestDto) {
    return bmiService.calculateBmi(mobileNumber, bmiRequestDto);
  }

}

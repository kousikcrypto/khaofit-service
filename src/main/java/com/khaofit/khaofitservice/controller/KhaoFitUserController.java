package com.khaofit.khaofitservice.controller;

import com.khaofit.khaofitservice.dto.request.EditUserProfileRequestDto;
import com.khaofit.khaofitservice.service.KhaoFitUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a khaofit user details controller .
 *
 * @author kousik mnaik
 */
@RestController
@RequestMapping("/user")
public class KhaoFitUserController {

  @Autowired
  private KhaoFitUserService khaoFitUserService;

  @GetMapping("/details")
  public ResponseEntity<?> getUserDetails(@Valid @RequestParam(name = "ulid")
                                          @NotBlank(message = "ulid is required") String ulid) {
    return khaoFitUserService.getUserDetails(ulid);
  }

  @PutMapping("/edit/details")
  public ResponseEntity<?> editUserDetails(@Valid @RequestParam(name = "ulid")
                                           @NotBlank(message = "ulid is required") String ulid,
                                           @Valid @RequestBody EditUserProfileRequestDto dto) {
    return khaoFitUserService.editUserDetails(ulid, dto);
  }


}

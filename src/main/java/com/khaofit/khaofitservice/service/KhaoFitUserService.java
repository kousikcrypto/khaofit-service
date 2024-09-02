package com.khaofit.khaofitservice.service;

import com.khaofit.khaofitservice.dto.request.EditUserProfileRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a user profile service class .
 *
 * @author kousik manik
 */
public interface KhaoFitUserService {

  public ResponseEntity<?> getUserDetails(String ulid);

  public ResponseEntity<?> editUserDetails(String ulid, EditUserProfileRequestDto dto);

}

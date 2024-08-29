package com.khaofit.khaofitservice.service;

import org.springframework.http.ResponseEntity;

/**
 * this is a user profile service class .
 *
 * @author kousik manik
 */
public interface KhaoFitUserService {

  public ResponseEntity<?> getUserDetails(String ulid);

}

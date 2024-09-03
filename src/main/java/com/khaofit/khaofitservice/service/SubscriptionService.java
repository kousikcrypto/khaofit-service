package com.khaofit.khaofitservice.service;

import com.khaofit.khaofitservice.dto.request.CreateSubscriptionPlansRequestDto;
import com.khaofit.khaofitservice.dto.request.UserSubscriptionRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a subscription service class .
 *
 * @author kousik manik
 */
public interface SubscriptionService {

  public ResponseEntity<?> createSubscriptionPlans(CreateSubscriptionPlansRequestDto dto);

  public ResponseEntity<?> getAllSubscriptionPlans();

  public ResponseEntity<?> getSubscriptionPlanByUlid(String ulid);

  public ResponseEntity<?> addSubscriptionForUser(UserSubscriptionRequestDto dto);

  public ResponseEntity<?> getUserSubscriptionDetails(String ulid);

}

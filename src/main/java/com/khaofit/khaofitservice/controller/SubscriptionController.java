package com.khaofit.khaofitservice.controller;

import com.khaofit.khaofitservice.dto.request.CreateSubscriptionPlansRequestDto;
import com.khaofit.khaofitservice.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a subscription controller .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

  @Autowired
  private SubscriptionService subscriptionService;

  @PostMapping("/plan/create")
  public ResponseEntity<?> createSubscriptionPlans(@Valid @RequestBody CreateSubscriptionPlansRequestDto dto) {
    return subscriptionService.createSubscriptionPlans(dto);
  }

  @GetMapping("/plan/all")
  public ResponseEntity<?> getAllSubscriptionPlans() {
    return subscriptionService.getAllSubscriptionPlans();
  }

  @GetMapping("/plan/byId")
  public ResponseEntity<?> getSubscriptionPlanByUlid(@Valid @RequestParam(name = "ulid")
                                                     @NotBlank(message = "ulid is required") String ulid) {
    return subscriptionService.getSubscriptionPlanByUlid(ulid);
  }

}

package com.khaofit.khaofitservice.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaofit.khaofitservice.dto.request.CreateSubscriptionPlansRequestDto;
import com.khaofit.khaofitservice.model.SubscriptionPlans;
import com.khaofit.khaofitservice.repository.SubscriptionPlansRepository;
import com.khaofit.khaofitservice.response.BaseResponse;
import com.khaofit.khaofitservice.service.SubscriptionService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * this is a subscription service implementation class .
 *
 * @author kousik manik
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

  @Autowired
  private SubscriptionPlansRepository subscriptionPlansRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

  /**
   * this is a create subscription plan method .
   *
   * @param dto @{@link CreateSubscriptionPlansRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> createSubscriptionPlans(CreateSubscriptionPlansRequestDto dto) {
    logger.info("Attempting to create a new subscription plan with name: {}", dto.getName());

    try {
      Optional<SubscriptionPlans> optionalSubscriptionPlans = subscriptionPlansRepository
          .findByName(dto.getName().trim());

      if (optionalSubscriptionPlans.isPresent()) {
        logger.warn("Subscription plan already exists with name: {}", dto.getName());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Subscription Plan is already present.");
      }

      SubscriptionPlans subscriptionPlans = objectMapper.convertValue(dto, SubscriptionPlans.class);
      subscriptionPlansRepository.saveAndFlush(subscriptionPlans);

      logger.info("Subscription plan created successfully with name: {}", dto.getName());
      return baseResponse.successResponse("Subscription plan added successfully.", subscriptionPlans);

    } catch (Exception e) {
      logger.error("Error occurred while creating subscription plan with name: {}. Error: {}",
          dto.getName(), e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An unexpected error occurred. Please try again later.");
    }
  }

  /**
   * this is a get all subscription plan method .
   *
   * @return @{@link ResponseEntity}
   */
  @Override
  public ResponseEntity<?> getAllSubscriptionPlans() {
    logger.info("Fetching all subscription plans...");

    try {
      List<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findByActiveTrue();

      if (subscriptionPlans.isEmpty()) {
        logger.warn("No subscription plans found.");
        return baseResponse.successResponse("No subscription plans available.", subscriptionPlans);
      }

      logger.info("Successfully retrieved {} subscription plans.", subscriptionPlans.size());
      return baseResponse.successResponse(subscriptionPlans);

    } catch (Exception e) {
      logger.error("Error occurred while fetching subscription plans: {}", e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching subscription plans.");
    }
  }

  /**
   * this is a get subscription plan details by ulid .
   *
   * @param ulid @{@link String}
   * @return @{@link ResponseEntity}
   */
  @Override
  public ResponseEntity<?> getSubscriptionPlanByUlid(String ulid) {
    logger.info("Fetching subscription plan with ULID: {}", ulid);

    try {
      Optional<SubscriptionPlans> subscriptionPlan = subscriptionPlansRepository.findByUlIdAndActiveTrue(ulid);

      if (subscriptionPlan.isEmpty()) {
        logger.warn("Subscription plan not found for ULID: {}", ulid);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Subscription plan not found!");
      }

      logger.info("Subscription plan found for ULID: {}", ulid);
      return baseResponse.successResponse(subscriptionPlan.get());

    } catch (Exception e) {
      logger.error("Error occurred while fetching subscription plan with ULID: {}. Error: {}", ulid, e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching the subscription plan.");
    }
  }
}

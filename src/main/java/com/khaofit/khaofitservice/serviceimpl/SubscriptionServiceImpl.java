package com.khaofit.khaofitservice.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaofit.khaofitservice.dto.request.CreateSubscriptionPlansRequestDto;
import com.khaofit.khaofitservice.dto.request.UserSubscriptionRequestDto;
import com.khaofit.khaofitservice.model.SubscriptionPlans;
import com.khaofit.khaofitservice.model.UserSubscriptionDetails;
import com.khaofit.khaofitservice.model.Users;
import com.khaofit.khaofitservice.repository.SubscriptionPlansRepository;
import com.khaofit.khaofitservice.repository.UserRepository;
import com.khaofit.khaofitservice.repository.UserSubscriptionDetailsRepository;
import com.khaofit.khaofitservice.response.BaseResponse;
import com.khaofit.khaofitservice.service.SubscriptionService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
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
  private UserRepository userRepository;

  @Autowired
  private UserSubscriptionDetailsRepository userSubscriptionDetailsRepository;

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

  /**
   * this is method for user subscribe a plan .
   *
   * @param dto @{@link UserSubscriptionRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> addSubscriptionForUser(UserSubscriptionRequestDto dto) {
    try {
      logger.info("Starting subscription process for user with ULID: {}", dto.getUserUlid());

      // Validate user existence
      Optional<Users> optionalUsers = userRepository.findByUlId(dto.getUserUlid());
      if (optionalUsers.isEmpty()) {
        logger.warn("User with ULID: {} not found", dto.getUserUlid());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User not found!");
      }

      List<UserSubscriptionDetails> userSubscriptionDetailsList = userSubscriptionDetailsRepository
          .findByUserAndActiveTrue(optionalUsers.get());

      if (!userSubscriptionDetailsList.isEmpty()) {
        logger.warn("User with ulid '{}' is already subscribed to an active plan", dto.getUserUlid());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User is already subscribed to an active plan.");
      }

      // Validate subscription plan existence and active status
      Optional<SubscriptionPlans> optionalSubscriptionPlans = subscriptionPlansRepository
          .findByUlIdAndActiveTrue(dto.getPlanUlid());
      if (optionalSubscriptionPlans.isEmpty()) {
        logger.warn("Subscription plan with ULID: {} not found or inactive", dto.getPlanUlid());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Subscription Plan Not Found!");
      }

      // Prepare user subscription details
      UserSubscriptionDetails userSubscriptionDetails = new UserSubscriptionDetails();
      userSubscriptionDetails.setUser(optionalUsers.get());
      userSubscriptionDetails.setSubscriptionPlans(optionalSubscriptionPlans.get());

      // Calculate subscription end time based on days, months, or years
      LocalDate startDate = LocalDate.now();
      if (dto.getDays() != null) {
        userSubscriptionDetails.setSubscriptionEndTime(startDate.plusDays(dto.getDays()));
      } else if (dto.getMonths() != null) {
        userSubscriptionDetails.setSubscriptionEndTime(startDate.plusMonths(dto.getMonths()));
      } else if (dto.getYears() != null) {
        userSubscriptionDetails.setSubscriptionEndTime(startDate.plusYears(dto.getYears()));
      } else {
        logger.warn("No valid duration specified for subscription for user with ULID: {}", dto.getUserUlid());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "No valid duration specified for subscription.");
      }

      // Save user subscription details
      UserSubscriptionDetails savedUserSubscriptionDetails = userSubscriptionDetailsRepository
          .saveAndFlush(userSubscriptionDetails);
      logger.info("User subscription activated successfully for user with ULID: {}", dto.getUserUlid());

      return baseResponse.successResponse("User Subscription Activated Successfully", savedUserSubscriptionDetails);
    } catch (Exception e) {
      logger.error("Error occurred while subscribing user with ULID: {}", dto.getUserUlid(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An unexpected error occurred. Please try again later.");
    }
  }

  /**
   * this is a get user subscription details method .
   *
   * @param ulid @{@link String}
   * @return @{@link ResponseEntity}
   */
  @Override
  public ResponseEntity<?> getUserSubscriptionDetails(String ulid) {
    logger.info("Fetching user subscription details for ULID: {}", ulid);
    try {
      Optional<Users> optionalUsers = userRepository.findByUlId(ulid);

      if (optionalUsers.isEmpty()) {
        logger.warn("User with ULID: {} not found.", ulid);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User Not Found!");
      }

      Users user = optionalUsers.get();
      logger.info("User with ULID: {} found. Fetching subscription details.", ulid);

      List<UserSubscriptionDetails> userSubscriptionDetailsList = userSubscriptionDetailsRepository
          .findByUserOrderByCreatedAtDesc(user);

      if (userSubscriptionDetailsList.isEmpty()) {
        logger.info("No subscription details found for user with ULID: {}", ulid);
        return baseResponse.successResponse("No Subscription Details Found", Collections.emptyList());
      }

      logger.info("Successfully fetched {} subscription details for user with ULID: {}",
          userSubscriptionDetailsList.size(), ulid);
      return baseResponse.successResponse("Subscription Details Fetched Successfully", userSubscriptionDetailsList);
    } catch (Exception e) {
      logger.error("Error fetching subscription details for user with ULID: {}: {}", ulid, e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching subscription details.");
    }
  }


}

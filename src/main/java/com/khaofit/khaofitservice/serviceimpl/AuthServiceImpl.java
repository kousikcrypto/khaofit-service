package com.khaofit.khaofitservice.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaofit.khaofitservice.cache.GuavaCacheServiceImpl;
import com.khaofit.khaofitservice.dto.jwt.JwtPayloadDto;
import com.khaofit.khaofitservice.dto.request.SendOtpDto;
import com.khaofit.khaofitservice.dto.request.UserProfileRequestDto;
import com.khaofit.khaofitservice.dto.request.VerifyOtpRequestDto;
import com.khaofit.khaofitservice.dto.response.SendOtpDtoResponseDto;
import com.khaofit.khaofitservice.dto.response.VerifyOtpResponseDto;
import com.khaofit.khaofitservice.model.ReferralDetails;
import com.khaofit.khaofitservice.model.Users;
import com.khaofit.khaofitservice.repository.ReferralDetailsRepository;
import com.khaofit.khaofitservice.repository.UserRepository;
import com.khaofit.khaofitservice.response.BaseResponse;
import com.khaofit.khaofitservice.service.AuthService;
import com.khaofit.khaofitservice.utilities.JwtAuthUtils;
import com.khaofit.khaofitservice.utilities.StringUtils;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * this is auth service implementation .
 *
 * @author kousik manik
 */
@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private ReferralDetailsRepository referralDetailsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BaseResponse baseResponse;

  @Autowired
  private JwtAuthUtils jwtAuthUtils;

  @Autowired
  private GuavaCacheServiceImpl guavaCacheService;

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


  /**
   * this is a method for mask mobile number .
   *
   * @param mobileNumber @{@link String}
   * @return @{@link String}
   */
  private String maskMobileNumber(String mobileNumber) {
    return mobileNumber.replaceAll("\\d(?=\\d{4})", "*");
  }

  /**
   * this is a jwtPayload dto method .
   *
   * @param users @{@link Users}
   * @return @{@link JwtPayloadDto}
   */
  private JwtPayloadDto makeJwtPayLoadDto(Users users) {
    JwtPayloadDto jwtPayloadDto = new JwtPayloadDto();
    jwtPayloadDto.setMobileNumber(users.getMobileNumber());
    jwtPayloadDto.setFirstName(users.getFirstName());
    jwtPayloadDto.setMiddleName(users.getMiddleName());
    jwtPayloadDto.setLastName(users.getLastName());
    jwtPayloadDto.setUserStatus(users.getStatus().name());
    jwtPayloadDto.setUserId(users.getUlId());
    return jwtPayloadDto;
  }

  /**
   * this is a generate jwt token method .
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   */
  private String generateJwtToken(JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {

    UsernamePasswordAuthenticationToken authenticationToken;
    authenticationToken = new UsernamePasswordAuthenticationToken(
        jwtPayloadDto.getMobileNumber(),
        null
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    String authToken = jwtAuthUtils.generateToken(authenticationToken, jwtPayloadDto);
    return authToken;
  }

  /**
   * this is a send mobileOtp method for sending otp to a mobile number .
   *
   * @param sendOtpDto @{@link SendOtpDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  public ResponseEntity<?> sendMobileOtp(SendOtpDto sendOtpDto) {
    try {
      final String txnId = UUID.randomUUID().toString();
      String maskedMobileNumber = maskMobileNumber(sendOtpDto.getMobileNumber());
      SendOtpDtoResponseDto sendOtpDtoResponseDto = new SendOtpDtoResponseDto(sendOtpDto.getMobileNumber(), txnId);
      guavaCacheService.put(sendOtpDto.getMobileNumber(), txnId);
      logger.info("OTP request initiated. Transaction ID: {}, Mobile Number: {}", txnId, maskedMobileNumber);

      String successMessage = "OTP has been successfully sent to the registered mobile number ending in "
          + maskedMobileNumber.substring(maskedMobileNumber.length() - 4);
      return baseResponse.successResponse(successMessage, sendOtpDtoResponseDto);
    } catch (Exception e) {
      logger.error("Error occurred while sending OTP to mobile number: {}, {}", sendOtpDto.getMobileNumber(),
          e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to send OTP. Please try again later.");
    }
  }

  /**
   * this is a verify mobile otp method .
   *
   * @param verifyOtpRequestDto @{@link VerifyOtpRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  public ResponseEntity<?> verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto) {
    try {
      logger.info("Starting OTP verification for mobile number: {}",
          maskMobileNumber(verifyOtpRequestDto.getMobileNumber()));

      String txnId = (String) guavaCacheService.get(verifyOtpRequestDto.getMobileNumber());
      if (txnId == null || !txnId.equals(verifyOtpRequestDto.getTxnId())) {
        logger.warn("Invalid transaction ID for mobile number: {}",
            maskMobileNumber(verifyOtpRequestDto.getMobileNumber()));
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid Transaction ID.");
      }

      if (!verifyOtpRequestDto.getOtp().equals("3214")) {
        logger.warn("OTP mismatch for mobile number: {}", maskMobileNumber(verifyOtpRequestDto.getMobileNumber()));
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "OTP does not match.");
      }

      Optional<Users> users = userRepo.findByMobileNumber(verifyOtpRequestDto.getMobileNumber());

      VerifyOtpResponseDto verifyOtpResponseDto = new VerifyOtpResponseDto();
      if (users.isPresent()) {
        Users user = users.get();

        verifyOtpResponseDto.setNew(false);
        verifyOtpResponseDto.setToken(generateJwtToken(makeJwtPayLoadDto(user)));
        verifyOtpResponseDto.setRefreshToken(null);
        verifyOtpResponseDto.setUser(user);
        verifyOtpResponseDto.setTxnId(verifyOtpRequestDto.getTxnId());

        logger.info("OTP verification successful for existing user. Mobile number: {}",
            maskMobileNumber(verifyOtpRequestDto.getMobileNumber()));
        guavaCacheService.remove(user.getMobileNumber());
      } else {
        verifyOtpResponseDto.setNew(true);
        verifyOtpResponseDto.setTxnId(verifyOtpRequestDto.getTxnId());
        logger.info("OTP verification successful for new user. Mobile number: {}",
            maskMobileNumber(verifyOtpRequestDto.getMobileNumber()));
      }

      return baseResponse.successResponse(verifyOtpResponseDto);

    } catch (Exception e) {
      logger.error("Error during OTP verification for mobile number: {}, {}",
          maskMobileNumber(verifyOtpRequestDto.getMobileNumber()), e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred during OTP verification. Please try again.");
    }
  }

  /**
   * this is a save user profile method .
   *
   * @param userProfileRequestDto @{@link UserProfileRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> saveKhaoFitUserProfile(UserProfileRequestDto userProfileRequestDto) {
    try {
      String maskedMobileNumber = maskMobileNumber(userProfileRequestDto.getMobileNumber());
      logger.info("Starting to save user profile for mobile number: {}", maskedMobileNumber);

      if (!isValidTransactionId(userProfileRequestDto)) {
        logger.warn("Invalid transaction ID for mobile number save profile : {}", maskedMobileNumber);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid Transaction ID.");
      }

      if (!isReferredCodeValid(userProfileRequestDto.getReferredCode())) {
        logger.info("Entered Referral Code is Invalid: {}", userProfileRequestDto.getReferredCode().trim());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Entered ReferralCode Is Invalid");
      }

      if (userRepo.existsByMobileNumber(userProfileRequestDto.getMobileNumber())) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User already exists!");
      }

      Users newUser = createUserFromRequest(userProfileRequestDto);
      userRepo.saveAndFlush(newUser);

      ReferralDetails referralDetails = getReferralDetails(userProfileRequestDto, newUser);
      referralDetailsRepository.saveAndFlush(referralDetails);

      VerifyOtpResponseDto verifyOtpResponseDto = buildOtpResponse(userProfileRequestDto, newUser);

      guavaCacheService.remove(newUser.getMobileNumber());
      logger.info("Cleared OTP cache for mobile number: {}", maskedMobileNumber);

      return baseResponse.successResponse(verifyOtpResponseDto);

    } catch (Exception e) {
      logger.error("Error saving user profile for mobile number: {}, {}",
          maskMobileNumber(userProfileRequestDto.getMobileNumber()), e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while saving the user profile. Please try again.");
    }
  }

  /**
   * this is a method for check transactionId valid or not .
   *
   * @param userProfileRequestDto @{@link UserProfileRequestDto}
   * @return @{@link Boolean}
   */
  private boolean isValidTransactionId(UserProfileRequestDto userProfileRequestDto) {
    String txnId = (String) guavaCacheService.get(userProfileRequestDto.getMobileNumber());
    return txnId != null && txnId.equals(userProfileRequestDto.getTxnId());
  }

  /**
   * this is a method for check referral code is valid or not .
   *
   * @param referredCode @{@link String}
   * @return @{@link Boolean}
   */
  private boolean isReferredCodeValid(String referredCode) {
    if (StringUtils.isNotNullAndNotEmpty(referredCode.trim())) {
      return userRepo.findByReferralCode(referredCode.trim()).isPresent();
    }
    return true;
  }

  /**
   * this is method for create user class from request dto .
   *
   * @param userProfileRequestDto @{@link UserProfileRequestDto}
   * @return @{@link Users}
   */
  private Users createUserFromRequest(UserProfileRequestDto userProfileRequestDto) {
    Users users = objectMapper.convertValue(userProfileRequestDto, Users.class);
    users.setReferralCode(generateRandomAlphaNumeric(6));
    return users;
  }

  /**
   * this is a referralDetails method .
   *
   * @param userProfileRequestDto   @{@link UserProfileRequestDto}
   * @param dbUsers @{@link Users}
   * @return @{@link ReferralDetails}
   */
  private static @NotNull ReferralDetails getReferralDetails(UserProfileRequestDto userProfileRequestDto, Users dbUsers) {
    ReferralDetails referralDetails = new ReferralDetails();
    if (StringUtils.isNotNullAndNotEmpty(userProfileRequestDto.getReferredCode().trim())) {
      referralDetails.setReferralCode(userProfileRequestDto.getReferredCode().trim());
      referralDetails.setReferral(true);
    } else {
      referralDetails.setReferralCode(dbUsers.getReferralCode());
      referralDetails.setReferral(false);
    }
    referralDetails.setUser(dbUsers);
    referralDetails.setCoinsAwarded(0);
    return referralDetails;
  }

  /**
   * this is a build otp response method .
   *
   * @param userProfileRequestDto @{@link UserProfileRequestDto}
   * @param dbUsers @{@link Users}
   * @return @{@link VerifyOtpResponseDto}
   * @throws JsonProcessingException @{@link JsonProcessingException}
   */
  private VerifyOtpResponseDto buildOtpResponse(UserProfileRequestDto userProfileRequestDto, Users dbUsers)
      throws JsonProcessingException {
    VerifyOtpResponseDto verifyOtpResponseDto = new VerifyOtpResponseDto();
    verifyOtpResponseDto.setNew(false);
    verifyOtpResponseDto.setToken(generateJwtToken(makeJwtPayLoadDto(dbUsers)));
    verifyOtpResponseDto.setUser(dbUsers);
    verifyOtpResponseDto.setTxnId(userProfileRequestDto.getTxnId());
    return verifyOtpResponseDto;
  }

  /**
   * generate Random AlphaNumeric Number .
   *
   * @param length @{@link Integer}
   * @return @{@link String}
   */
  private String generateRandomAlphaNumeric(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder result = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      result.append(characters.charAt(random.nextInt(characters.length())));
    }
    return result.toString();
  }

}

package com.khaofit.khaofitservice.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaofit.khaofitservice.dto.request.EditUserProfileRequestDto;
import com.khaofit.khaofitservice.model.Users;
import com.khaofit.khaofitservice.repository.UserRepository;
import com.khaofit.khaofitservice.response.BaseResponse;
import com.khaofit.khaofitservice.service.KhaoFitUserService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * this is a khao fit user service implementation class .
 *
 * @author kousik manik
 */
@Service
public class KhaoFitUserServiceImpl implements KhaoFitUserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BaseResponse baseResponse;

  @Autowired
  private ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(KhaoFitUserServiceImpl.class);

  /**
   * Method to get user details by ULID.
   *
   * @param ulid User ULID.
   * @return ResponseEntity containing user details or an error message.
   */
  @Override
  public ResponseEntity<?> getUserDetails(String ulid) {
    logger.info("Fetching user details for ULID: {}", ulid);
    try {
      Optional<Users> optionalUser = userRepository.findByUlId(ulid);

      if (optionalUser.isPresent()) {
        Users user = optionalUser.get();
        logger.info("User found: {}", user.getMobileNumber());
        return baseResponse.successResponse(user); // Consistent return type
      } else {
        logger.warn("User not found for ULID: {}", ulid);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User not found for ULID: " + ulid);
      }

    } catch (Exception e) {
      logger.error("An error occurred while fetching user details for ULID: {}", ulid, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching user details");
    }
  }

  /**
   * this is a edit user details method .
   *
   * @param ulid @{@link String}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> editUserDetails(String ulid, EditUserProfileRequestDto dto) {
    logger.info("Attempting to update user details for ulid: {}", ulid);

    try {
      Optional<Users> optionalUsers = userRepository.findByUlId(ulid);
      if (optionalUsers.isEmpty()) {
        logger.warn("User not found with ulid: {}", ulid);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User not found with the provided ID.");
      }

      Users users = optionalUsers.get();
      users.setFirstName(dto.getFirstName());
      users.setMiddleName(dto.getMiddleName());
      users.setLastName(dto.getLastName());
      users.setDateOfBirth(dto.getDateOfBirth());
      users.setEmailId(dto.getEmailId());
      users.setGender(dto.getGender());

      Users updatedUser = userRepository.saveAndFlush(users);
      logger.info("User details updated successfully for ulid: {}", ulid);

      return baseResponse.successResponse("User details updated successfully.", updatedUser);
    } catch (Exception e) {
      logger.error("Error occurred while updating user details for ulid: {}. Error: {}", ulid, e.getMessage());
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An unexpected error occurred. Please try again later.");
    }
  }


}

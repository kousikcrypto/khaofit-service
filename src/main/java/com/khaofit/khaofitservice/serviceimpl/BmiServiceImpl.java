package com.khaofit.khaofitservice.serviceimpl;

import com.khaofit.khaofitservice.dto.request.BmiRequestDto;
import com.khaofit.khaofitservice.enums.UserGender;
import com.khaofit.khaofitservice.model.BmiDetails;
import com.khaofit.khaofitservice.model.Users;
import com.khaofit.khaofitservice.repository.BmiDetailsRepository;
import com.khaofit.khaofitservice.repository.UserRepository;
import com.khaofit.khaofitservice.response.BaseResponse;
import com.khaofit.khaofitservice.service.BmiService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * this is a bmi service implementation class .
 *
 * @author kousik manik
 */
@Service
public class BmiServiceImpl implements BmiService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BmiDetailsRepository bmiDetailsRepository;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(BmiServiceImpl.class);



  /**
   * this is a calculate bmi method .
   *
   * @param bmiRequestDto @{@link BmiRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> calculateBmi(String mobileNumber, BmiRequestDto bmiRequestDto) {

    try {
      logger.info("Starting BMI calculation for mobile number: {}", mobileNumber);

      Optional<Users> optionalUsers = userRepository.findByMobileNumber(mobileNumber);

      if (optionalUsers.isEmpty()) {
        logger.warn("User with mobile number {} not found.", mobileNumber);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User Not Found");
      }

      Users user = optionalUsers.get();
      logger.info("User found: {}, proceeding with BMI calculation.", user.getMobileNumber());

      double height = bmiRequestDto.getHeight();
      double weight = bmiRequestDto.getWeight();
      UserGender gender = bmiRequestDto.getGender();
      int age = bmiRequestDto.getAge();

      logger.debug("Height: {}, Weight: {}, Gender: {}, Age: {}", height, weight, gender, age);

      double bmi = weight / (height * height);
      logger.info("Calculated BMI for user {}: {}", user.getMobileNumber(), bmi);

      // Determine BMI Category
      String category = getBmiCategory(bmi, gender, age);
      logger.info("BMI category for user {}: {}", user.getMobileNumber(), category);

      BmiDetails bmiDetails = createBmiDetails(bmi, gender, age, height, weight, category, user);
      logger.info("BMI details saved for user {}:", user.getMobileNumber());

      return baseResponse.successResponse("BMI Calculated Successfully", bmiDetails);

    } catch (Exception e) {
      logger.error("Error occurred while calculating BMI for mobile number {}: {}", mobileNumber, e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while calculating BMI");
    }
  }

  /**
   * this is a getBmi category method .
   *
   * @param bmi    @{@link Double}
   * @param gender @{@link String}
   * @param age    @{@link Integer}
   * @return @{@link String}
   */
  private String getBmiCategory(double bmi, UserGender gender, int age) {
    if (bmi < 18.5) {
      return "Underweight";
    } else if (bmi >= 18.5 && bmi < 24.9) {
      return "Normal weight";
    } else if (bmi >= 25 && bmi < 29.9) {
      return "Overweight";
    } else {
      return "Obesity";
    }
  }

  /**
   * this is method for save into database all bmi details .
   *
   * @param bmi @{@link Double}
   * @param gender @{@link UserGender}
   * @param age @{@link Integer}
   * @param height @{@link Double}
   * @param weight @{@link Double}
   * @param category @{@link String}
   * @param user @{@link Users}
   * @return @{@link BmiDetails}
   */
  private BmiDetails createBmiDetails(double bmi, UserGender gender, int age, double height,
                                      double weight, String category, Users user) {
    // Deactivate all current active BMI details for the user
    int updatedCount = bmiDetailsRepository.deactivateActiveBmiDetailsForUser(user.getId());

    // Log how many records were deactivated (optional)
    if (updatedCount > 0) {
      logger.info("{} active BMI details deactivated for user MobileNumber {}", updatedCount, user.getMobileNumber());
    }

    // Create new BMI details record
    BmiDetails bmiDetails = new BmiDetails();
    bmiDetails.setBmiValue(bmi);
    bmiDetails.setHeight(height);
    bmiDetails.setWeight(weight);
    bmiDetails.setAge(age);
    bmiDetails.setGender(gender);
    bmiDetails.setCategory(category);
    bmiDetails.setUser(user);
    bmiDetails.setActive(true); // Set this record as active

    // Save and flush the new record
    return bmiDetailsRepository.saveAndFlush(bmiDetails);
  }

}

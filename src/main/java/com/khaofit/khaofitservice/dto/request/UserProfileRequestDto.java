package com.khaofit.khaofitservice.dto.request;

import com.khaofit.khaofitservice.constant.KhaoFitConstantService;
import com.khaofit.khaofitservice.enums.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * This is a user profile request DTO for registration.
 *
 * @author Kousik Manik
 */
@Data
public class UserProfileRequestDto {

  @NotBlank(message = "First name is mandatory")
  @Size(max = 50, message = "First name must be at most 50 characters long")
  private String firstName;

  @Size(max = 50, message = "Middle name must be at most 50 characters long")
  private String middleName;

  @NotBlank(message = "Last name is mandatory")
  @Size(max = 50, message = "Last name must be at most 50 characters long")
  private String lastName;

  @NotBlank(message = "Date of birth is mandatory")
  @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date of birth must be in the format YYYY-MM-DD")
  private String dateOfBirth;

  @Pattern(regexp = KhaoFitConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect phone number.")
  private String mobileNumber;

  @NotBlank(message = "Email ID is mandatory")
  @Email(message = "Email ID should be valid")
  @Pattern(regexp = KhaoFitConstantService.EMAIL_REGEX, message = "Incorrect email Id.")
  private String emailId;

  @NotNull(message = "Gender is mandatory")
  private UserGender gender;

  private String referredCode;

  @NotBlank(message = "txnId cannot be blank")
  @NotEmpty(message = "txnId cannot be empty")
  @NotNull(message = "txnId cannot be null")
  private String txnId;
}

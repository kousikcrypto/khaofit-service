package com.khaofit.khaofitservice.dto.request;

import com.khaofit.khaofitservice.enums.UserGender;
import lombok.Data;

/**
 * this is a edit user profile request dto .
 *
 * @author kousik manik
 */
@Data
public class EditUserProfileRequestDto {

  private String firstName;

  private String middleName;

  private String lastName;

  private String dateOfBirth;

  private String emailId;

  private UserGender gender;

}

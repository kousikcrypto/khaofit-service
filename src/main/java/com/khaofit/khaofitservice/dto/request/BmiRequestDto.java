package com.khaofit.khaofitservice.dto.request;

import com.khaofit.khaofitservice.enums.UserGender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * this is a bmi request dto .
 *
 * @author kousik manik
 */
@Data
public class BmiRequestDto {

  @NotNull(message = "Height cannot be null")
  @Positive(message = "Height must be greater than zero")
  private double height;

  @NotNull(message = "Weight cannot be null")
  @Positive(message = "Weight must be greater than zero")
  private double weight;

  @NotNull(message = "Gender cannot be null")
  private UserGender gender;

  @NotNull(message = "Age cannot be null")
  @Min(value = 1, message = "Age must be equal to or greater than 1")
  @Max(value = 150, message = "Age must be equal to or less than 150")
  private int age;

}

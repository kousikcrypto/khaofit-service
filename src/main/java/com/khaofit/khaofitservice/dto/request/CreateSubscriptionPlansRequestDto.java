package com.khaofit.khaofitservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

/**
 * This is a create subscription plans request DTO.
 *
 * @author kousik manik
 */
@Data
public class CreateSubscriptionPlansRequestDto {

  @NotBlank(message = "Subscription name is required.")
  @Size(max = 50, message = "Subscription name must not exceed 50 characters.")
  private String name;

  @NotBlank(message = "Subscription description is required.")
  private String description;

  @NotNull(message = "Item limit is required.")
  @Min(value = 1, message = "Item limit must be at least 1.")
  @Max(value = 100, message = "Item limit must not exceed 100.")  // Adjust the max value as needed
  private Integer itemLimit;

  @NotEmpty(message = "At least one category is required.")
  private Set<@NotBlank(message = "Category name cannot be blank.") String> category;

  @NotNull(message = "Price is required.")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
  @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount.")
  private Double price;
}

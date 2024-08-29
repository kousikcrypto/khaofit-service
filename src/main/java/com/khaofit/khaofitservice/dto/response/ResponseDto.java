package com.khaofit.khaofitservice.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Base response dto.
 *
 * @author avinash
 */
@Data
public class ResponseDto {
  boolean response;
  String message;
  Object data;
  HttpStatus status;
  LocalDateTime timestamp;

  // Getters and setters

  /**
   * this is a to json method .
   *
   * @return @{@link String}
   */
  public String toJson() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(this);
    } catch (Exception e) {
      return "{}"; // Handle serialization exception
    }
  }
}


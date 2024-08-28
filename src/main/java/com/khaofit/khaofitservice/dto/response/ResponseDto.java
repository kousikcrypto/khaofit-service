package com.khaofit.khaofitservice.dto.response;

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
}


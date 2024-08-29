package com.khaofit.khaofitservice.exceptions;

import com.khaofit.khaofitservice.dto.response.ResponseDto;
import com.khaofit.khaofitservice.response.BaseResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * this is a global custom handler .
 *
 * @author kousik manik
 */
@ControllerAdvice
public class CustomExceptionHandler {

  @Autowired
  private BaseResponse baseResponse;

  /**
   * Method to handle the validation issues.
   *
   * @param ex invalid argument.
   * @return ResponseDto.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = "message";
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Validation Error.", errors);
  }

  /**
   * this is handle method validation exception class .
   *
   * @param ex @{@link HandlerMethodValidationException}
   * @return @{@link ResponseDto}
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<?> handleMethodValidationExceptions(HandlerMethodValidationException ex) {
    // Capture the validation errors
    Map<String, String> errors = new HashMap<>();
    ex.getAllValidationResults().forEach(parameterValidationResult -> {
      parameterValidationResult.getResolvableErrors().forEach(messageSourceResolvable -> {
        String fieldName = "message"; // or you can get the specific field if needed
        String errorMessage = messageSourceResolvable.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });
    });
    return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Validation Error.", errors);
  }

  /**
   * Exception filter.
   *
   * @param ex {@link ConstraintViolationException}
   * @return @ResponseEntity
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<?> handleConstraintViolationExceptions(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String fieldName = "message";
      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    }
    return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Validation Error.", errors);
  }


  /**
   * this is a method not support error handler .
   *
   * @param ex @{@link HttpRequestMethodNotSupportedException}
   * @param request @{@link WebRequest}
   * @return @{@link ResponseEntity}
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                      WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
    body.put("error", "Method Not Allowed");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false).replace("uri=", ""));

    return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * this is a global exception method .
   *
   * @param ex @{@link Exception}
   * @param request @{@link WebRequest}
   * @return @{@link ResponseEntity}
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false).replace("uri=", ""));

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

package com.khaofit.khaofitservice.exceptions;

/**
 * Customize exception class for khaoFit.
 *
 * @author Kousik manik
 */
public class KhaoFitException extends Exception {
  private String message;

  public KhaoFitException() {
    super();
  }

  public KhaoFitException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  public void setMessage(String message) {
    this.message = message;
  }
}


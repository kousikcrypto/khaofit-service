package com.khaofit.khaofitservice.enums;

/**
 * User Status Enums.
 *
 * @author kousik manik
 */
public enum UserStatus {
  ACTIVE("active"),
  INACTIVE("inactive");

  private final String value;

  private UserStatus(String value) {
    this.value = value;
  }
}

package com.khaofit.khaofitservice.enums;

/**
 * this is a user type enum class .
 *
 * @author kousik manik
 */
public enum UserType {

  USER("user"),
  GYM("gym"),
  INFLUENCERS("influencers");

  private final String value;

  private UserType(String value) {
    this.value = value;
  }

}

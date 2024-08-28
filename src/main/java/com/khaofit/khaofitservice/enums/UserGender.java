package com.khaofit.khaofitservice.enums;

/**
 * UserGender enum .
 *
 * @author kousik manik
 */
public enum UserGender {

  MALE("male"),
  FEMALE("female"),

  OTHERS("others");

  private final String value;

  private UserGender(String value) {
    this.value = value;
  }

}

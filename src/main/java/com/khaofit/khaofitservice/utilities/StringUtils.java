package com.khaofit.khaofitservice.utilities;

import org.springframework.stereotype.Component;

/**
 * String Utility class.
 *
 * @author kousik manik
 */
@Component
public class StringUtils {
  public static boolean isNotNullAndNotEmpty(String s) {
    return s != null && !s.isEmpty();
  }
}

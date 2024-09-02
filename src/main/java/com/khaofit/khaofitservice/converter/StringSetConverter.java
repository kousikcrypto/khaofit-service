package com.khaofit.khaofitservice.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Set;
import org.springframework.util.CollectionUtils;

/**
 * StringSetConverter .
 */
@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

  @Override
  public String convertToDatabaseColumn(Set<String> list) {
    return CollectionUtils.isEmpty(list) ? null : String.join(",", list);
  }

  @Override
  public Set<String> convertToEntityAttribute(String joined) {
    if (joined == null) {
      return null;
    }
    return Set.of(joined.split(","));
  }

}

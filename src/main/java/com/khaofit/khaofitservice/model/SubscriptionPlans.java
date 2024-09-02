package com.khaofit.khaofitservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khaofit.khaofitservice.converter.StringSetConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import ulid4j.Ulid;

/**
 * SubscriptionPlans model class .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "subscription_plans",
    schema = "master",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "ulid"})
    }
)
public class SubscriptionPlans {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "ulid", nullable = false)
  private String ulId;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "item_limit", nullable = false)
  private Integer itemLimit;

  @Convert(converter = StringSetConverter.class)
  @Column(name = "category", nullable = false, columnDefinition = "TEXT")
  private Set<String> category;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;


  @PrePersist
  private void beforeInsert() {
    this.setUlId(new Ulid().next());
    this.setCreatedAt(OffsetDateTime.now(ZoneOffset.of("+05:30")));
    this.setUpdatedAt(OffsetDateTime.now(ZoneOffset.of("+05:30")));
    this.setActive(true);
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(OffsetDateTime.now(ZoneOffset.of("+05:30")));
  }

  /**
   * Custom getter for createdAt to always return in IST .
   *
   * @return @ {@link OffsetDateTime}
   */
  public OffsetDateTime getCreatedAt() {
    if (createdAt != null) {
      return createdAt.withOffsetSameInstant(ZoneOffset.of("+05:30"));
    }
    return null;
  }

  /**
   * Custom getter for updatedAt to always return in IST .
   *
   * @return @ {@link OffsetDateTime}
   */
  public OffsetDateTime getUpdatedAt() {
    if (updatedAt != null) {
      return updatedAt.withOffsetSameInstant(ZoneOffset.of("+05:30"));
    }
    return null;
  }


}

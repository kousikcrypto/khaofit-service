package com.khaofit.khaofitservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khaofit.khaofitservice.enums.UserGender;
import com.khaofit.khaofitservice.enums.UserStatus;
import com.khaofit.khaofitservice.enums.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ulid4j.Ulid;

/**
 * Users model class.
 *
 * @author kousik
 */
@Entity
@Getter
@Setter
@Table(
    name = "users",
    schema = "khaofit",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mobile_number", "ulid", "referralCode"})
    }
)
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "ulid", nullable = false)
  private String ulId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "middle_name")
  private String middleName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "date_of_birth")
  private String dateOfBirth;

  @Column(name = "mobile_number")
  private String mobileNumber;

  @Column(name = "email_id")
  private String emailId;

  @Column(name = "gender")
  @Enumerated(EnumType.STRING)
  private UserGender gender;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "user_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserType userType;

  @Column(name = "referral_code", nullable = false)
  private String referralCode;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<BmiDetails> bmiDetails = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<ReferralDetails> referralDetails = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<UserSubscriptionDetails> userSubscriptionDetails = new ArrayList<>();

  @PrePersist
  private void beforeInsert() {
    this.setUlId(new Ulid().next());
    this.setCreatedAt(OffsetDateTime.now(ZoneOffset.of("+05:30")));
    this.setUpdatedAt(OffsetDateTime.now(ZoneOffset.of("+05:30")));
    this.setStatus(UserStatus.ACTIVE);
    this.setUserType(UserType.USER);
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


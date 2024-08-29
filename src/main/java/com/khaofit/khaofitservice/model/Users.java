package com.khaofit.khaofitservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khaofit.khaofitservice.enums.UserGender;
import com.khaofit.khaofitservice.enums.UserStatus;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ulid4j.Ulid;

/**
 * Users model class.
 *
 * @author avinash
 */
@Entity
@Getter
@Setter
@Table(
    name = "users",
    schema = "khaofit",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mobile_number", "ulid"})
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

  @Column(name = "created_at")
  private Date createdAt;

  @Column(name = "updated_at")
  private Date updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BmiDetails> bmiDetails = new ArrayList<>();

  @PrePersist
  private void beforeInsert() {
    this.setUlId(new Ulid().next());
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
    this.setStatus(UserStatus.ACTIVE);
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }
}


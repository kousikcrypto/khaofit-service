package com.khaofit.khaofitservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khaofit.khaofitservice.enums.UserGender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * this is a bmi details table .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "bmi_details",
    schema = "khaofit")
public class BmiDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "height")
  private double height;

  @Column(name = "weight")
  private double weight;

  @Column(name = "gender")
  @Enumerated(EnumType.STRING)
  private UserGender gender;

  @Column(name = "age", columnDefinition = "INT DEFAULT 0")
  private Integer age;

  @Column(name = "category")
  private String category;

  @Column(name = "bmi_value")
  private double bmiValue;

  @Column(name = "active")
  private boolean active;

  @Column(name = "created_at")
  private Date createdAt;

  @Column(name = "updated_at")
  private Date updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private Users user;


  @PrePersist
  private void beforeInsert() {
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
    this.setActive(true);
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }
}

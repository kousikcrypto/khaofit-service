package com.khaofit.khaofitservice.repository;

import com.khaofit.khaofitservice.model.UserSubscriptionDetails;
import com.khaofit.khaofitservice.model.Users;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * this is a repository class for user subscription details .
 *
 * @author kousik manik
 */
@Repository
public interface UserSubscriptionDetailsRepository extends JpaRepository<UserSubscriptionDetails, Long> {

  List<UserSubscriptionDetails> findByUserAndActiveTrue(Users user);

  List<UserSubscriptionDetails> findByUserOrderByCreatedAtDesc(Users user);

  List<UserSubscriptionDetails> findByActiveTrue();

  @Transactional
  @Modifying
  @Query("UPDATE UserSubscriptionDetails u SET u.active = false WHERE u.subscriptionEndTime < :currentDate"
      + " AND u.active = true")
  int deactivateExpiredSubscriptions(LocalDate currentDate);

}

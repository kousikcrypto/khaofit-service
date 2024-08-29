package com.khaofit.khaofitservice.repository;

import com.khaofit.khaofitservice.model.BmiDetails;
import com.khaofit.khaofitservice.model.Users;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * this is a bmi details repository class .
 *
 * @author kousik manik
 */
@Repository
public interface BmiDetailsRepository extends JpaRepository<BmiDetails, Long> {

  // Find all active BmiDetails for a specific user
  List<BmiDetails> findByUserAndActive(Users user, boolean active);

  @Modifying
  @Transactional
  @Query("UPDATE BmiDetails b SET b.active = false WHERE b.user.id = :userId AND b.active = true")
  int deactivateActiveBmiDetailsForUser(Long userId);

}

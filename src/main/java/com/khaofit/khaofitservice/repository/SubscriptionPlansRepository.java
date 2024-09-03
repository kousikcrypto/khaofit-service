package com.khaofit.khaofitservice.repository;

import com.khaofit.khaofitservice.model.SubscriptionPlans;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a repository class for SubscriptionPlans .
 *
 * @author kousik manik
 */
@Repository
public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, Long> {

  Optional<SubscriptionPlans> findByName(String name);

  Optional<SubscriptionPlans> findByUlIdAndActiveTrue(String ulid);

  List<SubscriptionPlans> findByActiveTrue();

}

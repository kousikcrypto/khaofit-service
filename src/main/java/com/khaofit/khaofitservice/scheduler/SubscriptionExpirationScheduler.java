package com.khaofit.khaofitservice.scheduler;

import com.khaofit.khaofitservice.repository.UserSubscriptionDetailsRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * this is class for scheduler which user are expired their subscription .
 *
 * @author kousik manik
 */
@Component
public class SubscriptionExpirationScheduler {

  @Autowired
  private UserSubscriptionDetailsRepository userSubscriptionDetailsRepository;

  private static final Logger logger = LoggerFactory.getLogger(SubscriptionExpirationScheduler.class);

  /**
   * Scheduler set to run at midnight every day at 12:00 AM .
   */
  @Scheduled(cron = "0 0 0 * * ?")  // Every day at 12:00 AM
  public void deactivateExpiredSubscriptions() {
    try {
      LocalDate currentDate = LocalDate.now();
      int updatedCount = userSubscriptionDetailsRepository.deactivateExpiredSubscriptions(currentDate);
      logger.info("Successfully deactivated {} expired subscriptions.", updatedCount);
    } catch (Exception e) {
      logger.error("Error occurred while deactivating expired subscriptions: {}", e.getMessage(), e);
    }
  }

}

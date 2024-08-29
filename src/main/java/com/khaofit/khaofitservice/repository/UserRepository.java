package com.khaofit.khaofitservice.repository;

import com.khaofit.khaofitservice.model.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a repository class for users database .
 *
 * @author kousik mnaik
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

  Optional<Users> findByMobileNumber(String mobileNumber);

  Optional<Users> findByUlId(String ulid);

}

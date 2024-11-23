package com.ricardo.scalable.ecommerce.platform.user_service.repositories;

import com.ricardo.scalable.ecommerce.platform.user_service.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}

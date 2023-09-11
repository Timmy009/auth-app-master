package com.example.ecommerce.repository;
import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
   Optional<User>findUserByEmail(String email);

   boolean existsByEmail(String email);

    User findUserByFirstName(String userName);

}


package com.app.repositories;

import com.app.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1  AND  u.isRegistered = false")
    Optional<User> nonRegisteredUser(String email);

    List<User> findAllByEmailIn(List<String> emails);

}

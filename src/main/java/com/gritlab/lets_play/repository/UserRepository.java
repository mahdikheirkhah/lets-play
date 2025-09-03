package com.gritlab.lets_play.repository;

import com.gritlab.lets_play.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // Find a user by their email address
    Optional<User> findByEmail(String email);
}
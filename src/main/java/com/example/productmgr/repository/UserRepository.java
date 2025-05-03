package com.example.productmgr.repository;

import com.example.productmgr.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username")
    boolean existsByUsername(String username);
}
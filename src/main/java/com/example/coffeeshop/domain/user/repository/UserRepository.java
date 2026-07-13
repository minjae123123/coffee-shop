package com.example.coffeeshop.domain.user.repository;

import com.example.coffeeshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
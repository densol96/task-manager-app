package com.accenture.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.PremiumAccount;

public interface PremiumAccountRepository extends JpaRepository<PremiumAccount, Long> {
    Optional<PremiumAccount> findByUserId(Long userId);
}

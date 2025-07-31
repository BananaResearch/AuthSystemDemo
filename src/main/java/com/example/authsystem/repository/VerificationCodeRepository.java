package com.example.authsystem.repository;

import com.example.authsystem.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmailAndCodeAndTypeAndStatusAndExpireTimeAfter(
            String email, String code, String type, String status, LocalDateTime currentTime);
    List<VerificationCode> findByEmailAndTypeAndStatusOrderByExpireTimeDesc(
            String email, String type, String status);
    void deleteByExpireTimeBefore(LocalDateTime currentTime);
}
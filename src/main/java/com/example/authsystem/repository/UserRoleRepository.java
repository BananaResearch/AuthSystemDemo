package com.example.authsystem.repository;

import com.example.authsystem.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
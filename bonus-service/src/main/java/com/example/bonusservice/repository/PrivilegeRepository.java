package com.example.bonusservice.repository;

import com.example.bonusservice.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {  // 改为Integer
    Optional<Privilege> findByUsername(String username);
}
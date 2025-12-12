package com.example.bonusservice.repository;

import com.example.bonusservice.entity.PrivilegeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeHistoryRepository extends JpaRepository<PrivilegeHistory, Integer> {  // 改为Integer
    List<PrivilegeHistory> findByPrivilegeId(Integer privilegeId);  // 改为Integer
}
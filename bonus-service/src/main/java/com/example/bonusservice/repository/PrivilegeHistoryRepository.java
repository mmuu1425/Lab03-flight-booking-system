package com.example.bonusservice.repository;

import com.example.bonusservice.entity.PrivilegeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrivilegeHistoryRepository extends JpaRepository<PrivilegeHistory, Integer> {
    List<PrivilegeHistory> findByPrivilegeId(Integer privilegeId);

    // 添加这个方法用于幂等性检查
    @Query("SELECT ph FROM PrivilegeHistory ph WHERE ph.ticketUid = :ticketUid")
    Optional<PrivilegeHistory> findByTicketUid(@Param("ticketUid") UUID ticketUid);

    // 或者使用Spring Data JPA的命名查询（如果支持）
    // Optional<PrivilegeHistory> findByTicketUid(UUID ticketUid);
}
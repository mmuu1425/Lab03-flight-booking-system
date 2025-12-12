package com.example.ticketservice.repository;

import com.example.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByUsernameOrderByIdDesc(String username);
    Optional<Ticket> findByTicketUidAndUsername(UUID ticketUid, String username);
    boolean existsByTicketUid(UUID ticketUid);
}
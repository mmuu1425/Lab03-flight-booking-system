package com.example.bonusservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "privilege_history")
public class PrivilegeHistory {

    @JsonIgnore  // 防止循环引用
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id", nullable = false)
    private Privilege privilege;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ticket_uid", nullable = false)
    private UUID ticketUid;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "balance_diff", nullable = false)
    private Integer balanceDiff;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    // Constructors
    public PrivilegeHistory() {}

    public PrivilegeHistory(Privilege privilege, UUID ticketUid, Integer balanceDiff, String operationType) {
        this.privilege = privilege;
        this.ticketUid = ticketUid;
        this.datetime = LocalDateTime.now();
        this.balanceDiff = balanceDiff;
        this.operationType = operationType;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Privilege getPrivilege() { return privilege; }
    public void setPrivilege(Privilege privilege) { this.privilege = privilege; }

    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }

    public Integer getBalanceDiff() { return balanceDiff; }
    public void setBalanceDiff(Integer balanceDiff) { this.balanceDiff = balanceDiff; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
}
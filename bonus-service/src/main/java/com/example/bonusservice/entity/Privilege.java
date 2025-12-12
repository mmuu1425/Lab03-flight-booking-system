package com.example.bonusservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "privilege")
public class Privilege {

    @JsonIgnore  // 防止循环引用
    @OneToMany(mappedBy = "privilege", fetch = FetchType.LAZY)
    private List<PrivilegeHistory> history;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true, nullable = false, length = 80)
    private String username;

    @Column(name = "status", nullable = false, length = 80)
    private String status = "BRONZE";

    @Column(name = "balance")
    private Integer balance = 0;

    // Constructors
    public Privilege() {}

    public Privilege(String username) {
        this.username = username;
    }

    public Privilege(String username, String status, Integer balance) {
        this.username = username;
        this.status = status;
        this.balance = balance;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
}
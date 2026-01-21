package com.ledgerflow.wallet_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "wallets")
@AllArgsConstructor // Generates the constructor for tests
@NoArgsConstructor  // Generates empty constructor for JPA
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
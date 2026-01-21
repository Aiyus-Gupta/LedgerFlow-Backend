package com.ledgerflow.wallet_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;       // Who owns this transaction?
    private String type;         // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private BigDecimal amount;   // How much?
    private String description;  // "Transfer to Bob"
    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
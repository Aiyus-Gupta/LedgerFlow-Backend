package com.ledgerflow.wallet_service.repository;

import com.ledgerflow.wallet_service.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<Entity, ID Type>
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // MAGIC: Spring automatically writes the SQL for this!
    // It sees "findByUserId" and runs: SELECT * FROM wallets WHERE user_id = ?
    Optional<Wallet> findByUserId(String userId);

    // Checks if a user already has a wallet (returns true/false)
    boolean existsByUserId(String userId);
}
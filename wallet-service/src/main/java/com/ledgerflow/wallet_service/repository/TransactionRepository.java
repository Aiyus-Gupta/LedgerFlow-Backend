package com.ledgerflow.wallet_service.repository;

import com.ledgerflow.wallet_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC"
    List<Transaction> findByUserIdOrderByTimestampDesc(String userId);
}
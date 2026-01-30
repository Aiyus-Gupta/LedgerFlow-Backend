package com.ledgerflow.wallet_service.controller;

import com.ledgerflow.wallet_service.model.Transaction;
import com.ledgerflow.wallet_service.model.Wallet;
import com.ledgerflow.wallet_service.repository.TransactionRepository;
import com.ledgerflow.wallet_service.repository.WalletRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin; // <--- ADD THIS IMPORT

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*") // <--- ADD THIS ANNOTATION (Fixes the CORS Error)
public class WalletController {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletController(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // --- READ OPERATIONS ---

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found!"));
    }

    @GetMapping("/{userId}/history")
    public List<Transaction> getHistory(@PathVariable String userId) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // --- WRITE OPERATIONS ---

    @PostMapping("/create")
    public Wallet createWallet(@RequestParam String userId) {
        if (walletRepository.existsByUserId(userId)) {
            throw new RuntimeException("Wallet already exists!");
        }
        Wallet newWallet = new Wallet();
        newWallet.setUserId(userId);
        newWallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(newWallet);
    }

    @PostMapping("/deposit")
    public Wallet deposit(@RequestParam String userId, @RequestParam BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found!"));
        
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        recordTransaction(userId, "DEPOSIT", amount, "Added money to wallet");

        return wallet;
    }

    // --- THE FIXED TRANSFER METHOD (Only one version!) ---
    @PostMapping("/transfer")
    @Transactional
    public String transfer(@RequestParam String fromUser, 
                           @RequestParam String toUser, 
                           @RequestParam BigDecimal amount) {

        // 1. Block self-transfers
        if (fromUser.equals(toUser)) {
            throw new RuntimeException("Cannot transfer to yourself!");
        }

        // 2. Find both wallets
        Wallet sender = walletRepository.findByUserId(fromUser)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        Wallet receiver = walletRepository.findByUserId(toUser)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 3. Check Balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds!");
        }

        // 4. Move Money
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);

        // 5. Record Transactions
        recordTransaction(fromUser, "TRANSFER_OUT", amount.negate(), "Sent to " + toUser);
        recordTransaction(toUser, "TRANSFER_IN", amount, "Received from " + fromUser);

        return "Transfer successful!";
    }

    // Helper method
    private void recordTransaction(String userId, String type, BigDecimal amount, String desc) {
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setType(type);
        txn.setAmount(amount);
        txn.setDescription(desc);
        transactionRepository.save(txn);
    }
}
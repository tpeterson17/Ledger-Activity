package com.twou.LedgerAPI.repository;

import com.twou.LedgerAPI.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT SUM(transaction_value) FROM transaction WHERE soft_delete = false", nativeQuery = true)
    public BigDecimal getSumOfAllTransactions();
}

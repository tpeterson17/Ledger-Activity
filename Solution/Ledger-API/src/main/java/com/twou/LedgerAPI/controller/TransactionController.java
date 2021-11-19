package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.exceptions.NotFoundException;
import com.twou.LedgerAPI.model.Transaction;
import com.twou.LedgerAPI.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @GetMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        if (foundTransaction.isPresent()) {
            return foundTransaction;

        }
        else {
            throw new NotFoundException("Transaction not found");
        }
    }

    @GetMapping("/transaction")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        List<Transaction> foundTransactions = transactionRepository.findAll();

        if (foundTransactions.isEmpty()) {
            throw new NotFoundException("No transactions found");
        }
        else {
            return transactionRepository.findAll();
        }
    }

    @GetMapping("/sum")
    @ResponseStatus(value = HttpStatus.OK)
    public BigDecimal getSumOfAllTransactions() {
        BigDecimal sum = transactionRepository.getSumOfAllTransactions();

        if (sum == null) {
            return BigDecimal.ZERO;
        }
        else {
            return sum;
        }
    }

    @PutMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTransactionValueById(@PathVariable Long id, @RequestBody Transaction transaction) {
        if (transaction.getId() != id) {
            throw new IllegalArgumentException("The ID in the URL must match the ID in the request body.");
        }

        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        if (foundTransaction.isPresent()) {
            foundTransaction.get().setTransactionValue(transaction.getTransactionValue());
            transactionRepository.save(foundTransaction.get());
        }
        else {
            throw new NotFoundException("Transaction not found");
        }
    }

    @DeleteMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTransactionById(@PathVariable Long id) {
        try {
            transactionRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Transaction not found");
        }
    }
}

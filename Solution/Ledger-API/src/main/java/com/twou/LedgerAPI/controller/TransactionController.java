package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.model.Transaction;
import com.twou.LedgerAPI.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    @Autowired
    TransactionRepository transactionRepository;

    // POST SINGLE TRANSACTION
    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // GET BY ID

    // GET ALL

    // GET SUM OF ALL TRANSACTIONS

    // PUT (UPDATE TRANSACTION VALUE BY TRANSACTION ID)

    // DELETE (SOFT DELETE)
}

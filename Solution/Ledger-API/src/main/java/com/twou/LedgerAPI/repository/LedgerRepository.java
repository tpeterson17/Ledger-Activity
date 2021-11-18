package com.twou.LedgerAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.twou.LedgerAPI.model.Ledger;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {

}

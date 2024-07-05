package com.koios.expense_tracker.repository;

import com.koios.expense_tracker.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepo extends JpaRepository<Balance, Integer> {
}


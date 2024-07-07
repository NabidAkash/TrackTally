package com.koios.expense_tracker.service;

import com.koios.expense_tracker.enums.PaymentMethod;
import com.koios.expense_tracker.model.Expense;
import com.koios.expense_tracker.model.User;
import com.koios.expense_tracker.model.Wallet;
import com.koios.expense_tracker.repository.ExpenseRepo;
import com.koios.expense_tracker.repository.UserRepo;
import com.koios.expense_tracker.repository.WalletRepo;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ExpenseService {
    private final ExpenseRepo expenseRepo;
    private final UserRepo userRepo;
    private final WalletRepo walletRepo;

    public ExpenseService(ExpenseRepo expenseRepo, UserRepo userRepo, WalletRepo walletRepo) {
        this.expenseRepo = expenseRepo;
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
    }

    @Transactional
    public ResponseEntity<?> addExpense(Expense expense) {
        User user = userRepo.findById(1).get();
        if(user.getWallet() == null){
            Wallet wallet = new Wallet();
            user.setWallet(wallet);
            walletRepo.save(wallet);
            userRepo.save(user);
        }

        Wallet wallet = user.getWallet();

        if(expense.getPaymentMethod() == PaymentMethod.BANK) {
            wallet.setBankBalance(wallet.getBankBalance() - expense.getAmount());
        }
        else if(expense.getPaymentMethod() == PaymentMethod.BKASH) {
            wallet.setBkashBalance(wallet.getBkashBalance() - expense.getAmount());
        } else if (expense.getPaymentMethod() == PaymentMethod.NAGAD) {
            wallet.setNagadBalance(wallet.getNagadBalance() - expense.getAmount());
        }
        walletRepo.save(wallet);
        user.setWallet(wallet);
        userRepo.save(user);
        expenseRepo.save(expense);
       return new ResponseEntity<>(userRepo.findAll(), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getAllExpenses() {
        if (expenseRepo.findAll().isEmpty()) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(expenseRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> getBalance(Integer id) {
        if(userRepo.findById(id).isPresent()) {
            return new ResponseEntity<>(userRepo.findById(id).get().getWallet(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
}

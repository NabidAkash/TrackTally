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
        Integer id = 1;
        User user = userRepo.findById(id).get();
        if (user.getWallet() == null) {
            Wallet wallet = new Wallet();
            user.setWallet(wallet);
            walletRepo.save(wallet);
            userRepo.save(user);
        }

        Wallet wallet = user.getWallet();

        if (expense.getPaymentMethod() == PaymentMethod.BANK) {
            int currentBalance = wallet.getBankBalance() - expense.getAmount();
            if(currentBalance >= 0) {
                wallet.setBankBalance(currentBalance);
            }
            else {
                return new ResponseEntity<>("Insufficient Balance", HttpStatus.BAD_REQUEST);
            }
        }
        else if (expense.getPaymentMethod() == PaymentMethod.BKASH) {
            int currentBalance = wallet.getBkashBalance() - expense.getAmount();
            if(currentBalance >= 0) {
                wallet.setBkashBalance(currentBalance);
            }
            else {
                return new ResponseEntity<>("Insufficient Balance", HttpStatus.BAD_REQUEST);
            }
        }
        else if (expense.getPaymentMethod() == PaymentMethod.NAGAD) {
            int currentBalance = wallet.getNagadBalance() - expense.getAmount();
            if(currentBalance >= 0) {
                wallet.setNagadBalance(currentBalance);
            }
            else {
                return new ResponseEntity<>("Insufficient Balance", HttpStatus.BAD_REQUEST);
            }
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
        if (userRepo.findById(id).isPresent()) {
            return new ResponseEntity<>(userRepo.findById(id).get().getWallet(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteExpense(Integer id) {
        if (userRepo.findById(1).isPresent()) {
            Wallet wallet = userRepo.findById(1).get().getWallet();
            if (expenseRepo.findById(id).isPresent()) {
                String pMethod = expenseRepo.findById(id).get().getPaymentMethod().toString();
                int amount = expenseRepo.findById(id).get().getAmount();
                expenseRepo.deleteById(id);
                switch (pMethod) {
                    case "BANK" -> wallet.setBankBalance(wallet.getBankBalance() + amount);
                    case "NAGAD" -> wallet.setNagadBalance(wallet.getNagadBalance() + amount);
                    case "BKASH" -> wallet.setBkashBalance(wallet.getBkashBalance() + amount);
                }
                walletRepo.save(wallet);
                return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
            }
            return new ResponseEntity<>("Not Found Expense", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not Found User", HttpStatus.NOT_FOUND);
    }
}

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
        System.out.println("HI");
        User user = new User();
        user.setName("Nabid Anzum Akash");
        user.setEmail("nabidanzum@gmail.com");
        user.setUsername("nabidanzum");
        user.setPassword("nabidanzum");

        Wallet  wallet = new Wallet();

        /*
        string userName = jwtService.getUserNameFromToken(Token);
        User user = userRepo.findByUserName(userName);
        user.getBalance();
        */


        if(expense.getPaymentMethod() == PaymentMethod.BANK) {
            wallet.setBankBalance(wallet.getBankBalance() - expense.getAmount());
        }
        walletRepo.save(wallet);
        user.setWallet(wallet);
        userRepo.save(user);
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
            return new ResponseEntity<>(userRepo.findById(id).get().getWallet().getBankBalance(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
}

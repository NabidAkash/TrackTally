package com.koios.expense_tracker.service;

import com.koios.expense_tracker.model.Expense;
import com.koios.expense_tracker.model.User;
import com.koios.expense_tracker.repository.ExpenseRepo;
import com.koios.expense_tracker.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ExpenseService {
    private final ExpenseRepo expenseRepo;
    private final UserRepo userRepo;

    public ExpenseService(ExpenseRepo expenseRepo, UserRepo userRepo) {
        this.expenseRepo = expenseRepo;
        this.userRepo = userRepo;
    }

    public ResponseEntity<?> addExpense(Expense expense) {
       try {
           expenseRepo.save(expense);
           User user = new User();
           user.setName("Nabid Anzum Akash");
           user.setEmail("nabidanzum@gmail.com");
           user.setUsername("nabidanzum");
           user.setPassword("nabidanzum");
           user.setBalance(-expense.getAmount());
           userRepo.save(user);

       }
       catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
       return new ResponseEntity<>("Expense Added", HttpStatus.CREATED);
    }

    public ResponseEntity<?> getAllExpenses() {
        if (expenseRepo.findAll().isEmpty()) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(expenseRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> getBalance(Integer id) {
        if(userRepo.findById(id).isPresent()) {
            return new ResponseEntity<>(userRepo.findById(id).get().getBalance(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

}

package com.app.controllers;

import com.app.entites.Expense;
import com.app.entites.User;
import com.app.payloads.AddExpenseRequestDTO;
import com.app.payloads.AddExpenseResponseDTO;
import com.app.payloads.ResponseStatus;
import com.app.repositories.UserRepo;
import com.app.services.ExpenseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/expense")
@SecurityRequirement(name = "Splitwise Application")
public class ExpenseController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    ExpenseService expenseService;

    @PostMapping("/addExpense")
    public AddExpenseResponseDTO addExpense(Principal principal, @RequestBody AddExpenseRequestDTO expenseDTO){

        Long addedByUserId = this.userRepo.findByEmail(principal.getName()).get().getId();

        AddExpenseResponseDTO response = new AddExpenseResponseDTO();
        try{
            Expense expense = expenseService.addExpense(
                    expenseDTO.getGroupId(),
                    expenseDTO.getTotalAmount(),
                    expenseDTO.getAmountOwedBy(),
                    expenseDTO.getAmountPaidBy(),
                    addedByUserId,
                    expenseDTO.getDescription()
            );
            response.setExpenseId(expense.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;

    }



}

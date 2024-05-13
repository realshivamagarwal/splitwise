package com.app.services;

import com.app.entites.Expense;
import com.app.entites.Group;
import com.app.entites.User;
import com.app.payloads.AddExpenseRequestDTO;
import com.app.payloads.AddGroupRequestDTO;

import java.util.Map;

public interface ExpenseService {

    Expense addExpense(AddExpenseRequestDTO expenseRequestDTO, String createdUserEmail, Long groupId);


}

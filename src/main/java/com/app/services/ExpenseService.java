package com.app.services;

import com.app.entites.Expense;
import com.app.entites.Group;
import com.app.entites.User;
import com.app.payloads.*;

import java.util.Map;

public interface ExpenseService {

    Expense addExpense(AddExpenseRequestDTO expenseRequestDTO, Long groupId, Long userId);

    ExpenseAmountDTO expenseSettleUpForUser(Long groupId, Long expenseId, Long userId);

    Expense addTransaction(AddTransactionRequestDTO transaction, Long groupId, Long userId);

    boolean deleteExpense(Long selfUserId, Long groupId, Long expenseId);
}

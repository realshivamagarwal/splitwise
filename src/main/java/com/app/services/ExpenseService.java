package com.app.services;

import com.app.entites.Expense;
import com.app.payloads.*;

public interface ExpenseService {

    Expense addExpense(AddExpenseRequestDTO expenseRequestDTO, Long groupId, Long userId);

    ExpenseAmountForUserDTO expenseSettleUpForUser(Long groupId, Long expenseId, Long userId);

    Expense addTransaction(AddTransactionRequestDTO transaction, Long groupId, Long userId);

    boolean deleteExpense(Long selfUserId, Long groupId, Long expenseId);

    Expense addExpenseForFriend(AddExpenseFriendDTO expenseDTO, Long userId);


    GroupResponse getAllExpensesForGroup(Long groupId, Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}

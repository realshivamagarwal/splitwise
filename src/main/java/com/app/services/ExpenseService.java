package com.app.services;

import com.app.entites.Expense;

import java.util.Map;

public interface ExpenseService {

    public Expense addExpense(Long groupId,
                                  Long amount,
                                  Map<Long,Long> amountOwedBy,
                                  Map<Long,Long> amountPaidBy,
                                  Long createdById,
                                  String description);


}

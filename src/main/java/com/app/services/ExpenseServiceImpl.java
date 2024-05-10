package com.app.services;
import com.app.entites.Expense;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Override
    public Expense addExpense(Long groupId, Long amount, Map<Long, Long> amountOwedBy, Map<Long, Long> amountPaidBy, Long createdById, String description) {
        return null;
    }
}

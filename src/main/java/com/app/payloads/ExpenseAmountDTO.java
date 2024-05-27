package com.app.payloads;

import lombok.Data;

@Data
public class ExpenseAmountDTO {

    private Long expenseId;
    private Long totalExpenseAmount;
}

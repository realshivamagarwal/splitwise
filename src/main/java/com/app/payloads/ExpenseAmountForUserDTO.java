package com.app.payloads;

import lombok.Data;

@Data
public class ExpenseAmountForUserDTO {
    private Long expenseId;
    private Long userId;
    private Long paid_share;
    private Long owed_share;
    private Long totalExpenseAmount;
}

package com.app.payloads;

import com.app.entites.Expense;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetExpenseResponseDTO {

    private ExpenseAmountDTO expenseAmountDTO;

    private ResponseStatus status;
}

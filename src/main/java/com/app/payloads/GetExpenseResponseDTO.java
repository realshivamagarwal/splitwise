package com.app.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetExpenseResponseDTO {

    private ExpenseAmountForUserDTO expenseAmountDTO;

    private ResponseStatus status;
}

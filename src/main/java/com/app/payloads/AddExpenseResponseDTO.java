package com.app.payloads;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class AddExpenseResponseDTO {
    private ResponseStatus status;
    private Long expenseId;
}

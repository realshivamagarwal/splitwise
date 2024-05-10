package com.app.payloads;

import lombok.Data;

import java.util.Map;

@Data
public class AddExpenseRequestDTO {

    private String description;
    private String currency;
    private String image;
    private Map<Long,Long> amountPaidBy;
    private Map<Long,Long> amountOwedBy;

    private Long totalAmount;
    private Long groupId;

}

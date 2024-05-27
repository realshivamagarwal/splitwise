package com.app.payloads;

import lombok.Data;

import java.util.Map;

@Data
public class AddTransactionRequestDTO {

    private String description;
    private String currency;
    private String image;
    private Long amountPaidBy;
    private Long amountOwedBy;
    private Long totalAmount;

}

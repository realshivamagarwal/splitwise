package com.app.payloads;

import lombok.Data;

@Data
public class AddExpenseFriendDTO {
    private String description;
    private String currency;
    private String image;
    private Long amountPaidBy;
    private Long amountOwedBy;
    private Long totalAmount;

}

package com.app.payloads;

import lombok.Data;

import java.util.List;
@Data
public class GroupAmountDTO {
    private Long groupId;
    private List<Transaction> transactions;
    private Long totalGroupAmount;


}

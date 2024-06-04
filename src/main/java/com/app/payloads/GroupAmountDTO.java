package com.app.payloads;

import com.app.helper.Transaction;
import lombok.Data;

import java.util.List;
@Data
public class GroupAmountDTO {
    private Long groupId;
    private List<Transaction> transactions;
    private Long totalGroupAmount;


}

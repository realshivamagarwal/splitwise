package com.app.payloads;

import com.app.entites.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private Long from;
    private Long to;
    private Long amount;
}

package com.app.entites;
import jakarta.persistence.*;

@Entity
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currency; // Note: transactions are happening across same currency.
    private double amount;
}

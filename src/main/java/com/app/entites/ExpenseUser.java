package com.app.entites;

import com.app.enums.ExpenseUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="expense_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUser extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    private Long amount;
    @Enumerated(value = EnumType.STRING)
    private ExpenseUserType expenseUserType;

}

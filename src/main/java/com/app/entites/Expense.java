package com.app.entites;
import com.app.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends BaseModel {

    private String description;

    private String currency;

    private String image;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    private Long totalAmount;

    boolean isSettled = false;

    @DateTimeFormat
    private Date date;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToOne
    @JoinColumn(name = "last_updated_by")
    private User lastUpdatedBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "expense")
    private List<ExpenseUser> expenseUsers = new ArrayList<>();

}

package com.app.entites;
import com.app.enums.GroupPart;
import com.app.enums.GroupType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "`group`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group extends BaseModel {

    private String name;

    private String image;

    private boolean simplifyDebts;

    @Enumerated(EnumType.STRING)
    private GroupType type;

    private Long budget;

    @Enumerated(EnumType.STRING)
    private GroupPart part;

    @OneToMany(mappedBy = "group")
    private List<GroupUsers> groupUsers = new ArrayList<>();

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "group")
    private List<Expense> expenses = new ArrayList<>();
}

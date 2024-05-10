package com.app.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="group_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUsers extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

}

package com.app.entites;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {

    @Size(min = 5, max = 20, message = "Full Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[A-Za-z\s]+$", message = "Full Name must not contain numbers or special characters")
    private String fullName;

    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile Number must contain only Numbers")
    @Column(unique = true)
    private String mobileNumber;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String referalCode;

    private String currency;

    private boolean active;

    private boolean isRegistered;

    private String otp;

    private LocalDateTime otpGeneratedTime;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Role> roles =new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<GroupUsers> groups = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ExpenseUser> expenseUsers = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Group> createdGroups = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<GroupUsers> createdGroupUsers = new ArrayList<>();

    @OneToMany(mappedBy = "addedBy")
    private List<Expense> createdExpense = new ArrayList<>();

    @OneToMany(mappedBy = "lastUpdatedBy")
    private List<Expense> updatedExpense = new ArrayList<>();

}

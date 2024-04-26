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
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_full_name")
    @Size(min = 5, max = 20, message = "Full Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[A-Za-z\s]+$", message = "Full Name must not contain numbers or special characters")
    private String fullName;

    @Column(name = "user_mobile_number")
    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile Number must contain only Numbers")
    private String mobileNumber;

    @Email
    @Column(name = "user_email",unique = true, nullable = false)
    private String email;

    @Column(name = "user_pass")
    private String password;
    @Column(name = "user_referal")
    private String referalCode;
    @Column(name = "user_currency")
    private String currency;

    private boolean active;
    private String otp;
    private LocalDateTime otpGeneratedTime;
    @ManyToMany(fetch = FetchType.EAGER)
    List<Role> roles =new ArrayList<>();

}

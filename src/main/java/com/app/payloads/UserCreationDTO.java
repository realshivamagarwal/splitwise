package com.app.payloads;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserCreationDTO {

    private String fullName;
    private String mobileNumber;
    private String email;
    private String password;
    private String referalCode;
    private String currency;
    private Set<RoleDTO> roles = new HashSet<>();

}

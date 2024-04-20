package com.app.payloads;

import lombok.Data;

@Data
public class UserCreationDTO {

    private String fullName;
    private String mobileNumber;
    private String email;
    private String password;
    private String referalCode;
    private String currency;

}

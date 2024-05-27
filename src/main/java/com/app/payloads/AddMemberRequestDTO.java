package com.app.payloads;

import lombok.Data;

@Data
public class AddMemberRequestDTO {

    private Long groupId;
    private String email;
    private String mobileNumber;
}

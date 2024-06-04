package com.app.payloads;

import lombok.Data;

@Data
public class AddFriendRequestDTO {
    private String name;
    private String email;
    private String mobileNumber;
}

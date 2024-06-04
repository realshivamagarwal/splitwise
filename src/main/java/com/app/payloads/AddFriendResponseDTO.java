package com.app.payloads;

import lombok.Data;

@Data
public class AddFriendResponseDTO {

    private FriendResponse friendResponse;

    private ResponseStatus status;

}

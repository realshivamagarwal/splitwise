package com.app.payloads;

import lombok.Data;

@Data
public class GroupSettleUpResponseDTO {

    private ResponseStatus status;
    private String message;

}

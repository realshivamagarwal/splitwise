package com.app.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddGroupResponseDTO {

    private ResponseStatus status;
    private Long groupId;
}

package com.app.payloads;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetGroupResponseDTO {
    private ResponseStatus status;
    GroupAmountDTO groupAmountDTO;

}

package com.app.payloads;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetAllExpenseDTO {

    private ResponseStatus status;
    private GroupResponse group ;

}

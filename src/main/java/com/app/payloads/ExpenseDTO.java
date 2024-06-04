package com.app.payloads;
import lombok.Data;
import java.util.Date;
import java.util.List;
@Data
public class ExpenseDTO {

    private Long id;
    private String description;
    private String currency;
    private String image;
    private Long totalAmount;
    private Date createdAt;
    private UserDTO addedBy;
    private UserDTO deletedBy;
    private List<ExpenseAmountForUserDTO> users;
    private UserDTO lastUpdatedBy;
    private Long groupId;

}

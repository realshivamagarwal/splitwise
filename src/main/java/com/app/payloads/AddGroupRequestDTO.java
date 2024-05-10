package com.app.payloads;
import com.app.enums.GroupType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
@Data
public class AddGroupRequestDTO {
    private String name;
    private String image;
    private List<MemberDTO> members= new ArrayList<>();
    private boolean simplifyDebts;
    private GroupType type;
    private Long budget;
}

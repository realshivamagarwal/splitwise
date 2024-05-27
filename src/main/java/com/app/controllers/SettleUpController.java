package com.app.controllers;

import com.app.payloads.GetGroupResponseDTO;
import com.app.payloads.GroupSettleUpResponseDTO;
import com.app.payloads.ResponseStatus;
import com.app.payloads.Transaction;
import com.app.repositories.UserRepo;
import com.app.services.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/settleUp")
@SecurityRequirement(name = "Splitwise Application")
public class SettleUpController {

    @Autowired
    GroupService groupService;
    @Autowired
    UserRepo userRepo;

    @PostMapping("/{groupId}")
    public GroupSettleUpResponseDTO groupSettleUp(Principal principal, @PathVariable Long groupId) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        GroupSettleUpResponseDTO response = new GroupSettleUpResponseDTO();
        try {
            groupService.groupSettleUpWithTransactions(userId, groupId);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setMessage("All the transactions has beed done, the group is get settled up by this user");
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setMessage("Failure while settle up the transactions");
        }
        return response;
    }

}

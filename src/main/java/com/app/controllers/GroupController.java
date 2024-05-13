package com.app.controllers;

import com.app.entites.Group;
import com.app.entites.User;
import com.app.payloads.*;
import com.app.payloads.ResponseStatus;
import com.app.repositories.UserRepo;
import com.app.services.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Splitwise Application")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    UserRepo userRepo;
    @PostMapping("/createGroup")
    public AddGroupResponseDTO createGroup(Principal principal, @RequestBody AddGroupRequestDTO groupDTO){

        String createdUserEmail = principal.getName();
        User createdUser = this.userRepo.findByEmail(createdUserEmail).get();
        AddGroupResponseDTO response = new AddGroupResponseDTO();
        try {
            Group group = groupService.addGroup(groupDTO,createdUser);
            response.setGroupId(group.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}

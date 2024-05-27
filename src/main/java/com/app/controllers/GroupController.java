package com.app.controllers;

import com.app.entites.Group;
import com.app.payloads.*;
import com.app.payloads.ResponseStatus;
import com.app.repositories.UserRepo;
import com.app.services.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group")
@SecurityRequirement(name = "Splitwise Application")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    UserRepo userRepo;
    @PostMapping("/createGroup")
    public AddGroupResponseDTO createGroup(Principal principal, @RequestBody AddGroupRequestDTO groupDTO){

        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        AddGroupResponseDTO response = new AddGroupResponseDTO();
        try {
            Group group = groupService.addGroup(groupDTO,userId);
            response.setGroupId(group.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/addMember")
    public AddMemberResponseDTO addMemeber(Principal principal, AddMemberRequestDTO request){
        Long addedByUserId  = userRepo.findByEmail(principal.getName()).get().getId();
        AddMemberResponseDTO response = new AddMemberResponseDTO();
        try{
            Group group = groupService.addMember(request, addedByUserId);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }


    @PostMapping("/leaveGroup/{groupId}")
    public APIResponse leaveGroup(Principal principal, @PathVariable Long groupId){
        Long selfUserId  = userRepo.findByEmail(principal.getName()).get().getId();
        APIResponse response = new APIResponse();
        try{
            if (groupService.leaveGroup(selfUserId, groupId)) {
                response.setStatus(ResponseStatus.SUCCESS);
                response.setMessage("Successfully Leaved the group");
            } else {
                response.setMessage("Already Leaved the group");
                response.setStatus(ResponseStatus.FAILURE);
            }
        } catch (Exception e) {
            response.setMessage("Failure in leaving the group");
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/removeMember/{groupId}/{userId}")
    public APIResponse removeMember(Principal principal, @PathVariable Long groupId, @PathVariable Long userId){
        Long selfUserId  = userRepo.findByEmail(principal.getName()).get().getId();
        APIResponse response = new APIResponse();
        try{
            if (groupService.removeMember(selfUserId,userId,groupId)) {
                response.setStatus(ResponseStatus.SUCCESS);
                response.setMessage("Successfully removed the member from the group");
            } else {
                response.setMessage("member is already removed from the group");
                response.setStatus(ResponseStatus.FAILURE);
            }
        } catch (Exception e) {
            response.setMessage("Failure in removing the group");
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/deleteGroup/{groupId}")
    public APIResponse deleteGroup(Principal principal, @PathVariable Long groupId){
        Long selfUserId  = userRepo.findByEmail(principal.getName()).get().getId();
        APIResponse response = new APIResponse();
        try{
            if (groupService.deleteGroup(selfUserId,groupId)) {
                response.setStatus(ResponseStatus.SUCCESS);
                response.setMessage("Successfully removed the member from the group");
            } else {
                response.setMessage("member is already removed from the group");
                response.setStatus(ResponseStatus.FAILURE);
            }
        } catch (Exception e) {
            response.setMessage("Failure in removing the group");
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @GetMapping("/{groupId}")
    public GetGroupResponseDTO getGroupAmountForUser(Principal principal, @PathVariable Long groupId) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        GetGroupResponseDTO response = new GetGroupResponseDTO();
        try {
            GroupAmountDTO groupAmountDTO = groupService.groupAmountForUser(userId, groupId);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setGroupAmountDTO(groupAmountDTO);
        } catch (Exception e) {
           response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }









}

package com.app.controllers;

import com.app.entites.Expense;
import com.app.payloads.AddFriendRequestDTO;
import com.app.payloads.AddFriendResponseDTO;
import com.app.payloads.FriendResponse;
import com.app.payloads.ResponseStatus;
import com.app.repositories.UserRepo;
import com.app.services.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    UserService userService;

    public AddFriendResponseDTO addFriend(Principal principal, @RequestBody AddFriendRequestDTO addFriendRequestDTO){
        AddFriendResponseDTO response =  new AddFriendResponseDTO();
        String userEmail = principal.getName();
        try {
            FriendResponse friendResponse = userService.addFriend(addFriendRequestDTO, userEmail);
            response.setFriendResponse(friendResponse);
            response.setStatus(ResponseStatus.SUCCESS);
        }
        catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }


}

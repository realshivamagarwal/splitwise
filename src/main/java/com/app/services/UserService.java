package com.app.services;

import com.app.entites.Expense;
import com.app.entites.User;
import com.app.payloads.AddFriendRequestDTO;
import com.app.payloads.FriendResponse;
import com.app.payloads.UserCreationDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

   UserCreationDTO registerUser(UserCreationDTO userDTO);

   User getUser(Long id);

   List<User> getAllUsers();

   String verifiedWithEmail(String email);

   String verifyAccountWithOTP(String email, String otp);


   FriendResponse addFriend(AddFriendRequestDTO addFriendRequestDTO, String userEmail);
}

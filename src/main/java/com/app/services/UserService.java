package com.app.services;

import com.app.entites.User;
import com.app.payloads.UserCreationDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

   UserCreationDTO registerUser(UserCreationDTO userDTO);

   User getUser(Long id);

   List<User> getAllUsers();

   String verifiedWithEmail(String email);

   String verifyAccountWithOTP(String email, String otp);



}

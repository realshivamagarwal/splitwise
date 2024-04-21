package com.app.services;

import com.app.entites.User;
import com.app.payloads.UserCreationDTO;

public interface UserService {

   UserCreationDTO registerUser(UserCreationDTO userDTO);

   User getUser(Long id);


}

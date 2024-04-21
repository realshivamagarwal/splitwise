package com.app.services;

import com.app.entites.User;
import com.app.exception.APIException;
import com.app.payloads.UserCreationDTO;
import com.app.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepo userRepo;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserCreationDTO registerUser(UserCreationDTO userDTO) {

    try {
        User user = this.modelMapper.map(userDTO, User.class);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser = userRepo.save(user);
        UserCreationDTO resDto = modelMapper.map(createdUser, UserCreationDTO.class);

        return resDto;
    } catch (DataIntegrityViolationException e) {
        throw new APIException("User already exists with emailId: " + userDTO.getEmail());
    }
    }

    @Override
    public User getUser(Long id) {

       Optional<User> user = this.userRepo.findById(id);
       return user.get();
    }


}

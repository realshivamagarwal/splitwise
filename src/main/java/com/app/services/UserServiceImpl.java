package com.app.services;

import com.app.entites.User;
import com.app.payloads.UserCreationDTO;
import com.app.repositories.UserRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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
    public String registerUser(UserCreationDTO userDTO) {

        User user = this.modelMapper.map(userDTO, User.class);
        Optional<User> existUser = this.userRepo.findByEmail(user.getEmail());

        if(!existUser.isEmpty())
            return "user with this email already exists";

          user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser = userRepo.save(user);

        return "user is successfully created";
    }

    @Override
    public User getUser(Long id) {

       Optional<User> user = this.userRepo.findById(id);
       return user.get();
    }


}

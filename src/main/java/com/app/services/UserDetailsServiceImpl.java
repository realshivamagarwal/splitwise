package com.app.services;

import com.app.config.UserInfoConfig;
import com.app.entites.User;
import com.app.exception.ResourceNotFoundException;
import com.app.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(username);

        return user.map(UserInfoConfig::new).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

    }
}


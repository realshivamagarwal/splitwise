package com.app.controllers;
import com.app.entites.User;
import com.app.payloads.LoginCredentials;
import com.app.payloads.UserCreationDTO;
import com.app.security.JWTUtil;
import com.app.services.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody UserCreationDTO userCreationDTO){

        String response = userService.registerUser(userCreationDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@Valid @RequestBody LoginCredentials credentials) {

        UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
                credentials.getEmail(), credentials.getPassword());

        Authentication authentication = authenticationManager.authenticate(authCredentials);
        String token =null;
        if(authentication.isAuthenticated()) {
            token = jwtUtil.generateToken(credentials.getEmail());
        }
        return Collections.singletonMap("jwt-token", token);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        User user = userService.getUser(id);

        return new ResponseEntity<User>(user, HttpStatus.FOUND);
    }


}

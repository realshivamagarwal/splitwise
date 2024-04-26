package com.app.controllers;
import com.app.entites.User;
import com.app.payloads.LoginCredentials;
import com.app.payloads.UserCreationDTO;
import com.app.security.JWTUtil;
import com.app.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Splitwise Application")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/registerUser")
    public ResponseEntity registerUser(@RequestBody UserCreationDTO userCreationDTO){

        UserCreationDTO resDto = userService.registerUser(userCreationDTO);

        return new ResponseEntity<>(resDto,  HttpStatus.CREATED);
    }

    @PutMapping("/verify-account/email")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) {
        return new ResponseEntity<>(userService.verifyAccountWithOTP(email, otp), HttpStatus.OK);
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

    @GetMapping("/user/getUser/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        User user = userService.getUser(id);

        return new ResponseEntity<User>(user, HttpStatus.FOUND);
    }

    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();

        return new ResponseEntity<List<User>>(users, HttpStatus.FOUND);
    }


}

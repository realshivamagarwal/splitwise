package com.app.services;

import com.app.config.AppConstant;
import com.app.entites.Role;
import com.app.entites.User;
import com.app.exception.APIException;
import com.app.payloads.UserCreationDTO;
import com.app.repositories.RoleRepo;
import com.app.repositories.UserRepo;
import com.app.util.EmailUtil;
import com.app.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepo userRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;

    @Override
    public UserCreationDTO registerUser(UserCreationDTO userDTO) {

    try {
        User user = this.modelMapper.map(userDTO, User.class);

//      send otp to the mail address of the user to verify the user
//      Commenting it now for testing purpose, untill we configure the basic apis for splitwise application
        /*
            String otp = verifiedWithEmail(userDTO.getEmail());
            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
        */

        //Encode the Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepo.findById(AppConstant.USER_ID).get();
        user.getRoles().add(role);
        for (Role role1 : user.getRoles()) {
            if(role1.getRoleName().equals("ADMIN"))
                role1.setRoleId(AppConstant.ADMIN_ID);
        }
       Optional<User> nonRegistered = userRepo.nonRegisteredUser(user.getEmail());
        if(nonRegistered.isPresent()){
            user.setId(nonRegistered.get().getId());
        }
        user.setRegistered(true);
        user.setActive(true);
        User createdUser = userRepo.save(user);
        UserCreationDTO resDto = modelMapper.map(createdUser, UserCreationDTO.class);

        //Not Returning password to the UI
        resDto.setPassword(null);

        return resDto;
      } catch (DataIntegrityViolationException e) {
        throw new APIException("User already exists with emailId: " + userDTO.getEmail());
      }
    }

    public String verifiedWithEmail(String email){
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        return otp;
    }

    public String verifyAccountWithOTP(String email, String otp) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (1 * 60)) {
            user.setActive(true);
            userRepo.save(user);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }
    @Override
    public User getUser(Long id) {

       Optional<User> user = this.userRepo.findById(id);
       return user.get();
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepo.findAll();
    }


}

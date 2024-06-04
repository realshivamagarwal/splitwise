package com.app.services;

import com.app.config.AppConstant;
import com.app.entites.Group;
import com.app.entites.GroupUsers;
import com.app.entites.Role;
import com.app.entites.User;
import com.app.enums.GroupPart;
import com.app.enums.GroupType;
import com.app.exception.APIException;
import com.app.payloads.AddFriendRequestDTO;
import com.app.payloads.FriendResponse;
import com.app.payloads.UserCreationDTO;
import com.app.repositories.GroupRepo;
import com.app.repositories.GroupUsersRepo;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


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
    GroupUsersRepo groupUsersRepo;

    @Autowired
    GroupRepo groupRepo;
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
    public FriendResponse addFriend(AddFriendRequestDTO addFriendRequestDTO, String userEmail) {
        FriendResponse friendResponse =  new FriendResponse();
        try {
            Long addedByUserId = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new APIException("User not found"))
                .getId();

        User addedByUser = userRepo.findById(addedByUserId)
                .orElseThrow(() -> new APIException("User not found"));

        Optional<User> friendUser = userRepo.findByEmail(addFriendRequestDTO.getEmail());

        if (friendUser.isEmpty()) {
            User nonRegisteredUser = this.modelMapper.map(addFriendRequestDTO, User.class);
            nonRegisteredUser.setRegistered(true);
            User savedUser = this.userRepo.save(nonRegisteredUser);
            addedByUser.getFriends().add(savedUser);
        } else {
            addedByUser.getFriends().add(friendUser.get());
        }

        User savedUser = this.userRepo.save(addedByUser);

        // Create default group for this user
        Group defaultGroup = new Group();
        defaultGroup.setName(addedByUser.getFullName() + "-" + friendUser.get().getFullName() + "group");
        defaultGroup.setPart(GroupPart.FRIEND);
        defaultGroup.setActive(true);
        defaultGroup.setCreatedBy(addedByUser);
        defaultGroup.setSimplifyDebts(false);
        defaultGroup.setType(GroupType.OTHER);
        Group savedGroup = groupRepo.save(defaultGroup);

        // Add users to the default group
        GroupUsers groupUser1 = new GroupUsers();
        groupUser1.setUser(addedByUser);
        groupUser1.setGroup(savedGroup);
        groupUser1.setActive(true);
        groupUser1.setAddedBy(addedByUser);

        GroupUsers groupUser2 = new GroupUsers();
        groupUser2.setUser(friendUser.orElse(null));
        groupUser2.setGroup(savedGroup);
        groupUser2.setActive(true);
        groupUser2.setAddedBy(addedByUser);

        groupUsersRepo.saveAll(Arrays.asList(groupUser1, groupUser2));
    friendResponse.setGroupId(savedGroup.getId());
    friendResponse.setMessage("user added in the friend list and default group is created for this friend");
    }
       catch (Exception e) {
            friendResponse.setMessage("Unable to add user as friend: " + e.getMessage());
        }
        return friendResponse;
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

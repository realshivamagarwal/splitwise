package com.app.services;

import com.app.entites.Group;
import com.app.entites.GroupUsers;
import com.app.entites.User;
import com.app.exception.APIException;
import com.app.payloads.AddGroupRequestDTO;
import com.app.payloads.MemberDTO;
import com.app.repositories.GroupRepo;
import com.app.repositories.GroupUsersRepo;
import com.app.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepo userRepo;
    @Autowired
    GroupRepo groupRepo;
    @Autowired
    GroupUsersRepo groupUsersRepo;

    @Override
    public Group addGroup(AddGroupRequestDTO groupDTO, User createdUser) {

        List<MemberDTO> members = groupDTO.getMembers();

        //Adding created user of this group as an default member of this group
        members.add(this.modelMapper.map(createdUser,MemberDTO.class));

        // Saving non-register user from the list of members of the group
        // To-Do: sending register or invitaion link to the non-register user

        List<User> registeredUsers = userRepo.findAllByEmailIn(
                members.stream().map(MemberDTO::getEmail).collect(Collectors.toList()));

        List<User> nonRegisteredUsers = members.stream()
                .filter(member -> registeredUsers.stream()
                        .noneMatch(user -> user.getEmail().equals(member.getEmail())))
                .map(member -> modelMapper.map(member, User.class))
                .collect(Collectors.toList());

        try {
        // Save non-registered users
        if (!nonRegisteredUsers.isEmpty()) {
            userRepo.saveAll(nonRegisteredUsers);
        }
        // Save the group
        Group group = this.modelMapper.map(groupDTO, Group.class);
        group.setActive(true);
        group.setCreatedBy(createdUser);
        Group createdGroup = this.groupRepo.save(group);

        // Save group users
        List<GroupUsers> groupUsersList = members.stream()
                .map(member -> {
                    User groupMember = this.userRepo.findByEmail(member.getEmail()).get();
                    GroupUsers groupUsers = new GroupUsers();
                    groupUsers.setGroup(createdGroup);
                    groupUsers.setUser(groupMember);
                    groupUsers.setCreatedBy(createdUser);
                    groupUsers.setActive(true);
                    return groupUsers;
                })
                .collect(Collectors.toList());
        groupUsersRepo.saveAll(groupUsersList);
        return createdGroup;
        }
        catch (Exception e) {
            // If by any chance the group is not created then we have to delete all the non registered users for this group
            userRepo.deleteAll(nonRegisteredUsers);
            throw new APIException("Unable to create the group with this information");
        }
    }
}


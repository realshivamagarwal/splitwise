package com.app.services;

import com.app.entites.Group;
import com.app.entites.User;
import com.app.payloads.*;

import java.util.List;
import java.util.Map;

public interface GroupService {

    Group addGroup(AddGroupRequestDTO groupDTO, Long userId);

    GroupAmountDTO groupAmountForUser(Long userId, Long groupId);

    Group addMember(AddMemberRequestDTO request, Long addedByUserId);

    boolean leaveGroup(Long selfUserId, Long groupId);

    boolean removeMember(Long selfUserId, Long userId, Long groupId);

    boolean deleteGroup(Long selfUserId, Long groupId);

    void groupSettleUpWithTransactions(Long userId, Long groupId);
}

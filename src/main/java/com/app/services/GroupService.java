package com.app.services;

import com.app.entites.Group;
import com.app.entites.User;
import com.app.payloads.AddGroupRequestDTO;

public interface GroupService {

    Group addGroup(AddGroupRequestDTO groupDTO, User user);

}

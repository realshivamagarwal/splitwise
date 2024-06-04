package com.app.services;


import com.app.entites.Group;
import com.app.entites.User;

public interface ActivityService {

    void saveActivityForGroup(Group group, User createdBy);

}

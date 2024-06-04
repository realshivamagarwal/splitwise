package com.app.services;

import com.app.entites.Activity;
import com.app.entites.Group;
import com.app.entites.User;
import com.app.enums.ActivityType;
import com.app.repositories.ActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityServiceImpl implements ActivityService{

    @Autowired
    ActivityRepo activityRepo;

    @Override
    public void saveActivityForGroup(Group group, User createdBy) {
        Activity activity =  new Activity();
        activity.setActivity_id(group.getId());
        activity.setType(ActivityType.GROUP);
        activity.setDescription(getDesciptionMessageForGroup(group));
        activity.setUser(createdBy);
        this.activityRepo.save(activity);
    }
    public String getDesciptionMessageForGroup(Group group){
        String response ="";
        String groupName = group.getName();
        response += "You Created this ${groupName}";
        return response;
    }


}

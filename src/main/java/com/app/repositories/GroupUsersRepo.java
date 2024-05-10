package com.app.repositories;

import com.app.entites.Group;
import com.app.entites.GroupUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUsersRepo extends JpaRepository<GroupUsers, Long> {
}

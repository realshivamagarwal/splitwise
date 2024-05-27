package com.app.repositories;

import com.app.entites.Expense;
import com.app.entites.Group;
import com.app.entites.GroupUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupUsersRepo extends JpaRepository<GroupUsers, Long> {

    @Query("Select gu from GroupUsers gu JOIN FETCH gu.user")
    List<GroupUsers> findAllByGroupId(Long groupId);


//    @Query("Select gu from GroupUsers gu where gu.group.id=?1 AND gu.user.id=?2 AND AND gu.isActive = true")
    Optional<GroupUsers> findAllByGroupIdAndUserId(Long groupId, Long userId);

}

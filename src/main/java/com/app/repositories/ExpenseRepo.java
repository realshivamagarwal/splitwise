package com.app.repositories;

import com.app.entites.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    Optional<Expense> findByGroupIdAndId(@Param("groupId") Long groupId, @Param("expenseId") Long expenseId);

    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.isActive = true")
    List<Expense> findAllByGroupId(@Param("groupId") Long groupId);

}

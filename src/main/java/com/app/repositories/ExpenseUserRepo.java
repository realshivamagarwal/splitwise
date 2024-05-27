package com.app.repositories;

import com.app.entites.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseUserRepo extends JpaRepository<ExpenseUser, Long> {
    List<ExpenseUser> findByExpenseIdAndUserId(@Param("expenseId") Long expenseId, @Param("userId") Long userId);

    List<ExpenseUser> findByExpenseId(Long id);
}


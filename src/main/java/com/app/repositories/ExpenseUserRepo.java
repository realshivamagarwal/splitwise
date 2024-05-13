package com.app.repositories;

import com.app.entites.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseUserRepo extends JpaRepository<ExpenseUser, Long> {
}

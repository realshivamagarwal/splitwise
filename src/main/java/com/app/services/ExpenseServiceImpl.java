package com.app.services;
import com.app.entites.*;
import com.app.enums.ExpenseType;
import com.app.enums.ExpenseUserType;
import com.app.exception.APIException;
import com.app.payloads.AddExpenseRequestDTO;
import com.app.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    GroupRepo groupRepo;
    @Autowired
    ExpenseRepo expenseRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ExpenseUserRepo expenseUserRepo;

    @Override
    public Expense addExpense(AddExpenseRequestDTO expenseDTO, String createdUserEmail, Long groupId) {

        Optional<User> createdByUser = userRepo.findByEmail(createdUserEmail);
        if(createdByUser.isEmpty())
            throw new APIException("User not found" + createdUserEmail);

        Expense expense = this.modelMapper.map(expenseDTO, Expense.class);
        expense.setAddedBy(createdByUser.get());

        if(groupId!=null) {
            Optional<Group> group = groupRepo.findById(groupId);
            if (group.isEmpty())
                throw new APIException("Group not found" + groupId);
            else {
                expense.setGroup(group.get());
            }
        }

        expense.setType(ExpenseType.ACTUAL_EXPENSE);
        expense.setLastUpdatedBy(createdByUser.get());

        Expense savedExpnese = null;
        try {
            savedExpnese = expenseRepo.save(expense);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
        Map<Long, Long> amountPaidBy = expenseDTO.getAmountPaidBy();
        Map<Long, Long> amountOwedBy = expenseDTO.getAmountOwedBy();

        List<ExpenseUser> expenseUsers = new ArrayList<>();

        for(Map.Entry<Long,Long> entrySet : amountPaidBy.entrySet()){
            Long userId = entrySet.getKey();
            Optional<User> userPaidBy = userRepo.findById(userId);
            if(userPaidBy.isEmpty())
                throw new APIException("User not found with this id" + userPaidBy);
            Long amount = entrySet.getValue();
            ExpenseUser expenseUser = new ExpenseUser();

            expenseUser.setExpense(savedExpnese);
            expenseUser.setAmount(amount);
            expenseUser.setUser(userPaidBy.get());
            expenseUser.setExpenseUserType(ExpenseUserType.PAID_BY);

            expenseUsers.add(expenseUser);
        }

        for(Map.Entry<Long,Long> entrySet : amountOwedBy.entrySet()){
            Long userId = entrySet.getKey();
            Optional<User> userPaidBy = userRepo.findById(userId);
            if(userPaidBy.isEmpty())
                throw new APIException("User not found with this id" + userPaidBy);
            Long amount = entrySet.getValue();
            ExpenseUser expenseUser = new ExpenseUser();

            expenseUser.setExpense(savedExpnese);
            expenseUser.setAmount(amount);
            expenseUser.setUser(userPaidBy.get());
            expenseUser.setExpenseUserType(ExpenseUserType.OWED_BY);
            expenseUsers.add(expenseUser);
        }
        //saved expense user
        this.expenseUserRepo.saveAll(expenseUsers);
        return savedExpnese;
    }
}

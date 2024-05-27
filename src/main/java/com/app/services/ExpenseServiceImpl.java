package com.app.services;
import com.app.entites.*;
import com.app.enums.ExpenseType;
import com.app.enums.ExpenseUserType;
import com.app.exception.APIException;
import com.app.payloads.AddExpenseRequestDTO;
import com.app.payloads.AddTransactionRequestDTO;
import com.app.payloads.ExpenseAmountDTO;
import com.app.payloads.Transaction;
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

    @Autowired
    GroupUsersRepo groupUsersRepo;


    public Expense addExpense(AddExpenseRequestDTO expenseDTO, Long groupId, Long userId) {
        // Find the user who created the expense
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));

        // Create an Expense object from DTO and set the creator
        Expense expense = modelMapper.map(expenseDTO, Expense.class);
        expense.setAddedBy(user);

        // Set the group if groupId is provided
        if(groupId != null) {
            Group group = groupRepo.findById(groupId)
                    .orElseThrow(() -> new APIException("Group not found: " + groupId));
            expense.setGroup(group);
        }

        // Set default values
        expense.setType(ExpenseType.ACTUAL_EXPENSE);
        expense.setLastUpdatedBy(user);
        expense.setActive(true);

        // Save the expense
        Expense savedExpense = expenseRepo.save(expense);

        try {
            // Process amount paid by users
            saveExpenseUsers(savedExpense, expenseDTO.getAmountPaidBy(), ExpenseUserType.PAID_BY);

            // Process amount owed by users
            saveExpenseUsers(savedExpense, expenseDTO.getAmountOwedBy(), ExpenseUserType.OWED_BY);
        }
        catch (Exception e){
            this.expenseRepo.delete(savedExpense);
        }
        return savedExpense;
    }

    private void saveExpenseUsers(Expense expense, Map<Long, Long> usersMap, ExpenseUserType userType) {
        Long totalExpenseSplit=0L;
        List<ExpenseUser> expenseUsers = new ArrayList<>();
        for(Map.Entry<Long, Long> entry : usersMap.entrySet()){
            Long userId = entry.getKey();
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new APIException("User not found with this id: " + userId));
            totalExpenseSplit+=entry.getValue();
            ExpenseUser expenseUser = new ExpenseUser();
            expenseUser.setExpense(expense);
            expenseUser.setAmount(entry.getValue());
            expenseUser.setUser(user);
            expenseUser.setExpenseUserType(userType);

            expenseUsers.add(expenseUser);
        }
        if(totalExpenseSplit.equals(expense.getTotalAmount()))
            expenseUserRepo.saveAll(expenseUsers);
        else{
            throw new APIException("Split expense is not accurate");
        }
    }


    public ExpenseAmountDTO expenseSettleUpForUser(Long groupId, Long expenseId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group not found"));

        List<GroupUsers> groupUsers = groupUsersRepo.findAllByGroupId(groupId);

        if (groupUsers.stream().noneMatch(gu -> gu.getUser().getId().equals(user.getId()))) {
            throw new APIException("User not part of this group");
        }

        Expense expense = expenseRepo.findByGroupIdAndId(groupId, expenseId)
                .orElseThrow(() -> new APIException("Expense is not for this Group"));

        if(expense.isActive()) {
            List<ExpenseUser> expenseUsers = expenseUserRepo.findByExpenseIdAndUserId(expenseId, userId);
            Long totalAmount = expenseUsers.stream()
                    .mapToLong(expenseUser -> {
                        if (expenseUser.getExpenseUserType().equals(ExpenseUserType.PAID_BY))
                            return expenseUser.getAmount();
                        else
                            return -expenseUser.getAmount();
                    })
                    .sum();
            ExpenseAmountDTO expenseAmountDTO = new ExpenseAmountDTO();
            expenseAmountDTO.setExpenseId(expenseId);
            expenseAmountDTO.setTotalExpenseAmount(totalAmount);
            return expenseAmountDTO;
        }
        else {
            throw new APIException("this expense is not active in this grioup");
        }
    }

    @Override
    public Expense addTransaction(AddTransactionRequestDTO transaction, Long groupId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group not found"));

        // Create an Expense object from DTO
        Expense expense = modelMapper.map(transaction, Expense.class);

        expense.setAddedBy(user);
        expense.setGroup(group);
        // Set default values
        expense.setType(ExpenseType.TRANSACTION);
        expense.setLastUpdatedBy(user);
        expense.setActive(true);

        // Save the expense
        Expense savedExpense = expenseRepo.save(expense);

        try {
            // Process amount paid by users
            List<ExpenseUser> expenseUsers = new ArrayList<>();

            User amountPaidBy = userRepo.findById(transaction.getAmountPaidBy())
                        .orElseThrow(() -> new APIException("User not found with this id: " + userId));
            User amountOwedBy = userRepo.findById(transaction.getAmountOwedBy())
                    .orElseThrow(() -> new APIException("User not found with this id: " + userId));

            ExpenseUser expenseUserPaidBy = new ExpenseUser();

            expenseUserPaidBy.setExpense(savedExpense);
            expenseUserPaidBy.setAmount(transaction.getTotalAmount());
            expenseUserPaidBy.setUser(amountPaidBy);
            expenseUserPaidBy.setExpenseUserType(ExpenseUserType.PAID_BY);
            expenseUsers.add(expenseUserPaidBy);

            ExpenseUser expenseUserOwedBy = new ExpenseUser();

            expenseUserOwedBy.setExpense(savedExpense);
            expenseUserOwedBy.setAmount(transaction.getTotalAmount());
            expenseUserOwedBy.setUser(amountOwedBy);
            expenseUserOwedBy.setExpenseUserType(ExpenseUserType.OWED_BY);
            expenseUsers.add(expenseUserOwedBy);
            expenseUserRepo.saveAll(expenseUsers);
        }
        catch (Exception e){
            this.expenseRepo.delete(savedExpense);
        }
        return savedExpense;
    }

    @Override
    public boolean deleteExpense(Long selfUserId, Long groupId, Long expenseId) {

        User deletedBy = userRepo.findById(selfUserId)
                .orElseThrow(() -> new APIException("User not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group not found"));

        List<GroupUsers> groupUsers = groupUsersRepo.findAllByGroupId(groupId);

        if (groupUsers.stream().noneMatch(gu -> gu.getUser().getId().equals(deletedBy.getId()))) {
            throw new APIException("User not part of this group");
        }

        Expense expense = expenseRepo.findByGroupIdAndId(groupId, expenseId)
                .orElseThrow(() -> new APIException("Expense is not for this Group"));

        expense.setActive(false);
        this.expenseRepo.save(expense);
        return false;
    }

}

package com.app.controllers;

import com.app.config.AppConstant;
import com.app.entites.Expense;
import com.app.payloads.*;
import com.app.payloads.ResponseStatus;
import com.app.repositories.UserRepo;
import com.app.services.ExpenseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/expense")
@SecurityRequirement(name = "Splitwise Application")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;
    @Autowired
    UserRepo userRepo;

    @PostMapping("/addExpense/{groupId}")
    public AddExpenseResponseDTO addExpenseForGroup(Principal principal,
                                            @RequestBody AddExpenseRequestDTO expenseDTO,
                                            @PathVariable Long groupId) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        AddExpenseResponseDTO response = new AddExpenseResponseDTO();
        try{
            Expense expense = expenseService.addExpense(expenseDTO,groupId,userId);
            response.setExpenseId(expense.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/addExpenseForFriend")
    public AddExpenseResponseDTO addExpenseForFriend(Principal principal,
                                                    @RequestBody AddExpenseFriendDTO expenseDTO) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        AddExpenseResponseDTO response = new AddExpenseResponseDTO();
        try{
            Expense expense = expenseService.addExpenseForFriend(expenseDTO,userId);
            response.setExpenseId(expense.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @GetMapping("{groupId}/getExpenseAmountForUser/{expenseId}")
    public GetExpenseResponseDTO expenseSettleUp(Principal principal, @PathVariable Long groupId, @PathVariable Long expenseId) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        GetExpenseResponseDTO response = new GetExpenseResponseDTO();
        try {
            ExpenseAmountForUserDTO expenseAmountDTO = expenseService.expenseSettleUpForUser(groupId, expenseId, userId);
            response.setExpenseAmountDTO(expenseAmountDTO);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
        response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/addTransaction/{groupId}")
    public AddExpenseResponseDTO addTransaction(Principal principal,
                                            @RequestBody AddTransactionRequestDTO transaction,
                                            @PathVariable Long groupId) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        AddExpenseResponseDTO response = new AddExpenseResponseDTO();
        try{
            Expense expense = expenseService.addTransaction(transaction,groupId,userId);
            response.setExpenseId(expense.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @PostMapping("/deleteExpense/{groupId}/{expenseId}")
    public APIResponse deleteExpense(Principal principal, @PathVariable Long groupId, @PathVariable Long expenseId){
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        APIResponse response = new APIResponse();
        try{
            if (expenseService.deleteExpense(userId,groupId,expenseId)) {
                response.setStatus(ResponseStatus.SUCCESS);
                response.setMessage("Successfully removed the member from the group");
            } else {
                response.setMessage("member is already removed from the group");
                response.setStatus(ResponseStatus.FAILURE);
            }
        } catch (Exception e) {
            response.setMessage("Failure in removing the group");
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @GetMapping("/getAllExpensesForGroup")
    public GetAllExpenseDTO getAllExpense(Principal principal,
                                          @RequestParam Long groupId,
                                          @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                          @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
                                          @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_EXPENSES_BY, required = false) String sortBy,
                                          @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder) {
        Long userId  = userRepo.findByEmail(principal.getName()).get().getId();
        GetAllExpenseDTO response = new GetAllExpenseDTO();
        try{
            GroupResponse groupResponse = this.expenseService.getAllExpensesForGroup(groupId, userId, pageNumber, pageSize, sortBy, sortOrder);
            response.setGroup(groupResponse);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}

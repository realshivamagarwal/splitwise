package com.app.services;

import com.app.entites.*;
import com.app.exception.APIException;
import com.app.payloads.*;
import com.app.repositories.*;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepo userRepo;
    @Autowired
    GroupRepo groupRepo;
    @Autowired
    GroupUsersRepo groupUsersRepo;

    @Autowired
    ExpenseRepo expenseRepo;

    @Autowired
    ExpenseService expenseService;
    
    @Autowired
    ExpenseUserRepo expenseUserRepo;

    @Override
    public Group addGroup(AddGroupRequestDTO groupDTO, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));

        List<MemberDTO> members = groupDTO.getMembers();

        //Adding created user of this group as an default member of this group
        members.add(this.modelMapper.map(user,MemberDTO.class));

        // Saving non-register user from the list of members of the group
        // To-Do: sending register or invitaion link to the non-register user

        List<User> registeredUsers = userRepo.findAllByEmailIn(
                members.stream().map(MemberDTO::getEmail).collect(Collectors.toList()));

        List<User> nonRegisteredUsers = members.stream()
                .filter(member -> registeredUsers.stream()
                        .noneMatch(user1 -> user1.getEmail().equals(member.getEmail())))
                .map(member -> modelMapper.map(member, User.class))
                .collect(Collectors.toList());

        try {
        // Save non-registered users
        if (!nonRegisteredUsers.isEmpty()) {
            userRepo.saveAll(nonRegisteredUsers);
        }
        // Save the group
        Group group = this.modelMapper.map(groupDTO, Group.class);
        group.setActive(true);
        group.setCreatedBy(user);
        Group createdGroup = this.groupRepo.save(group);

        // Save group users
        List<GroupUsers> groupUsersList = members.stream()
                .map(member -> {
                    User groupMember = this.userRepo.findByEmail(member.getEmail()).get();
                    GroupUsers groupUsers = new GroupUsers();
                    groupUsers.setGroup(createdGroup);
                    groupUsers.setUser(groupMember);
                    groupUsers.setAddedBy(user);
                    groupUsers.setActive(true);
                    return groupUsers;
                })
                .collect(Collectors.toList());
        groupUsersRepo.saveAll(groupUsersList);
        return createdGroup;
        }
        catch (Exception e) {
            // If by any chance the group is not created then we have to delete all the non registered users for this group
            userRepo.deleteAll(nonRegisteredUsers);
            throw new APIException("Unable to create the group with this information");
        }
    }

    @Override
    public GroupAmountDTO groupAmountForUser(Long userId, Long groupId) {
        // Find the user who are getting group amount
        User selfUser = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));

        // Find the group for which the total amount is calculated
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group not found"));

        List<GroupUsers> groupUsers = groupUsersRepo.findAllByGroupId(groupId);

        if (groupUsers.stream().noneMatch(gu -> gu.getUser().getId().equals(selfUser.getId())))
            throw new APIException("User not part of this group");

        List<Expense> expenses = expenseRepo.findAllByGroupId(groupId);

        Map<Long, Long> userAmount = new HashMap<>();

        for(Expense expense : expenses) {

            List<ExpenseUser> expenseUsers = this.expenseUserRepo.findByExpenseId(expense.getId());

            List<User> users = expenseUsers.stream()
                    .map(ExpenseUser::getUser).distinct()
                    .collect(Collectors.toList());

            users.stream().forEach(user -> {
                ExpenseAmountDTO expenseAmountDTO = expenseService.expenseSettleUpForUser(groupId, expense.getId(),user.getId());
                Long expenseAmount = expenseAmountDTO.getTotalExpenseAmount();
                if(userAmount.containsKey(user.getId()))
                   userAmount.put(user.getId(), userAmount.get(user.getId()) + expenseAmount);
                else
                   userAmount.put(user.getId(), expenseAmount);
            });
        }

        // PriorityQueue implementing Min heap based on values of Pair<K, V>
        PriorityQueue<Pair<Long, Long>>  minHeap =
                new PriorityQueue<>((a, b) -> Math.toIntExact(a.getValue() - b.getValue()));

        // PriorityQueue implementing Max heap based on values of Pair<K, V>
        PriorityQueue<Pair<Long, Long> > maxHeap =
                new PriorityQueue<>((a, b) -> Math.toIntExact(b.getValue() - a.getValue()));

        for(Map.Entry<Long, Long> entry :  userAmount.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
            if(entry.getValue() > 0){
                maxHeap.add(Pair.of(entry.getKey(), entry.getValue()));
            } else {
                minHeap.add(Pair.of(entry.getKey(), entry.getValue()));
            }
        }

        List<Transaction> transactions = new ArrayList<>();
        while(!maxHeap.isEmpty() && !minHeap.isEmpty()){
            Transaction transaction = new Transaction();

            Pair<Long, Long> amountOwedBy = minHeap.remove();
            Pair<Long, Long> amountPaidBy = maxHeap.remove();

            System.out.print("AmountOwedBy" + amountOwedBy.getKey() + " " + "Amount" + amountOwedBy.getValue());
            System.out.print("AmountPaidBy" + amountPaidBy.getKey() + " " + "Amount" + amountPaidBy.getValue());

            long extraPaid = amountPaidBy.getValue() + amountOwedBy.getValue();

            transaction.setFrom(amountOwedBy.getKey());
            transaction.setTo(amountPaidBy.getKey());

            if(extraPaid>0){
                transaction.setAmount(-amountOwedBy.getValue());
                maxHeap.add(Pair.of(amountPaidBy.getKey(), extraPaid));
            }
            else if(extraPaid<0){
                transaction.setAmount(amountPaidBy.getValue());
                minHeap.add(Pair.of(amountOwedBy.getKey(), extraPaid));
            }
            else {
                transaction.setAmount(-amountOwedBy.getValue());
            }
            System.out.println("FROM" + transaction.getFrom());
            System.out.println("TO" + transaction.getTo());
            System.out.println("AMOUNT" + transaction.getAmount());
            transactions.add(transaction);
        }
        List <Transaction> userTransactions = transactions.stream().filter(transaction -> {
            if(transaction.getTo()==selfUser.getId() || transaction.getFrom()==userId)
                return true;
            else
                return false;
        }).collect(Collectors.toList());

        GroupAmountDTO groupAmountDTO = new GroupAmountDTO();
        groupAmountDTO.setTotalGroupAmount(userAmount.get(userId));
        groupAmountDTO.setGroupId(groupId);
        groupAmountDTO.setTransactions(userTransactions);

        return groupAmountDTO;
    }

    @Override
    public Group addMember(AddMemberRequestDTO request, Long addedByUserId) {
        User addedBy = userRepo.findById(addedByUserId)
                .orElseThrow(() -> new APIException("User which is adding this member is not found"));

        Group group = groupRepo.findById(request.getGroupId())
                .orElseThrow(() -> new APIException("Group is not found"));

        GroupUsers groupUsers = new GroupUsers();

        groupUsers.setGroup(group);
        groupUsers.setAddedBy(addedBy);
        groupUsers.setActive(true);


        Optional<User> member = userRepo.findByEmail(request.getEmail());
        if(member.isEmpty()){
            User user = new User(request.getMobileNumber(), request.getEmail());
            User savedUser = this.userRepo.save(user);
            groupUsers.setUser(savedUser);
        }
        else{
            User savedUser = member.get();
            groupUsers.setUser(savedUser);
        }
        this.groupUsersRepo.save(groupUsers);

        return group;
    }

    @Override
    public boolean leaveGroup(Long selfUserId, Long groupId) {
        User removedBy = userRepo.findById(selfUserId)
                .orElseThrow(() -> new APIException("User which is adding this member is not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group is not found"));

        try {
            Optional<GroupUsers> groupusers = this.groupUsersRepo.findAllByGroupIdAndUserId(groupId, selfUserId);
            if (groupusers.isPresent()) {
                GroupUsers groupUsers = groupusers.get();
                groupUsers.setActive(false);
                groupUsers.setRemovedBy(removedBy);
                groupUsersRepo.save(groupUsers);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeMember(Long selfUserId, Long userId, Long groupId) {
        User removedBy = userRepo.findById(selfUserId)
                .orElseThrow(() -> new APIException("User which is removing this member is not found"));

        User removedTo = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User which need to remove is not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group is not found"));

        try {
            Optional<GroupUsers> groupusers = this.groupUsersRepo.findAllByGroupIdAndUserId(groupId, userId);
            if (groupusers.isPresent()) {
                GroupUsers groupUsers = groupusers.get();
                groupUsers.setActive(false);
                groupUsers.setRemovedBy(removedBy);
                groupUsersRepo.save(groupUsers);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteGroup(Long selfUserId, Long groupId) {
        User deletedBy = userRepo.findById(selfUserId)
                .orElseThrow(() -> new APIException("User which is deleting this group is not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group is not found"));

        try {
            group.setActive(false);
            this.groupRepo.save(group);
            List<GroupUsers> allByGroupId = this.groupUsersRepo.findAllByGroupId(groupId);
            allByGroupId.stream().forEach(groupUsers -> {
                groupUsers.setActive(false);
                groupUsers.setRemovedBy(deletedBy);
            });

            this.groupUsersRepo.saveAll(allByGroupId);
            List<Expense> expensedOfGroup = this.expenseRepo.findAllByGroupId(groupId);
            expensedOfGroup.stream().forEach(expense -> {
                this.expenseService.deleteExpense(deletedBy.getId(),groupId,expense.getId());
            });
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void groupSettleUpWithTransactions(Long userId, Long groupId) {
        User settledFor = userRepo.findById(userId)
                .orElseThrow(() -> new APIException("User which is settle up this group is not found"));

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new APIException("Group is not found"));

        GroupAmountDTO groupAmountDTO = groupAmountForUser(userId, groupId);

        groupAmountDTO.getTransactions().stream().map(transaction -> {
            Long fromUser = transaction.getFrom();
            Long ToUser = transaction.getTo();
            Long amount = transaction.getAmount();
            AddTransactionRequestDTO transactionDetails = new AddTransactionRequestDTO();
            transactionDetails.setDescription(fromUser + "Paid" + ToUser);
            transactionDetails.setAmountPaidBy(fromUser);
            transactionDetails.setAmountOwedBy(ToUser);
            transactionDetails.setTotalAmount(amount);
            Expense expense = this.expenseService.addTransaction(transactionDetails, groupId, userId);
            return expense;
        }).collect(Collectors.toList());

    }

}


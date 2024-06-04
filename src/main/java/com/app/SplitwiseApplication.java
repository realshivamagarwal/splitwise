package com.app;

import com.app.entites.Group;
import com.app.entites.Role;
import com.app.enums.GroupPart;
import com.app.repositories.GroupRepo;
import com.app.repositories.RoleRepo;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@SecurityScheme(name = "Splitwise Application", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SplitwiseApplication implements CommandLineRunner {
	@Autowired
	RoleRepo roleRepo;

	@Autowired
	GroupRepo groupRepo;
	public static void main(String[] args) {
		SpringApplication.run(SplitwiseApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			Role adminRole = new Role();
			adminRole.setRoleId(101L);
			adminRole.setRoleName("ADMIN");

			Role userRole = new Role();
			userRole.setRoleId(102L);
			userRole.setRoleName("USER");

			List<Role> roles = List.of(adminRole, userRole);

			List<Role> savedRoles = roleRepo.saveAll(roles);

			Group defaultFriendGroup = new Group();

			defaultFriendGroup.setName("Default Expense Group");
			defaultFriendGroup.setActive(true);
			defaultFriendGroup.setPart(GroupPart.FRIEND);
			defaultFriendGroup.setId(201L);

			Group save = this.groupRepo.save(defaultFriendGroup);

			savedRoles.forEach(System.out::println);
			System.out.println(save.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	}


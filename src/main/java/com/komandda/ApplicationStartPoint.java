/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.komandda;

import com.komandda.entity.Permission;
import com.komandda.entity.User;
import com.komandda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Collections;

@SpringBootApplication
public class ApplicationStartPoint {

	@Autowired
	private UserService userService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStartPoint.class, args);
	}

	@PostConstruct
	void init() {
		if(userService.findAll().isEmpty()) {
			User admin = generateDefaultUser();
			userService.save(admin);
		}
	}

	private User generateDefaultUser() {
		User admin = new User();
		admin.setId("1");
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setEmail("no email");
		admin.setColor("#E67E22");
		admin.setAccountNonExpired(true);
		admin.setAccountNonLocked(true);
		admin.setCredentialsNonExpired(true);
		admin.setEnabled(true);
		admin.setAuthorities(Collections.singletonList(new Permission("2", "user_edit")));
		return admin;
	}

}

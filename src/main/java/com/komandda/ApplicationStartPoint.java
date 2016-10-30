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
import com.komandda.repository.EquipmentRepository;
import com.komandda.repository.LocationRepository;
import com.komandda.repository.UserRepository;
import com.komandda.service.EquipmentService;
import com.komandda.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class ApplicationStartPoint {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;

	@Autowired
	private LocationRepository locationRepository;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStartPoint.class, args);
	}

	@PostConstruct
	void init() {
        generateDefaultUserIfAnyAbsent();
        setDeletedToFalse();
		setNameIfAbsent();
	}

	private void setNameIfAbsent() {
		userRepository.findAll().stream()
				.filter(user -> StringUtils.isEmpty(user.getName()))
				.map(user -> {
					user.setName(user.getUsername());
					return user;
				})
				.forEach(user -> {
					userRepository.save(user);
				});
	}

	private void setDeletedToFalse(){
		userRepository.findAll().stream()
				.filter(user -> !user.isDeleted())
				.forEach(user -> {
					userRepository.save(user);
				});
		equipmentRepository.findAll().stream()
				.filter(equipment -> !equipment.isDeleted())
				.forEach(equipment -> {
					equipmentRepository.save(equipment);
				});
		locationRepository.findAll().stream()
				.filter(location -> !location.isDeleted())
				.forEach(location -> {
					locationRepository.save(location);
				});
	}

	private void generateDefaultUserIfAnyAbsent() {
		if(userRepository.findAll().isEmpty()) {
			User admin = generateDefaultUser();
			userRepository.save(admin);
		}
	}

	private User generateDefaultUser() {
		User admin = new User();
		admin.setId("1");
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setEmail("");
		admin.setColor("#E67E22");
		admin.setAccountNonExpired(true);
		admin.setAccountNonLocked(true);
		admin.setCredentialsNonExpired(true);
		admin.setEnabled(true);
		admin.setAuthorities(Arrays.asList(
				new Permission("1", "event_edit"),
				new Permission("3", "location_edit"),
				new Permission("4", "equipment_edit"),
				new Permission("2", "user_edit")));
		return admin;
	}

}

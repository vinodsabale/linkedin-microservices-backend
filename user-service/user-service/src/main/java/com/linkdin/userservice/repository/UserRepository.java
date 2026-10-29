package com.linkdin.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkdin.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	public List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrHeadlineContainingIgnoreCaseOrSkillContainingIgnoreCase(
			String firstName, String lastName, String headline, String skill);
}

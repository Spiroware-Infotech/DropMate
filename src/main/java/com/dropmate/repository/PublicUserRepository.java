package com.dropmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dropmate.entity.PublicUser;

public interface PublicUserRepository  extends JpaRepository<PublicUser, Long> {
	
	public PublicUser findByEmail(String email);
}

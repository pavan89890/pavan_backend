package com.pavan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavan.modal.User;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {
	public User findByMobile(String mobile);
}

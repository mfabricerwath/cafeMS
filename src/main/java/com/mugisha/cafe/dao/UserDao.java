package com.mugisha.cafe.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mugisha.cafe.pojo.User;

public interface UserDao extends JpaRepository<User, Integer> {
	
	User findUserByEmail(@Param("email")String email);

}

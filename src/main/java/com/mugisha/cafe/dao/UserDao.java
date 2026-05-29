package com.mugisha.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mugisha.cafe.pojo.User;
import com.mugisha.cafe.wrapper.UserWrapper;

import jakarta.transaction.Transactional;

public interface UserDao extends JpaRepository<User, Integer> {
    
    User findUserByEmail(@Param("email") String email);
    
    @Query(name = "User.getAllUser")
    List<UserWrapper> getAllUser(@Param("user") String user);
    
    @Query(name = "User.getAllAdmin")
    List<String> getAllAdmin(@Param("admin") String role); // ← corrigé
    
    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

	User findByEmail(String string);
}
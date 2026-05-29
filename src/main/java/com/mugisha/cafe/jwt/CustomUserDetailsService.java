package com.mugisha.cafe.jwt;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mugisha.cafe.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserDao userdao;
	
	private com.mugisha.cafe.pojo.User userDetails;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		log.info("Inside loadUserByUserName{}",username);
		
		userDetails=userdao.findUserByEmail(username);
		
		if(!Objects.isNull(userDetails)) {
			return new User(
		            userDetails.getEmail(),
		            userDetails.getPassword(),
		            Arrays.asList(
		                new SimpleGrantedAuthority(userDetails.getRole()) // ← "admin" ou "user"
		            )
		        );

		}else
			throw new UsernameNotFoundException("User not found.");
	}
	
	public com.mugisha.cafe.pojo.User getUserDetail(){
		return userDetails;
	}

}

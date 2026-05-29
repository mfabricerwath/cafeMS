package com.mugisha.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.mugisha.cafe.wrapper.UserWrapper;

public interface UserService {

	ResponseEntity<String>signUp(Map<String,String>mapRequest);
	ResponseEntity<String>login(Map<String,String>mapRequest);
	ResponseEntity<List<UserWrapper>>getAllUser();
	ResponseEntity<String>update(Map<String,String>mapRequest);
	ResponseEntity<String>checkToken();
	ResponseEntity<String>changePassword(Map<String,String>mapRequest);
	
}

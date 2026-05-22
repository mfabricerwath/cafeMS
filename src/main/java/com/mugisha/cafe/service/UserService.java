package com.mugisha.cafe.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UserService {

	ResponseEntity<String>signUp(Map<String,String>mapRequest);
	
}

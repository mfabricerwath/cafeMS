package com.mugisha.cafe.serviceImp;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mugisha.cafe.constents.cafeConstents;
import com.mugisha.cafe.dao.UserDao;
import com.mugisha.cafe.pojo.User;
import com.mugisha.cafe.service.UserService;
import com.mugisha.cafe.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserDao userDao;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		
		log.info("Inside signUp {}",requestMap);
		
		try {
		if(validateSignUp(requestMap)) {
			
			User user=userDao.findUserByEmail(requestMap.get("email"));
			if(Objects.isNull(user)) {
				userDao.save(getUserFromMap(requestMap));
				return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
			}else {
				return CafeUtils.getResponseEntity("Email already Exists", HttpStatus.BAD_REQUEST);
			}
			
		}else {
			return CafeUtils.getResponseEntity(cafeConstents.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private boolean validateSignUp(Map<String,String> requestMap) {
		
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
		 && requestMap.containsKey("password")) {
			return true;
		}else return false;
	}
	
	private User getUserFromMap(Map<String,String>RequestMap) {
		
		User user=new User();
		
		user.setName(RequestMap.get("name"));
		user.setEmail(RequestMap.get("email"));
		user.setContactNumber(RequestMap.get("contactNumber"));
		user.setPassword(RequestMap.get("password"));
		user.setRole("user");
		user.setStatus("false");
		
		return user;
	}

}

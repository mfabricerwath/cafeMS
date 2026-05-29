package com.mugisha.cafe.restImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.mugisha.cafe.constents.cafeConstents;
import com.mugisha.cafe.rest.UserRest;
import com.mugisha.cafe.service.UserService;
import com.mugisha.cafe.utils.CafeUtils;
import com.mugisha.cafe.wrapper.UserWrapper;

@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;

	@Override
	public ResponseEntity<String> SignUp(Map<String, String> RequestMap) {
		 try {
	            return userService.signUp(RequestMap);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> RequestMap) {
		try {
			return userService.login(RequestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		 return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			return userService.getAllUser();
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> RequestMap) {
		try {
			return userService.update(RequestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<String>(cafeConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> checkToken() {
		try {
			return userService.checkToken();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return new ResponseEntity<String>(cafeConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> mapRequest) {
		try {
			return userService.changePassword(mapRequest);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<String>(cafeConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

 



}
package com.mugisha.cafe.restImp;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.mugisha.cafe.constents.cafeConstents;
import com.mugisha.cafe.rest.UserRest;
import com.mugisha.cafe.service.UserService;
import com.mugisha.cafe.utils.CafeUtils;

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

 



}
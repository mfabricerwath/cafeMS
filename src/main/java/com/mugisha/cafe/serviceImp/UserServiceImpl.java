package com.mugisha.cafe.serviceImp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mugisha.cafe.constents.cafeConstents;
import com.mugisha.cafe.dao.UserDao;
import com.mugisha.cafe.jwt.CustomUserDetailsService;
import com.mugisha.cafe.jwt.JwtFilter;
import com.mugisha.cafe.jwt.JwtUtil;
import com.mugisha.cafe.pojo.User;
import com.mugisha.cafe.service.UserService;
import com.mugisha.cafe.utils.CafeUtils;
import com.mugisha.cafe.utils.EmailUtils;
import com.mugisha.cafe.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	EmailUtils emailUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Inside signUp {}", requestMap);
		try {
			if (validateSignUp(requestMap)) {
				User user = userDao.findUserByEmail(requestMap.get("email"));
				if (Objects.isNull(user)) {
					userDao.save(getUserFromMap(requestMap));
					return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("Email already Exists", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(cafeConstents.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateSignUp(Map<String, String> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("password");
	}

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setEmail(requestMap.get("email"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setPassword(passwordEncoder.encode(requestMap.get("password")));
		user.setRole("user");
		user.setStatus("false");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> mapRequest) {
		log.info("Inside login");
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(mapRequest.get("email"), mapRequest.get("password")));

			if (auth.isAuthenticated()) {
				if (customUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					Map<String, Object> claims = new HashMap<>();
					claims.put("email", mapRequest.get("email"));
					claims.put("role", customUserDetailsService.getUserDetail().getRole());
					String token = jwtUtil.generateToken(claims, mapRequest.get("email"));
					return ResponseEntity.ok(token);
				} else {
					return new ResponseEntity<String>("{\"message\":\"" + " Wait for admin approval." + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
			}
		} catch (BadCredentialsException ex) {
			log.error("Bad credentials for user {}: {}", mapRequest.get("email"), ex.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password here ");
		} catch (DisabledException ex) {
			log.error("Account disabled for user {}: {}", mapRequest.get("email"), ex.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled");
		} catch (LockedException ex) {
			log.error("Account locked for user {}: {}", mapRequest.get("email"), ex.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked");
		} catch (Exception ex) {
			log.error("Unexpected error during login: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong. Please try again later");
		}
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if (jwtFilter.isAdmin()) {
				List<UserWrapper> list = userDao.getAllUser("user");
				return new ResponseEntity<>(list, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> mapRequest) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(mapRequest.get("id")));
				if (!optional.isEmpty()) {
					userDao.updateStatus(mapRequest.get("status"), Integer.parseInt(mapRequest.get("id")));
					sendMailToAllAdmin(mapRequest.get("status"), optional.get().getEmail(),
							userDao.getAllAdmin("admin"));
					return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("User id does not exist", HttpStatus.OK);
				}
			} else {
				return CafeUtils.getResponseEntity(cafeConstents.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account approved",
					"User :-" + user + "\n is approved by \n ADMIN: -" + jwtFilter.getCurrentUser(), allAdmin);
		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled",
					"User :-" + user + "\n is disabled by \n ADMIN: -" + jwtFilter.getCurrentUser(), allAdmin);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {	
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> mapRequest) {
	    try {
	        // Add validation here
	        if (mapRequest == null || !mapRequest.containsKey("oldPassword") || !mapRequest.containsKey("newPassword")) {
	            System.out.println("Missing required fields. Map contents: " + mapRequest);
	            return CafeUtils.getResponseEntity("oldPassword and newPassword are required", HttpStatus.BAD_REQUEST);
	        }
	        
	        String oldPassword = mapRequest.get("oldPassword");
	        String newPassword = mapRequest.get("newPassword");
	        
	        // Check if passwords are null or empty
	        if (oldPassword == null || oldPassword.trim().isEmpty() || 
	            newPassword == null || newPassword.trim().isEmpty()) {
	            return CafeUtils.getResponseEntity("Passwords cannot be null or empty", HttpStatus.BAD_REQUEST);
	        }
	        
	        User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
	        if (userObj != null) {
	            if (passwordEncoder.matches(oldPassword, userObj.getPassword())) {
	                userObj.setPassword(passwordEncoder.encode(newPassword));
	                userDao.save(userObj);
	                return CafeUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);
	            }
	            return CafeUtils.getResponseEntity("Incorrect old password please try again later", HttpStatus.BAD_REQUEST);
	        }
	        System.out.println("here is where we are!!!01");
	        return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    System.out.println("here is where we are!!!02");
	    return CafeUtils.getResponseEntity(cafeConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
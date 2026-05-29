package com.mugisha.cafe.rest;


import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mugisha.cafe.wrapper.UserWrapper;

@RestController 
@RequestMapping(path="/user")
public interface UserRest {

    @PostMapping(path="/signup")
    public ResponseEntity<String> SignUp(@RequestBody(required=true) Map<String,String> RequestMap);

    @PostMapping(path="/login")
    public ResponseEntity<String> login(@RequestBody(required=true) Map<String,String> RequestMap);

    @GetMapping(path="/get")
    public ResponseEntity<List<UserWrapper>> getAllUser();

    @PostMapping(path="/update")
    public ResponseEntity<String> update(@RequestBody(required=true) Map<String,String> RequestMap);
    
    @GetMapping(path="/checkToken")
    public ResponseEntity<String>checkToken();
    
    @PostMapping(path="/changePassword")
    public ResponseEntity<String>changePassword(@RequestBody Map<String,String>RequestMap);
}
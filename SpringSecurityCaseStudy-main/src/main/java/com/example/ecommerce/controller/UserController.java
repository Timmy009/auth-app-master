package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.*;
import com.example.ecommerce.dto.response.GetUserResponse;
import com.example.ecommerce.exception.UserNotFoundException;
import com.example.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")

public class UserController {
    @Autowired
    UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest registerRequest) throws Exception
    {

        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.OK);


    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {

            return new ResponseEntity<>(userService.login(loginRequest), HttpStatus.OK);


    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<GetUserResponse> findUserByEmail(@RequestParam String email) throws Exception {
        GetUserResponse user = userService.findUserByEmail(email);
        return ResponseEntity.ok().body(user);
    }




}

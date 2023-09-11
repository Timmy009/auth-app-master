package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ConfirmationTokenRequest;
import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.UserRegisterRequest;
import com.example.ecommerce.dto.response.LoginResponse;
import com.example.ecommerce.dto.response.RegistrationResponse;
import com.example.ecommerce.model.User;

import java.util.List;


public interface UserService {
     RegistrationResponse register(UserRegisterRequest registerRequest);
//     LoginResponse login(LoginRequest loginRequest);
     LoginResponse login(LoginRequest loginRequest);
     String confirmToken(ConfirmationTokenRequest confirmationTokenRequest);


     List<User> getAllUser();
     User findUserById(Long userId);
     User findUserByFirstName(String firstName);
     User findUserByEmail(String email);




}

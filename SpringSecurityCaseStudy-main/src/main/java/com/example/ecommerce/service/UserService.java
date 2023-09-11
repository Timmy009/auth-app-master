package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ConfirmationTokenRequest;
import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.UserRegisterRequest;
import com.example.ecommerce.dto.response.GetUserResponse;
import com.example.ecommerce.dto.response.LoginResponse;
import com.example.ecommerce.dto.response.RegistrationResponse;
import com.example.ecommerce.exception.UserNotFoundException;
import com.example.ecommerce.model.User;

import java.util.List;


public interface UserService {
     RegistrationResponse register(UserRegisterRequest registerRequest);
//     LoginResponse login(LoginRequest loginRequest);
     LoginResponse login(LoginRequest loginRequest);

     GetUserResponse getUserById(Long id) throws UserNotFoundException;

     String confirmToken(ConfirmationTokenRequest confirmationTokenRequest);


     User findUserByFirstName(String firstName);
     GetUserResponse findUserByEmail(String email);




}

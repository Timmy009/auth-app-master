package com.example.ecommerce.service;

import com.example.ecommerce.Security.JwtService;
import com.example.ecommerce.dto.request.*;
import com.example.ecommerce.dto.response.GetUserResponse;
import com.example.ecommerce.dto.response.LoginResponse;
import com.example.ecommerce.dto.response.RegistrationResponse;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.ecommerce.exception.ExceptionMessage.USER_NOT_FOUND_EXCEPTION;
import static com.example.ecommerce.utils.EmailUtils.buildEmail;
import static com.example.ecommerce.utils.EmailUtils.generateOTP;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    SecuredUserService securedUserService;
    @Autowired
    JwtService jwtService;

    @Autowired
    ConfirmTokenService confirmTokenService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    ModelMapper mapper = new ModelMapper();

    @Override
    public RegistrationResponse register(UserRegisterRequest registerRequest) {

        boolean foundUser = userRepository.existsByEmail(registerRequest.getEmail());
        if (foundUser){
            throw new UserAlreadyExistException("User Already Exist");
        }
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = mapper.map(registerRequest,User.class);
        user.setPassword(hashedPassword);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(Role.CUSTOMER);
        user.setUserRoles(userRoles);
        userRepository.save(user);

        String token = generateOTP();
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setMessage(buildEmail(registerRequest.getFirstName(),token));
        emailRequest.setReceiver(registerRequest.getEmail());

        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now() ,LocalDateTime.now().plusMinutes(5),user);
        confirmationToken.setConfirmAt(LocalDateTime.now());
        confirmTokenService.saveConfirmationToken(confirmationToken);

        return RegistrationResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .message("token sent to your email")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User foundUser = getUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())){
            System.out.println("error");
            throw new PasswordMismatchException("incorrect password");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails = securedUserService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtService.generateToken(userDetails);
        return new LoginResponse("successful", OK, now(), token);


    }


    @Override
    public GetUserResponse getUserById(Long id) {
        Optional<User> foundUser = userRepository.findUserById(id);
        User user = foundUser.orElseThrow(
                ()->new UserNotFoundException(USER_NOT_FOUND_EXCEPTION.getMessage())
        );

        GetUserResponse getUserResponse = buildUserResponse(user);

        return getUserResponse;
    }

    private static GetUserResponse buildUserResponse(User savedUser) {
        return GetUserResponse.builder()
                .id(savedUser.getId())

                .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .phoneNumber(savedUser.getPhoneNumber())
                .email(savedUser.getEmail())
                .build();
    }
    @Override
    public String confirmToken(ConfirmationTokenRequest confirmationTokenRequest) {
        User foundUser = getUserByEmail(confirmationTokenRequest.getEmail());
        ConfirmationToken confirmationToken = confirmTokenService.getConfirmationToken(confirmationTokenRequest.getToken())
                .orElseThrow(()-> new TokenNotFoundException("token does not exist"));
        if (confirmationToken.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("token is expired");
        }
        if (confirmationToken.getConfirmAt() != null){
            throw new IllegalStateException("token has already been confirmed");
        }

        confirmTokenService.setExpiredAt(confirmationToken.getToken());
        foundUser.setValid(true);
        userRepository.save(foundUser);
        return "token confirmed,you are now verified";
    }




    @Override
    public User findUserByFirstName(String firstName) {
        return userRepository.findUserByFirstName(firstName);
    }

    @Override
    public GetUserResponse findUserByEmail(String email) {
        Optional<User> foundUser = userRepository.findUserByEmail(email);
        User user = foundUser.orElseThrow(
                ()->new UserNotFoundException(USER_NOT_FOUND_EXCEPTION.getMessage())
        );

        GetUserResponse getUserResponse = buildUserResponse(user);

        return getUserResponse;
    }
    public User getUserByEmail(String email) {
        Optional<User> foundUser = userRepository.findUserByEmail(email);
        User user = foundUser.orElseThrow(
                ()->new UserNotFoundException(USER_NOT_FOUND_EXCEPTION.getMessage())
        );

        GetUserResponse getUserResponse = buildUserResponse(user);

        return user;
    }

}




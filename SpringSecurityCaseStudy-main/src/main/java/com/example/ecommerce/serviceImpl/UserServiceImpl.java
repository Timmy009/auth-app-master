package com.example.ecommerce.serviceImpl;

import com.example.ecommerce.config.JwtService;
import com.example.ecommerce.dto.request.*;
import com.example.ecommerce.dto.response.LoginResponse;
import com.example.ecommerce.dto.response.RegistrationResponse;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
        //Optional<User> foundUser = userRepository.findUserByEmail(registerRequest.getEmail());
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
        User foundUser = findUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())){
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
    public String confirmToken(ConfirmationTokenRequest confirmationTokenRequest) {
        User foundUser = findUserByEmail(confirmationTokenRequest.getEmail());
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
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) throw new UserNotFoundException("user not found");
        return userRepository.findById(userId).get();
//        return user.get();
    }

    @Override
    public User findUserByFirstName(String firstName) {
        return userRepository.findUserByFirstName(firstName);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()->new UserNotFoundException("user not found"));
    }

}




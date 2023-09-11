package com.example.ecommerce.serviceImpl;

import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.UserRegisterRequest;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest

@RunWith(MockitoJUnitRunner.class)
class CustomerServiceImplTest {
    @Mock
    UserRepository customerRepository;

    @InjectMocks
    UserServiceImpl userService;


    @Captor
    private  ArgumentCaptor<User> userArgumentCaptor;

    UserRegisterRequest customerRequest;
    LoginRequest loginRequest;

    @BeforeEach

    void setUp() {
        MockitoAnnotations.initMocks(this);
//        customerService = new CustomerServiceImpl();
        customerRequest = new UserRegisterRequest();
        customerRequest.setFirstName("Neche");
        customerRequest.setLastName("Okolo");
        customerRequest.setEmail("dorisebele47@gmail.com");
        customerRequest.setPassword("eb0ko76");

    }


    @Test
    void testThatAUserCanLogin(){
        loginRequest = new LoginRequest();
        loginRequest.setEmail("dorisebele47@gmail.com");
        loginRequest.setPassword("567890");
    }

    @Test
    void testLogin(){
        User user = new User();
        String email = "ebele1@gmail.com";
        String password = "Rty2345";
        user.setEmail(email);
        user.setPassword(password);
        when(customerRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        String message = String.valueOf(userService.login(loginRequest));
        assertEquals(message," Bearer " + message );

    }

    @Test
    void test_that_i_can_get_all_users(){
        BDDMockito.when(customerRepository.save(userArgumentCaptor.capture())).thenReturn(new User());
        BDDMockito.when(customerRepository.findAll()).thenReturn(List.of(new User()));

        userService.register(customerRequest);
        BDDMockito.then(customerRepository).should().save(any(User.class));

    }
    @Test
    void test_that_i_can_get_user_by_firstname(){
        User foundUser = new User();
        foundUser.setFirstName("Doris");
        BDDMockito.when(customerRepository.findUserByFirstName(anyString())).thenReturn(foundUser);
        String firstName = "Doris";
        var user = userService.findUserByFirstName(firstName);
        assertEquals("Doris",user.getFirstName());
    }





    }



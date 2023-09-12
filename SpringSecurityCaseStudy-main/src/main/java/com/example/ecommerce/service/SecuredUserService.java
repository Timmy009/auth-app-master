package com.example.ecommerce.service;

import com.example.ecommerce.model.SecuredUser;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecuredUserService implements UserDetailsService {
    private final UserRepository userRepository;


    //TODO:3
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User foundUser =userRepository.findUserByEmail(email).orElseThrow(RuntimeException::new);
        return new SecuredUser(foundUser);
    }
}

package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication

public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }


//    @Bean TODO:2
//    public ModelMapper mapper(){
//        return new ModelMapper();
//    }
//

    //TODO:4
   @Bean
    public JavaMailSender javaMailSender(){
        return new JavaMailSenderImpl();
    }


}

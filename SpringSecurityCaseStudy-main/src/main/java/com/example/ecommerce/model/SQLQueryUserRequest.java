package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SQLQueryUserRequest {

    public long id;
    public String firstName;
    public String lastName;
    public String username;
    public String password;
}

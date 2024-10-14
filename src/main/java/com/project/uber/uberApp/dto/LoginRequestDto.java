package com.project.uber.uberApp.dto;


import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;  // hashed password for security reasons.
}

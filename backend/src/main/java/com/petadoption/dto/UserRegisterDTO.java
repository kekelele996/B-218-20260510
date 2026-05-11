package com.petadoption.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String phone;
    private String email;
    private String realName;
}

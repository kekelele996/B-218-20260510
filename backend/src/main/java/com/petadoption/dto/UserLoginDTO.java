package com.petadoption.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String account;
    private String password;
    private Boolean remember;
}

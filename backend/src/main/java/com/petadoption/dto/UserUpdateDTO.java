package com.petadoption.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String phone;
    private String email;
    private String avatar;
    private String address;
}

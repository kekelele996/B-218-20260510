package com.petadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String realName;
    private String avatar;
    private String address;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

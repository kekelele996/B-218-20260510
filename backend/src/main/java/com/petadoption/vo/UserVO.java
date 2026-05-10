package com.petadoption.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String realName;
    private String avatar;
    private String address;
    private LocalDateTime createTime;
}

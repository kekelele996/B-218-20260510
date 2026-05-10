package com.petadoption.service;

import com.petadoption.dto.PasswordUpdateDTO;
import com.petadoption.dto.UserLoginDTO;
import com.petadoption.dto.UserRegisterDTO;
import com.petadoption.dto.UserUpdateDTO;
import com.petadoption.vo.LoginVO;
import com.petadoption.vo.UserVO;

public interface UserService {

    void register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    UserVO getUserInfo(Long userId);

    void updateUserInfo(Long userId, UserUpdateDTO dto);

    void updatePassword(Long userId, PasswordUpdateDTO dto);

    boolean checkUsername(String username);

    boolean checkPhone(String phone);
}

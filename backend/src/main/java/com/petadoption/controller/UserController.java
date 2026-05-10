package com.petadoption.controller;

import com.petadoption.dto.PasswordUpdateDTO;
import com.petadoption.dto.UserLoginDTO;
import com.petadoption.dto.UserRegisterDTO;
import com.petadoption.dto.UserUpdateDTO;
import com.petadoption.service.UserService;
import com.petadoption.util.UserContext;
import com.petadoption.vo.LoginVO;
import com.petadoption.vo.Result;
import com.petadoption.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody UserLoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，前端清除token即可
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        userService.updateUserInfo(userId, dto);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功");
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check/username")
    public Result<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean available = userService.checkUsername(username);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return Result.success(result);
    }

    /**
     * 检查手机号是否可用
     */
    @GetMapping("/check/phone")
    public Result<Map<String, Boolean>> checkPhone(@RequestParam String phone) {
        boolean available = userService.checkPhone(phone);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return Result.success(result);
    }
}

package com.petadoption.service.impl;

import com.petadoption.dto.PasswordUpdateDTO;
import com.petadoption.dto.UserLoginDTO;
import com.petadoption.dto.UserRegisterDTO;
import com.petadoption.dto.UserUpdateDTO;
import com.petadoption.entity.User;
import com.petadoption.exception.BusinessException;
import com.petadoption.mapper.UserMapper;
import com.petadoption.service.UserService;
import com.petadoption.util.JwtUtil;
import com.petadoption.util.PasswordUtil;
import com.petadoption.util.ValidationUtil;
import com.petadoption.vo.LoginVO;
import com.petadoption.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public void register(UserRegisterDTO dto) {
        // 参数校验
        if (!ValidationUtil.isValidUsername(dto.getUsername())) {
            throw BusinessException.badRequest("用户名格式不正确，需要4-20位字母数字下划线");
        }
        if (!ValidationUtil.isValidPassword(dto.getPassword())) {
            throw BusinessException.badRequest("密码格式不正确，需要6-20位且包含字母和数字");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw BusinessException.badRequest("两次密码输入不一致");
        }
        if (!ValidationUtil.isValidPhone(dto.getPhone())) {
            throw BusinessException.badRequest("手机号格式不正确");
        }
        if (!ValidationUtil.isValidEmail(dto.getEmail())) {
            throw BusinessException.badRequest("邮箱格式不正确");
        }
        if (!ValidationUtil.isValidRealName(dto.getRealName())) {
            throw BusinessException.badRequest("真实姓名需要2-10位中文");
        }

        // 检查用户名是否存在
        if (userMapper.existsByUsername(dto.getUsername())) {
            throw BusinessException.conflict("用户名已存在");
        }

        // 检查手机号是否存在
        if (userMapper.existsByPhone(dto.getPhone())) {
            throw BusinessException.conflict("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRealName(dto.getRealName());
        user.setAvatar("/images/avatars/default.png");
        user.setStatus(1);

        userMapper.insert(user);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        if (!ValidationUtil.isNotEmpty(dto.getAccount())) {
            throw BusinessException.badRequest("账号不能为空");
        }
        if (!ValidationUtil.isNotEmpty(dto.getPassword())) {
            throw BusinessException.badRequest("密码不能为空");
        }

        // 查找用户（支持用户名或手机号登录）
        User user = userMapper.selectByUsername(dto.getAccount());
        if (user == null) {
            user = userMapper.selectByPhone(dto.getAccount());
        }

        if (user == null || !PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("账号或密码错误");
        }

        if (user.getStatus() == 0) {
            throw BusinessException.forbidden("账号已被禁用");
        }

        // 生成Token
        boolean remember = dto.getRemember() != null && dto.getRemember();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), remember);

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 手机号脱敏
        userVO.setPhone(maskPhone(user.getPhone()));
        // 邮箱脱敏
        userVO.setEmail(maskEmail(user.getEmail()));
        loginVO.setUser(userVO);

        return loginVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        // 检查手机号是否被其他用户使用
        if (dto.getPhone() != null && !dto.getPhone().equals(user.getPhone())) {
            if (!ValidationUtil.isValidPhone(dto.getPhone())) {
                throw BusinessException.badRequest("手机号格式不正确");
            }
            if (userMapper.existsByPhoneAndIdNot(dto.getPhone(), userId)) {
                throw BusinessException.conflict("手机号已被其他用户使用");
            }
        }

        // 校验邮箱格式
        if (dto.getEmail() != null && !ValidationUtil.isValidEmail(dto.getEmail())) {
            throw BusinessException.badRequest("邮箱格式不正确");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPhone(dto.getPhone());
        updateUser.setEmail(dto.getEmail());
        updateUser.setAvatar(dto.getAvatar());
        updateUser.setAddress(dto.getAddress());

        userMapper.update(updateUser);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        if (!ValidationUtil.isNotEmpty(dto.getOldPassword())) {
            throw BusinessException.badRequest("原密码不能为空");
        }
        if (!ValidationUtil.isValidPassword(dto.getNewPassword())) {
            throw BusinessException.badRequest("新密码格式不正确，需要6-20位且包含字母和数字");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw BusinessException.badRequest("两次密码输入不一致");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("原密码错误");
        }

        // 更新密码
        userMapper.updatePassword(userId, PasswordUtil.encode(dto.getNewPassword()));
    }

    @Override
    public boolean checkUsername(String username) {
        return !userMapper.existsByUsername(username);
    }

    @Override
    public boolean checkPhone(String phone) {
        return !userMapper.existsByPhone(phone);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}

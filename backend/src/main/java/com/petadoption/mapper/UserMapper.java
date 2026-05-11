package com.petadoption.mapper;

import com.petadoption.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insert(User user);

    User selectById(Long id);

    User selectByUsername(String username);

    User selectByPhone(String phone);

    int update(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(@Param("phone") String phone, @Param("id") Long id);
}

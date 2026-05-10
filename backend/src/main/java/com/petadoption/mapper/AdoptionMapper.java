package com.petadoption.mapper;

import com.petadoption.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdoptionMapper {

    int insert(AdoptionApplication application);

    List<AdoptionApplication> selectByUserId(@Param("userId") Long userId,
                                              @Param("status") Integer status,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    long selectCountByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    AdoptionApplication selectById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    boolean existsByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);
}

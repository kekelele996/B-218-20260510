package com.petadoption.mapper;

import com.petadoption.dto.PetQueryDTO;
import com.petadoption.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {

    List<Pet> selectList(@Param("query") PetQueryDTO query, @Param("offset") int offset);

    long selectCount(@Param("query") PetQueryDTO query);

    Pet selectById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

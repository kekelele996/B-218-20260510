package com.petadoption.mapper;

import com.petadoption.entity.BreedDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BreedMapper {

    List<BreedDict> selectByPetType(@Param("petType") String petType);

    List<BreedDict> selectAll();

    List<String> selectDistinctPetTypes();
}

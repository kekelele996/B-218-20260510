package com.petadoption.service;

import com.petadoption.vo.BreedVO;

import java.util.List;

public interface BreedService {

    List<BreedVO> getBreedList(String petType);
}

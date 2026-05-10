package com.petadoption.service;

import com.petadoption.dto.PetQueryDTO;
import com.petadoption.vo.PageResult;
import com.petadoption.vo.PetDetailVO;
import com.petadoption.vo.PetVO;

public interface PetService {

    PageResult<PetVO> getPetList(PetQueryDTO query);

    PetDetailVO getPetDetail(Long id);
}

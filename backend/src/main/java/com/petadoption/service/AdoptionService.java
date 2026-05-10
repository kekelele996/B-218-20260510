package com.petadoption.service;

import com.petadoption.dto.AdoptionApplyDTO;
import com.petadoption.vo.AdoptionDetailVO;
import com.petadoption.vo.AdoptionVO;
import com.petadoption.vo.PageResult;

public interface AdoptionService {

    Long apply(Long userId, AdoptionApplyDTO dto);

    PageResult<AdoptionVO> getMyApplications(Long userId, Integer status, Integer page, Integer pageSize);

    AdoptionDetailVO getApplicationDetail(Long userId, Long applicationId);

    void cancelApplication(Long userId, Long applicationId);
}

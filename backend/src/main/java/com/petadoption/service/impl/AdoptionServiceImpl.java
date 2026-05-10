package com.petadoption.service.impl;

import com.petadoption.dto.AdoptionApplyDTO;
import com.petadoption.entity.AdoptionApplication;
import com.petadoption.entity.Pet;
import com.petadoption.exception.BusinessException;
import com.petadoption.mapper.AdoptionMapper;
import com.petadoption.mapper.PetMapper;
import com.petadoption.service.AdoptionService;
import com.petadoption.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionServiceImpl implements AdoptionService {

    @Autowired
    private AdoptionMapper adoptionMapper;

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public Long apply(Long userId, AdoptionApplyDTO dto) {
        // 校验宠物是否存在且可领养
        Pet pet = petMapper.selectById(dto.getPetId());
        if (pet == null) {
            throw BusinessException.notFound("宠物不存在");
        }
        if (pet.getStatus() != 1) {
            throw BusinessException.badRequest("该宠物已被领养或下架");
        }

        // 检查是否已有进行中的申请
        if (adoptionMapper.existsByUserIdAndPetId(userId, dto.getPetId())) {
            throw BusinessException.conflict("您已提交过该宠物的领养申请");
        }

        // 校验必填字段
        if (dto.getLivingType() == null || dto.getLivingType().isEmpty()) {
            throw BusinessException.badRequest("请选择居住类型");
        }
        if (dto.getHousingType() == null || dto.getHousingType().isEmpty()) {
            throw BusinessException.badRequest("请选择住房类型");
        }
        if (dto.getHasYard() == null) {
            throw BusinessException.badRequest("请选择是否有院子");
        }
        if (dto.getExperience() == null || dto.getExperience().isEmpty()) {
            throw BusinessException.badRequest("请选择养宠经验");
        }
        if (dto.getDailyTime() == null || dto.getDailyTime().isEmpty()) {
            throw BusinessException.badRequest("请选择每日陪伴时间");
        }
        if (dto.getReason() == null || dto.getReason().length() < 50 || dto.getReason().length() > 500) {
            throw BusinessException.badRequest("领养理由需要50-500字");
        }
        if (dto.getContactAddress() == null || dto.getContactAddress().isEmpty()) {
            throw BusinessException.badRequest("请填写联系地址");
        }

        // 创建申请
        AdoptionApplication application = new AdoptionApplication();
        application.setUserId(userId);
        application.setPetId(dto.getPetId());
        application.setLivingType(dto.getLivingType());
        application.setHousingType(dto.getHousingType());
        application.setHasYard(dto.getHasYard() ? 1 : 0);
        application.setExperience(dto.getExperience());
        application.setDailyTime(dto.getDailyTime());
        application.setReason(dto.getReason());
        application.setContactAddress(dto.getContactAddress());
        application.setStatus(0); // 待审核

        // 处理现有宠物列表
        if (dto.getCurrentPets() != null && !dto.getCurrentPets().isEmpty()) {
            application.setCurrentPets(String.join(",", dto.getCurrentPets()));
        }

        adoptionMapper.insert(application);

        return application.getId();
    }

    @Override
    public PageResult<AdoptionVO> getMyApplications(Long userId, Integer status, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (page - 1) * pageSize;

        List<AdoptionApplication> applications = adoptionMapper.selectByUserId(userId, status, offset, pageSize);
        long total = adoptionMapper.selectCountByUserId(userId, status);

        List<AdoptionVO> voList = applications.stream().map(app -> {
            AdoptionVO vo = new AdoptionVO();
            vo.setId(app.getId());
            vo.setStatus(app.getStatus());
            vo.setStatusText(getStatusText(app.getStatus()));
            vo.setCreateTime(app.getCreateTime());

            // 获取宠物信息
            Pet pet = petMapper.selectById(app.getPetId());
            if (pet != null) {
                PetSimpleVO petVO = new PetSimpleVO();
                petVO.setId(pet.getId());
                petVO.setName(pet.getName());
                petVO.setBreed(pet.getBreed());
                petVO.setType(pet.getType());
                petVO.setAge(pet.getAge());
                petVO.setGender(pet.getGender());
                if (pet.getImages() != null && !pet.getImages().isEmpty()) {
                    petVO.setImage(pet.getImages().split(",")[0]);
                }
                vo.setPet(petVO);
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, total, page, pageSize);
    }

    @Override
    public AdoptionDetailVO getApplicationDetail(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionMapper.selectById(applicationId);
        if (application == null) {
            throw BusinessException.notFound("申请不存在");
        }

        // 校验是否是本人的申请
        if (!application.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权查看此申请");
        }

        AdoptionDetailVO vo = new AdoptionDetailVO();
        vo.setId(application.getId());
        vo.setLivingType(application.getLivingType());
        vo.setHousingType(application.getHousingType());
        vo.setHasYard(application.getHasYard() != null && application.getHasYard() == 1);
        vo.setExperience(application.getExperience());
        vo.setDailyTime(application.getDailyTime());
        vo.setReason(application.getReason());
        vo.setContactAddress(application.getContactAddress());
        vo.setStatus(application.getStatus());
        vo.setStatusText(getStatusText(application.getStatus()));
        vo.setRemark(application.getRemark());
        vo.setCreateTime(application.getCreateTime());
        vo.setUpdateTime(application.getUpdateTime());

        // 解析现有宠物
        if (application.getCurrentPets() != null && !application.getCurrentPets().isEmpty()) {
            vo.setCurrentPets(Arrays.asList(application.getCurrentPets().split(",")));
        } else {
            vo.setCurrentPets(Collections.emptyList());
        }

        // 获取宠物信息
        Pet pet = petMapper.selectById(application.getPetId());
        if (pet != null) {
            PetSimpleVO petVO = new PetSimpleVO();
            petVO.setId(pet.getId());
            petVO.setName(pet.getName());
            petVO.setBreed(pet.getBreed());
            petVO.setType(pet.getType());
            petVO.setAge(pet.getAge());
            petVO.setGender(pet.getGender());
            if (pet.getImages() != null && !pet.getImages().isEmpty()) {
                petVO.setImage(pet.getImages().split(",")[0]);
            }
            vo.setPet(petVO);
        }

        return vo;
    }

    @Override
    @Transactional
    public void cancelApplication(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionMapper.selectById(applicationId);
        if (application == null) {
            throw BusinessException.notFound("申请不存在");
        }

        // 校验是否是本人的申请
        if (!application.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权操作此申请");
        }

        // 只有待审核状态可以取消
        if (application.getStatus() != 0) {
            throw BusinessException.badRequest("当前状态不可取消");
        }

        adoptionMapper.updateStatus(applicationId, 4); // 4=已取消
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核中";
            case 2: return "已通过";
            case 3: return "已拒绝";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}

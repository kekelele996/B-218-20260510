package com.petadoption.service.impl;

import com.petadoption.dto.PetQueryDTO;
import com.petadoption.entity.Pet;
import com.petadoption.exception.BusinessException;
import com.petadoption.mapper.PetMapper;
import com.petadoption.service.PetService;
import com.petadoption.vo.PageResult;
import com.petadoption.vo.PetDetailVO;
import com.petadoption.vo.PetVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetMapper petMapper;

    @Override
    public PageResult<PetVO> getPetList(PetQueryDTO query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(12);
        }

        int offset = (query.getPage() - 1) * query.getPageSize();

        List<Pet> pets = petMapper.selectList(query, offset);
        long total = petMapper.selectCount(query);

        List<PetVO> voList = pets.stream().map(this::convertToVO).collect(Collectors.toList());

        return new PageResult<>(voList, total, query.getPage(), query.getPageSize());
    }

    @Override
    public PetDetailVO getPetDetail(Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            throw BusinessException.notFound("宠物不存在");
        }

        PetDetailVO vo = new PetDetailVO();
        BeanUtils.copyProperties(pet, vo);

        // 设置年龄显示
        vo.setAgeDisplay(formatAge(pet.getAge()));

        // 设置状态文本
        vo.setStatusText(getStatusText(pet.getStatus()));

        // 设置疫苗和绝育状态
        vo.setVaccinated(pet.getVaccinated() != null && pet.getVaccinated() == 1);
        vo.setNeutered(pet.getNeutered() != null && pet.getNeutered() == 1);

        // 解析图片列表
        if (pet.getImages() != null && !pet.getImages().isEmpty()) {
            vo.setImages(Arrays.asList(pet.getImages().split(",")));
        } else {
            vo.setImages(Collections.emptyList());
        }

        return vo;
    }

    private PetVO convertToVO(Pet pet) {
        PetVO vo = new PetVO();
        vo.setId(pet.getId());
        vo.setName(pet.getName());
        vo.setType(pet.getType());
        vo.setBreed(pet.getBreed());
        vo.setAge(pet.getAge());
        vo.setAgeDisplay(formatAge(pet.getAge()));
        vo.setGender(pet.getGender());
        vo.setSize(pet.getSize());
        vo.setPersonality(pet.getPersonality());
        vo.setStatus(pet.getStatus());
        vo.setStatusText(getStatusText(pet.getStatus()));

        // 取第一张图片作为封面
        if (pet.getImages() != null && !pet.getImages().isEmpty()) {
            String[] images = pet.getImages().split(",");
            vo.setImage(images[0]);
        }

        return vo;
    }

    private String formatAge(Integer ageInMonths) {
        if (ageInMonths == null) return "未知";
        if (ageInMonths < 12) {
            return ageInMonths + "个月";
        } else {
            int years = ageInMonths / 12;
            int months = ageInMonths % 12;
            if (months == 0) {
                return years + "岁";
            } else {
                return years + "岁" + months + "个月";
            }
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "可领养";
            case 2: return "已领养";
            case 0: return "已下架";
            default: return "未知";
        }
    }
}

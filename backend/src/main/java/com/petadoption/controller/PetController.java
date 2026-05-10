package com.petadoption.controller;

import com.petadoption.dto.PetQueryDTO;
import com.petadoption.service.PetService;
import com.petadoption.vo.PageResult;
import com.petadoption.vo.PetDetailVO;
import com.petadoption.vo.PetVO;
import com.petadoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取宠物列表
     */
    @GetMapping("/list")
    public Result<PageResult<PetVO>> getPetList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String ageRange,
            @RequestParam(required = false, defaultValue = "1") Integer status,
            @RequestParam(required = false, defaultValue = "newest") String sortBy) {

        PetQueryDTO query = new PetQueryDTO();
        query.setPage(page);
        query.setPageSize(pageSize);
        query.setType(type);
        query.setBreed(breed);
        query.setGender(gender);
        query.setSize(size);
        query.setAgeRange(ageRange);
        query.setStatus(status);
        query.setSortBy(sortBy);

        PageResult<PetVO> result = petService.getPetList(query);
        return Result.success(result);
    }

    /**
     * 获取宠物详情
     */
    @GetMapping("/{id}")
    public Result<PetDetailVO> getPetDetail(@PathVariable Long id) {
        PetDetailVO detail = petService.getPetDetail(id);
        return Result.success(detail);
    }
}

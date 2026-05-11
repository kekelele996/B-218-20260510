package com.petadoption.controller;

import com.petadoption.service.BreedService;
import com.petadoption.vo.BreedVO;
import com.petadoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breed")
public class BreedController {

    @Autowired
    private BreedService breedService;

    /**
     * 获取品种列表
     */
    @GetMapping("/list")
    public Result<List<BreedVO>> getBreedList(@RequestParam(required = false) String petType) {
        List<BreedVO> breeds = breedService.getBreedList(petType);
        return Result.success(breeds);
    }
}

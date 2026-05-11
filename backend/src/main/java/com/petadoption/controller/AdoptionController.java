package com.petadoption.controller;

import com.petadoption.dto.AdoptionApplyDTO;
import com.petadoption.service.AdoptionService;
import com.petadoption.util.UserContext;
import com.petadoption.vo.AdoptionDetailVO;
import com.petadoption.vo.AdoptionVO;
import com.petadoption.vo.PageResult;
import com.petadoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    /**
     * 提交领养申请
     */
    @PostMapping("/apply")
    public Result<Map<String, Long>> apply(@RequestBody AdoptionApplyDTO dto) {
        Long userId = UserContext.getUserId();
        Long applicationId = adoptionService.apply(userId, dto);

        Map<String, Long> result = new HashMap<>();
        result.put("applicationId", applicationId);
        return Result.success("申请提交成功", result);
    }

    /**
     * 获取我的申请列表
     */
    @GetMapping("/list")
    public Result<PageResult<AdoptionVO>> getMyApplications(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {

        Long userId = UserContext.getUserId();
        PageResult<AdoptionVO> result = adoptionService.getMyApplications(userId, status, page, pageSize);
        return Result.success(result);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    public Result<AdoptionDetailVO> getApplicationDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        AdoptionDetailVO detail = adoptionService.getApplicationDetail(userId, id);
        return Result.success(detail);
    }

    /**
     * 取消申请
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelApplication(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        adoptionService.cancelApplication(userId, id);
        return Result.success("申请已取消");
    }
}

package com.petadoption.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdoptionDetailVO {
    private Long id;
    private PetSimpleVO pet;
    private String livingType;
    private String housingType;
    private Boolean hasYard;
    private String experience;
    private List<String> currentPets;
    private String dailyTime;
    private String reason;
    private String contactAddress;
    private Integer status;
    private String statusText;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

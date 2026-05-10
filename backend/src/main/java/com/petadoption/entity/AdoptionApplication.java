package com.petadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionApplication {
    private Long id;
    private Long userId;
    private Long petId;
    private String livingType;
    private String housingType;
    private Integer hasYard;
    private String experience;
    private String currentPets;
    private String dailyTime;
    private String reason;
    private String contactAddress;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

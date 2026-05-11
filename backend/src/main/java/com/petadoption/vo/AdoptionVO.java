package com.petadoption.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionVO {
    private Long id;
    private PetSimpleVO pet;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
}

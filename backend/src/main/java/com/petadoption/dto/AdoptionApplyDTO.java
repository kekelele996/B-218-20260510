package com.petadoption.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdoptionApplyDTO {
    private Long petId;
    private String livingType;
    private String housingType;
    private Boolean hasYard;
    private String experience;
    private List<String> currentPets;
    private String dailyTime;
    private String reason;
    private String contactAddress;
}

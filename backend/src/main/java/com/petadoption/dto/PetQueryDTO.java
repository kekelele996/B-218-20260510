package com.petadoption.dto;

import lombok.Data;

@Data
public class PetQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 12;
    private String type;
    private String breed;
    private String gender;
    private String size;
    private String ageRange;
    private Integer status = 1;
    private String sortBy = "newest";
}

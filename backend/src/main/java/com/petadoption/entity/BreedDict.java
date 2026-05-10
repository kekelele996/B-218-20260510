package com.petadoption.entity;

import lombok.Data;

@Data
public class BreedDict {
    private Integer id;
    private String petType;
    private String breedName;
    private Integer sortOrder;
}

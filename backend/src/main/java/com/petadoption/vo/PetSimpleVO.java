package com.petadoption.vo;

import lombok.Data;

@Data
public class PetSimpleVO {
    private Long id;
    private String name;
    private String breed;
    private String image;
    private String type;
    private Integer age;
    private String gender;
}

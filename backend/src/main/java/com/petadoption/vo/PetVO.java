package com.petadoption.vo;

import lombok.Data;

@Data
public class PetVO {
    private Long id;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String ageDisplay;
    private String gender;
    private String size;
    private String image;
    private String personality;
    private Integer status;
    private String statusText;
}

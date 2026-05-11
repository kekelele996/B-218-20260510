package com.petadoption.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Pet {
    private Long id;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String gender;
    private String size;
    private String color;
    private BigDecimal weight;
    private Integer vaccinated;
    private Integer neutered;
    private String healthDesc;
    private String personality;
    private String requirement;
    private String images;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

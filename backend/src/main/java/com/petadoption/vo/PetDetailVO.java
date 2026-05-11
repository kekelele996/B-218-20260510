package com.petadoption.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PetDetailVO {
    private Long id;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String ageDisplay;
    private String gender;
    private String size;
    private String color;
    private BigDecimal weight;
    private Boolean vaccinated;
    private Boolean neutered;
    private String healthDesc;
    private String personality;
    private String requirement;
    private List<String> images;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
}

package com.petadoption.vo;

import lombok.Data;
import java.util.List;

@Data
public class BreedVO {
    private String petType;
    private List<String> breeds;
}

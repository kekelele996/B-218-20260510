package com.petadoption.service.impl;

import com.petadoption.entity.BreedDict;
import com.petadoption.mapper.BreedMapper;
import com.petadoption.service.BreedService;
import com.petadoption.vo.BreedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BreedServiceImpl implements BreedService {

    @Autowired
    private BreedMapper breedMapper;

    @Override
    public List<BreedVO> getBreedList(String petType) {
        List<BreedDict> breeds;

        if (petType != null && !petType.isEmpty()) {
            breeds = breedMapper.selectByPetType(petType);
            if (breeds.isEmpty()) {
                return new ArrayList<>();
            }
            BreedVO vo = new BreedVO();
            vo.setPetType(petType);
            vo.setBreeds(breeds.stream().map(BreedDict::getBreedName).collect(Collectors.toList()));
            List<BreedVO> result = new ArrayList<>();
            result.add(vo);
            return result;
        }

        // 获取所有品种并按类型分组
        breeds = breedMapper.selectAll();

        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (BreedDict breed : breeds) {
            grouped.computeIfAbsent(breed.getPetType(), k -> new ArrayList<>())
                   .add(breed.getBreedName());
        }

        List<BreedVO> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            BreedVO vo = new BreedVO();
            vo.setPetType(entry.getKey());
            vo.setBreeds(entry.getValue());
            result.add(vo);
        }

        return result;
    }
}

package com.zoohse.factory.impl;

import com.zoohse.domain.animals.Animal;
import com.zoohse.factory.AnimalFactory;
import com.zoohse.factory.AnimalAbstractFactory;
import com.zoohse.factory.types.AnimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DefaultAnimalAbstractFactory implements AnimalAbstractFactory {
    private final Map<AnimalType, AnimalFactory> factoryMap;

    @Autowired
    public DefaultAnimalAbstractFactory(List<AnimalFactory> factories) {
        this.factoryMap = createFactoryMap(factories);
    }

    private Map<AnimalType, AnimalFactory> createFactoryMap(List<AnimalFactory> factories) {
        Map<AnimalType, AnimalFactory> map = new HashMap<>();

        for (AnimalFactory factory : factories) {
            List<AnimalType> supportedTypes = Arrays.stream(AnimalType.values())
                    .filter(factory::supports)
                    .collect(Collectors.toList());

            for (AnimalType type : supportedTypes) {
                if (map.containsKey(type)) {
                    throw new IllegalStateException("Multiple factories support type: " + type);
                }
                map.put(type, factory);
            }
        }

        return map;
    }

    @Override
    public Animal createAnimal(AnimalType animalType, String name, int food, int health, int number, int... params) {
        AnimalFactory factory = factoryMap.get(animalType);
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for animal type: " + animalType);
        }
        return factory.createAnimal(animalType, name, food, health, number, params);
    }
}
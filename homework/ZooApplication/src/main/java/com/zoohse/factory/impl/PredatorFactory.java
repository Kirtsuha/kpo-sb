package com.zoohse.factory.impl;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Tiger;
import com.zoohse.domain.animals.Wolf;
import com.zoohse.factory.AnimalFactory;
import com.zoohse.factory.types.AnimalType;
import org.springframework.stereotype.Component;


@Component
public class PredatorFactory implements AnimalFactory {
    public PredatorFactory() {}

    @Override
    public Animal createAnimal(AnimalType specificType, String name, int food, int health, int number, int ... params) {
        return switch (specificType) {
            case TIGER -> new Tiger(name, food, health, number);
            case WOLF -> new Wolf(name, food, health, number);
            default -> throw new IllegalArgumentException("Unknown animal predator type: " + specificType);
        };
    }

    @Override
    public boolean supports(AnimalType animalType) {
        return animalType.isPredator();
    }
}

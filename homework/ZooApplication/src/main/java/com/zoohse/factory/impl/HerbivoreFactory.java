package com.zoohse.factory.impl;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Monkey;
import com.zoohse.domain.animals.Rabbit;
import com.zoohse.factory.AnimalFactory;
import com.zoohse.factory.types.AnimalType;
import org.springframework.stereotype.Component;


@Component
public class HerbivoreFactory implements AnimalFactory {
    public HerbivoreFactory() {}

    @Override
    public Animal createAnimal(AnimalType specificType, String name, int food, int health, int number, int ... params) {
        int kindness = params[0];
        return switch (specificType) {
            case MONKEY -> new Monkey(name, food, health, number, kindness);
            case RABBIT -> new Rabbit(name, food, health, number, kindness);
            default -> throw new IllegalArgumentException("Unknown animal herbivore type: " + specificType);
        };
    }

    @Override
    public boolean supports(AnimalType animalType) {
        return animalType.isHerbivore();
    }
}

package com.zoohse.factory;

import com.zoohse.domain.animals.Animal;
import com.zoohse.factory.types.AnimalType;

public interface AnimalFactory {
    public Animal createAnimal(AnimalType specificType, String name, int food, int health, int number, int ... params);

    boolean supports(AnimalType animalType);
}

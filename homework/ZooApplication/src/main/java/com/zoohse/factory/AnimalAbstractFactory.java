package com.zoohse.factory;

import com.zoohse.domain.animals.Animal;
import com.zoohse.factory.types.AnimalType;

public interface AnimalAbstractFactory {
    Animal createAnimal(AnimalType animalType, String name, int food, int health, int number, int ... params);
}

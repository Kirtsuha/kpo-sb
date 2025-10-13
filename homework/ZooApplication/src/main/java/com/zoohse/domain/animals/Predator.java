package com.zoohse.domain.animals;

import lombok.Getter;

@Getter
public abstract class Predator extends Animal{

    Predator(String name, int food, int health, int number) {
        super(name, food, health, number);
    }
}

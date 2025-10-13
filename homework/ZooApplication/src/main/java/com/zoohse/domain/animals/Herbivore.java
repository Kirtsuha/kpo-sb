package com.zoohse.domain.animals;

import lombok.Getter;

@Getter
public abstract class Herbivore extends Animal{
    int kindness;
    Herbivore(String name, int food, int health, int number, int kindness) {
        super(name, food, health, number);
        this.kindness = kindness;
        if (kindness > 7) {
            this.canBeInContactZoo = true;
        }
    }

    public int getKindness() {
        return kindness;
    }
}

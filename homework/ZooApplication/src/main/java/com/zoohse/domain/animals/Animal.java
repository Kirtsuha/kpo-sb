package com.zoohse.domain.animals;

import com.zoohse.domain.Alive;
import com.zoohse.domain.Inventory;
import com.zoohse.domain.Nameable;
import lombok.Getter;

@Getter
public abstract class Animal implements Alive, Inventory, Nameable {
    String name;
    int number;
    int health;
    int food;
    boolean canBeInContactZoo = false;

    Animal(String name, int food, int health, int number) {
        this.name = name;
        this.food = food;
        this.health = health;
        this.number = number;
    }

    public boolean getCanBeInContactZoo() {
        return canBeInContactZoo;
    }
}

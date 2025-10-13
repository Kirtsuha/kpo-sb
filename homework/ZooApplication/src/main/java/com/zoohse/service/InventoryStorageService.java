package com.zoohse.service;

import com.zoohse.domain.Inventory;
import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.things.Thing;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryStorageService {
    private final List<Inventory> inventories = new ArrayList<>();

    public InventoryStorageService() {}

    public void addAnimal(Animal animal) {
        inventories.add(animal);
    }

    public void addThing(Thing thing) {
        inventories.add(thing);
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public List<Thing> getThings() {
        return inventories.stream()
                .filter(item -> item instanceof Thing)
                .map(item -> (Thing) item)
                .collect(Collectors.toList());
    }

    public List<Animal> getAnimals() {
        return inventories.stream()
                .filter(item -> item instanceof Animal)
                .map(item -> (Animal) item)
                .collect(Collectors.toList());
    }

    public int getTotalFoodConsumption() {
        return inventories.stream()
                .filter(item -> item instanceof Animal)
                .map(item -> (Animal) item)
                .mapToInt(Animal::getFood)
                .sum();
    }

    public List<Animal> getAnimalsForContactZoo() {
        return inventories.stream()
                .filter(item -> item instanceof Animal)
                .map(item -> (Animal) item)
                .filter(Animal::getCanBeInContactZoo)
                .collect(Collectors.toList());
    }
}

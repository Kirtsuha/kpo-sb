package com.zoohse.service;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Herbivore;
import com.zoohse.domain.things.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final InventoryStorageService inventoryService;

    @Autowired
    public ReportService(InventoryStorageService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void printInventoryReport() {
        printAnimalReport();
        printThingReport();
    }

    public void printAnimalReport() {
        List<Animal> animals = inventoryService.getAnimals();

        System.out.println("=== ANIMALS REPORT ===");
        System.out.println("Total animals: " + animals.size());
        System.out.println("Total food consumption: " + printTotalFood());

        animals.forEach(animal ->
                System.out.println(animal.getNumber() + ": " + animal.getName() +
                        " (" + animal.getClass().getSimpleName() + ")")
        );
    }

    public void printThingReport() {
        List<Thing> things = inventoryService.getThings();

        System.out.println("=== THINGS REPORT ===");
        System.out.println("Total things: " + things.size());

        things.forEach(thing ->
                System.out.println(thing.getNumber() + ": " + thing.getName() +
                        " (" + thing.getClass().getSimpleName() + ")")
        );
    }

    public void printContactZooAnimals() {
        List<Animal> contactAnimals = inventoryService.getAnimals().stream()
                .filter(Animal::getCanBeInContactZoo)
                .collect(Collectors.toList());

        System.out.println("=== CONTACT ZOO ANIMALS ===");
        contactAnimals.forEach(animal ->
                System.out.println(animal.getName() + " - Kindness: " +
                        ((Herbivore) animal).getKindness())
        );
    }

    public int printTotalFood() {
        System.out.println("=== TOTAL FOOD CONSUMPTION ===");
        return inventoryService.getTotalFoodConsumption();
    }
}
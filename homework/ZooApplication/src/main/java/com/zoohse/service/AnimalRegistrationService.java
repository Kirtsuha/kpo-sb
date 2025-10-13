package com.zoohse.service;

import com.zoohse.domain.animals.Animal;
import com.zoohse.exceptions.AnimalRejectedException;
import com.zoohse.factory.AnimalAbstractFactory;
import com.zoohse.factory.types.AnimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimalRegistrationService {
    private final AnimalAbstractFactory animalAbstractFactory;
    private final HealthCheckService healthCheckService;
    private final InventoryNumberGenerator inventoryNumberGenerator;
    private final InventoryStorageService inventoryStorageService;

    @Autowired
    public AnimalRegistrationService(AnimalAbstractFactory animalAbstractFactory,
                                     HealthCheckService healthCheckService,
                                     InventoryNumberGenerator inventoryNumberGenerator,
                                     InventoryStorageService inventoryStorageService) {
        this.animalAbstractFactory = animalAbstractFactory;
        this.healthCheckService = healthCheckService;
        this.inventoryNumberGenerator = inventoryNumberGenerator;
        this.inventoryStorageService = inventoryStorageService;
    }

    public Animal registerAnimal(String type, String name, int food, int health, int ... params) throws AnimalRejectedException {
        AnimalType animalType = AnimalType.fromString(type);
        int number = inventoryNumberGenerator.generateNextNumber();

        Animal animal = animalAbstractFactory.createAnimal(animalType, name, food, health, number, params);
        if (healthCheckService.checkHealth(animal)) {
            inventoryStorageService.addAnimal(animal);
            return animal;
        }
        throw new AnimalRejectedException("Animal is not healthy!");
    }
}

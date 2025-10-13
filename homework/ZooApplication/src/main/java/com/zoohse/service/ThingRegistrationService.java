package com.zoohse.service;

import com.zoohse.domain.things.Thing;
import com.zoohse.factory.ThingFactory;
import com.zoohse.factory.types.ThingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingRegistrationService {
    private final ThingFactory thingFactory;
    private final InventoryNumberGenerator inventoryNumberGenerator;
    private final InventoryStorageService inventoryStorageService;

    @Autowired
    public ThingRegistrationService(ThingFactory thingFactory, InventoryNumberGenerator inventoryNumberGenerator, InventoryStorageService inventoryStorageService) {
        this.thingFactory = thingFactory;
        this.inventoryNumberGenerator = inventoryNumberGenerator;
        this.inventoryStorageService = inventoryStorageService;
    }

    public Thing registerThing(String type, String name) {
        ThingType thingType = ThingType.fromString(type);
        int number = inventoryNumberGenerator.generateNextNumber();

        Thing thing = thingFactory.createThing(thingType, name, number);
        inventoryStorageService.addThing(thing);
        return thing;
    }
}

package com.zoohse.service;

import com.zoohse.domain.Inventory;
import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Rabbit;
import com.zoohse.domain.animals.Tiger;
import com.zoohse.domain.things.Computer;
import com.zoohse.domain.things.Table;
import com.zoohse.domain.things.Thing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InventoryStorageServiceTest {
    private InventoryStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new InventoryStorageService();
    }

    @Test
    void shouldAddAnimalToInventory() {
        // Given
        Rabbit rabbit = new Rabbit("Bugs", 15, 100, 1, 10);

        // When
        storageService.addAnimal(rabbit);

        // Then
        List<Inventory> allItems = storageService.getInventories();
        assertEquals(1, allItems.size());
        assertEquals(rabbit, allItems.get(0));
    }

    @Test
    void shouldAddThingToInventory() {
        // Given
        Computer computer = new Computer("Office PC", 1);

        // When
        storageService.addThing(computer);

        // Then
        List<Inventory> allItems = storageService.getInventories();
        assertEquals(1, allItems.size());
        assertEquals(computer, allItems.get(0));
    }

    @Test
    void shouldAddMultipleItemsToInventory() {
        // Given
        Rabbit rabbit = new Rabbit("Bugs", 15, 100, 1, 10);
        Tiger tiger = new Tiger("Sherkhan", 300, 90, 2);
        Computer computer = new Computer("Office PC", 3);

        // When
        storageService.addAnimal(rabbit);
        storageService.addAnimal(tiger);
        storageService.addThing(computer);

        // Then
        List<Inventory> allItems = storageService.getInventories();
        assertEquals(3, allItems.size());
        assertTrue(allItems.contains(rabbit));
        assertTrue(allItems.contains(tiger));
        assertTrue(allItems.contains(computer));
    }

    @Test
    void shouldReturnAllAnimalsFromInventory() {
        // Given
        Rabbit rabbit = new Rabbit("Bugs", 85, 1, 7, 10);
        Tiger tiger = new Tiger("Sherkhan", 90, 2, 8);
        Computer computer = new Computer("Office PC", 3);
        Table table = new Table("Meeting Table", 4);

        storageService.addAnimal(rabbit);
        storageService.addAnimal(tiger);
        storageService.addThing(computer);
        storageService.addThing(table);

        // When
        List<Animal> animals = storageService.getAnimals();

        // Then
        assertEquals(2, animals.size());
        assertTrue(animals.contains(rabbit));
        assertTrue(animals.contains(tiger));
        assertFalse(animals.contains(computer));
        assertFalse(animals.contains(table));
    }

    @Test
    void shouldReturnAllThingsFromInventory() {
        // Given
        Rabbit rabbit = new Rabbit("Bugs", 85, 1, 7, 10);
        Computer computer = new Computer("Office PC", 2);
        Table table = new Table("Meeting Table", 3);

        storageService.addAnimal(rabbit);
        storageService.addThing(computer);
        storageService.addThing(table);

        // When
        List<Thing> things = storageService.getThings();

        // Then
        assertEquals(2, things.size());
        assertTrue(things.contains(computer));
        assertTrue(things.contains(table));
        assertFalse(things.contains(rabbit));
    }

    @Test
    void shouldReturnEmptyListWhenNoAnimals() {
        // Given
        Computer computer = new Computer("Office PC", 1);
        Table table = new Table("Meeting Table", 2);

        storageService.addThing(computer);
        storageService.addThing(table);

        // When
        List<Animal> animals = storageService.getAnimals();

        // Then
        assertTrue(animals.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoThings() {
        // Given
        Rabbit rabbit = new Rabbit("Bugs", 85, 1, 7, 10);
        Tiger tiger = new Tiger("Sherkhan", 90, 2, 8);

        storageService.addAnimal(rabbit);
        storageService.addAnimal(tiger);

        // When
        List<Thing> things = storageService.getThings();

        // Then
        assertTrue(things.isEmpty());
    }
}
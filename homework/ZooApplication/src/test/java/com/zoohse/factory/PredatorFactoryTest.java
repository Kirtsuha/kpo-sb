package com.zoohse.factory;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Tiger;
import com.zoohse.domain.animals.Wolf;
import com.zoohse.factory.impl.PredatorFactory;
import com.zoohse.factory.types.AnimalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PredatorFactoryTest {
    private PredatorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PredatorFactory();
    }

    @Test
    void shouldCreateTigerWithCorrectProperties() {
        Animal animal = factory.createAnimal(AnimalType.TIGER, "Sherkhan", 330, 10, 9);

        assertTrue(animal instanceof Tiger);
        assertEquals("Sherkhan", animal.getName());
        assertEquals(330, animal.getFood());
        assertEquals(10, animal.getHealth());
        assertEquals(9, animal.getNumber());
        assertFalse(animal.getCanBeInContactZoo()); // predators cannot be in contact zoo
    }

    @Test
    void shouldCreateWolfWithCorrectProperties() {
        Animal animal = factory.createAnimal(AnimalType.WOLF, "Grey", 5000, 4, 7);

        assertTrue(animal instanceof Wolf);
        assertEquals("Grey", animal.getName());
        assertEquals(5000, animal.getFood());
        assertEquals(4, animal.getHealth());
        assertEquals(7, animal.getNumber());
        assertFalse(animal.getCanBeInContactZoo()); // predators cannot be in contact zoo
    }

    @Test
    void shouldThrowExceptionForNonPredatorType() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createAnimal(AnimalType.RABBIT, "Wrong", 300, 80, 5);
        });
    }
}
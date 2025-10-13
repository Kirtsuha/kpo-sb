package com.zoohse.factory;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Monkey;
import com.zoohse.domain.animals.Rabbit;
import com.zoohse.factory.impl.HerbivoreFactory;
import com.zoohse.factory.types.AnimalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HerbivoreFactoryTest {
    private HerbivoreFactory factory;

    @BeforeEach
    void setUp() {
        factory = new HerbivoreFactory();
    }

    @Test
    void shouldCreateMonkeyWithCorrectProperties() {
        Animal animal = factory.createAnimal(AnimalType.MONKEY, "George", 85, 100, 6, 3);

        assertTrue(animal instanceof Monkey);
        assertEquals("George", animal.getName());
        assertEquals(85, animal.getFood());
        assertEquals(100, animal.getHealth());
        assertEquals(6, animal.getNumber());
        assertEquals(3, ((Monkey) animal).getKindness());
        assertFalse(animal.getCanBeInContactZoo()); // kindness = 3 < 5
    }

    @Test
    void shouldCreateRabbitWithCorrectProperties() {
        Animal animal = factory.createAnimal(AnimalType.RABBIT, "Bugs", 90, 2, 7, 10);

        assertTrue(animal instanceof Rabbit);
        assertEquals("Bugs", animal.getName());
        assertEquals(90, animal.getFood());
        assertEquals(2, animal.getHealth());
        assertEquals(7, animal.getNumber());
        assertEquals(10, ((Rabbit) animal).getKindness());
        assertTrue(animal.getCanBeInContactZoo()); // kindness = 10 > 5
    }

    @Test
    void shouldThrowExceptionForNonHerbivoreType() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createAnimal(AnimalType.TIGER, "Wrong", 5000, 100, 5, 5);
        });
    }
}
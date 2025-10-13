package com.zoohse.factory;

import com.zoohse.factory.types.AnimalType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnimalTypeTest {

    @Test
    void shouldConvertStringToAnimalType() {
        assertEquals(AnimalType.MONKEY, AnimalType.fromString("monkey"));
        assertEquals(AnimalType.RABBIT, AnimalType.fromString("rabbit"));
        assertEquals(AnimalType.TIGER, AnimalType.fromString("tiger"));
        assertEquals(AnimalType.WOLF, AnimalType.fromString("wolf"));
    }

    @Test
    void shouldThrowExceptionForInvalidAnimalType() {
        assertThrows(IllegalArgumentException.class, () -> {
            AnimalType.fromString("dragon");
        });
    }

    @Test
    void shouldIdentifyHerbivoresCorrectly() {
        assertTrue(AnimalType.MONKEY.isHerbivore());
        assertTrue(AnimalType.RABBIT.isHerbivore());
        assertFalse(AnimalType.TIGER.isHerbivore());
        assertFalse(AnimalType.WOLF.isHerbivore());
    }

    @Test
    void shouldIdentifyPredatorsCorrectly() {
        assertFalse(AnimalType.MONKEY.isPredator());
        assertFalse(AnimalType.RABBIT.isPredator());
        assertTrue(AnimalType.TIGER.isPredator());
        assertTrue(AnimalType.WOLF.isPredator());
    }

    @Test
    void shouldConvertAnimalTypeToString() {
        assertEquals("MONKEY", AnimalType.MONKEY.toString());
        assertEquals("RABBIT", AnimalType.RABBIT.toString());
        assertEquals("TIGER", AnimalType.TIGER.toString());
        assertEquals("WOLF", AnimalType.WOLF.toString());
    }
}
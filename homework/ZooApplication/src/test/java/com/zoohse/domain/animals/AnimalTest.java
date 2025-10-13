package com.zoohse.domain.animals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void herbivoreShouldBeInContactZooWithHighKindness() {
        Rabbit friendlyRabbit = new Rabbit("Friendly", 85, 1, 1, 10); // kindness = 7
        assertTrue(friendlyRabbit.getCanBeInContactZoo());
    }

    @Test
    void herbivoreShouldNotBeInContactZooWithLowKindness() {
        Rabbit shyRabbit = new Rabbit("EVIL", 85, 1, 2, 0); // kindness = 3
        assertFalse(shyRabbit.getCanBeInContactZoo());
    }
}
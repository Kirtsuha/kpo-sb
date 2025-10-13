package com.zoohse.factory;

import com.zoohse.domain.things.Thing;
import com.zoohse.domain.things.Computer;
import com.zoohse.domain.things.Table;
import com.zoohse.factory.impl.DefaultThingFactory;
import com.zoohse.factory.types.ThingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DefaultThingFactoryTest {
    private DefaultThingFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultThingFactory();
    }

    @Test
    void shouldCreateComputerWithCorrectProperties() {
        Thing thing = factory.createThing(ThingType.COMPUTER, "Office PC", 1);

        assertTrue(thing instanceof Computer);
        assertEquals("Office PC", thing.getName());
        assertEquals(1, thing.getNumber());
    }

    @Test
    void shouldCreateTableWithCorrectProperties() {
        Thing thing = factory.createThing(ThingType.TABLE, "Meeting Table", 2);

        assertTrue(thing instanceof Table);
        assertEquals("Meeting Table", thing.getName());
        assertEquals(2, thing.getNumber());
    }
}
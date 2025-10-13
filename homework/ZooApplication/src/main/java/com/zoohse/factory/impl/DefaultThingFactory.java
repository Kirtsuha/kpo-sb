package com.zoohse.factory.impl;

import com.zoohse.domain.things.Computer;
import com.zoohse.domain.things.Table;
import com.zoohse.domain.things.Thing;
import com.zoohse.factory.ThingFactory;
import com.zoohse.factory.types.ThingType;
import org.springframework.stereotype.Component;


@Component
public class DefaultThingFactory implements ThingFactory {
    public DefaultThingFactory() {}

    @Override
    public Thing createThing(ThingType specificType, String name, int number) {
        return switch (specificType) {
            case TABLE -> new Table(name, number);
            case COMPUTER -> new Computer(name, number);
            default -> throw new IllegalArgumentException("Unknown thing type: " + specificType);
        };
    }
}

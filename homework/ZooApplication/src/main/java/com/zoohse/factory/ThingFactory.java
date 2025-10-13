package com.zoohse.factory;

import com.zoohse.domain.things.Thing;
import com.zoohse.factory.types.ThingType;

public interface ThingFactory {
    Thing createThing(ThingType specificType, String name, int number);
}

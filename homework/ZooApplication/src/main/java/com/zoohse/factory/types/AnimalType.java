package com.zoohse.factory.types;

public enum AnimalType {
    MONKEY(AnimalCategory.HERBIVORE),
    RABBIT(AnimalCategory.HERBIVORE),
    TIGER(AnimalCategory.PREDATOR),
    WOLF(AnimalCategory.PREDATOR);

    private final AnimalCategory category;

    AnimalType(AnimalCategory category) {
        this.category = category;
    }

    public AnimalCategory getCategory() {
        return category;
    }

    public boolean isHerbivore() {
        return category == AnimalCategory.HERBIVORE;
    }

    public boolean isPredator() {
        return category == AnimalCategory.PREDATOR;
    }

    public static AnimalType fromString(String type) {
        try {
            return AnimalType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}

enum AnimalCategory {
    HERBIVORE, PREDATOR
}
package com.zoohse.domain.things;

import com.zoohse.domain.Inventory;
import com.zoohse.domain.Nameable;
import lombok.Getter;

@Getter
public abstract class Thing implements Inventory, Nameable {
    String name;
    int number;
    public Thing(String name, int number) {
        this.name = name;
        this.number = number;
    }
}

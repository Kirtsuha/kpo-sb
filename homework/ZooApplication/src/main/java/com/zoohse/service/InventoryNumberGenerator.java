package com.zoohse.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryNumberGenerator {
    private final AtomicLong sequence = new AtomicLong(1);

    public int generateNextNumber() {
        return (int) sequence.getAndIncrement();
    }
}

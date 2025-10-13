package com.zoohse.service;

import com.zoohse.domain.animals.Animal;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {
    public HealthCheckService() {}
    public boolean checkHealth(Animal animal) {
        return animal.getHealth() >= 80;
    }
}

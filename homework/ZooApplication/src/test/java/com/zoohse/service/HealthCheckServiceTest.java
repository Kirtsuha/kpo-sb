package com.zoohse.service;

import com.zoohse.domain.animals.Rabbit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthCheckServiceTest {

    @Test
    void shouldApproveHealthyAnimal() {
        HealthCheckService service = new HealthCheckService();
        Rabbit healthyRabbit = new Rabbit("Healthy Kroll", 200, 90, 1, 8);

        assertTrue(service.checkHealth(healthyRabbit));
    }

    @Test
    void shouldRejectUnhealthyAnimal() {
        HealthCheckService service = new HealthCheckService();
        Rabbit unhealthyRabbit = new Rabbit("Sick Kroll", 50, 50, 2, 3);

        assertFalse(service.checkHealth(unhealthyRabbit));
    }
}
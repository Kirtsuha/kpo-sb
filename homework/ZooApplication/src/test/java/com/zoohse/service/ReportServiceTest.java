package com.zoohse.service;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Herbivore;
import com.zoohse.domain.animals.Monkey;
import com.zoohse.domain.animals.Rabbit;
import com.zoohse.domain.animals.Tiger;
import com.zoohse.domain.animals.Wolf;
import com.zoohse.domain.things.Thing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private InventoryStorageService inventoryService;

    private ReportService reportService;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(inventoryService);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void printInventoryReport_CallsBothAnimalAndThingReports() {
        // Given
        when(inventoryService.getAnimals()).thenReturn(Collections.emptyList());
        when(inventoryService.getThings()).thenReturn(Collections.emptyList());

        // When
        reportService.printInventoryReport();

        // Then
        String output = getOutput();
        assertTrue(output.contains("ANIMALS REPORT"));
        assertTrue(output.contains("THINGS REPORT"));
        verify(inventoryService, times(1)).getAnimals();
        verify(inventoryService, times(1)).getThings();
    }

    @Test
    void printAnimalReport_WithAnimals_PrintsCorrectInformation() {
        // Given
        List<Animal> animals = Arrays.asList(
                new Monkey("Charlie", 50, 80, 1, 7),
                new Tiger("Tigra", 100, 90, 2),
                new Wolf("Fang", 80, 85, 3)
        );

        when(inventoryService.getAnimals()).thenReturn(animals);
        when(inventoryService.getTotalFoodConsumption()).thenReturn(230);

        // When
        reportService.printAnimalReport();

        // Then
        String output = getOutput();
        assertTrue(output.contains("=== ANIMALS REPORT ==="));
        assertTrue(output.contains("Total animals: 3"));
        assertTrue(output.contains("Total food consumption: 230"));
        assertTrue(output.contains("1: Charlie (Monkey)"));
        assertTrue(output.contains("2: Tigra (Tiger)"));
        assertTrue(output.contains("3: Fang (Wolf)"));

        verify(inventoryService).getAnimals();
        verify(inventoryService).getTotalFoodConsumption();
    }

    @Test
    void printAnimalReport_EmptyList_PrintsZeroCount() {
        // Given
        when(inventoryService.getAnimals()).thenReturn(Collections.emptyList());
        when(inventoryService.getTotalFoodConsumption()).thenReturn(0);

        // When
        reportService.printAnimalReport();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Total animals: 0"));
        assertTrue(output.contains("Total food consumption: 0"));
    }

    @Test
    void printThingReport_WithThings_PrintsCorrectInformation() {
        // Given
        List<Thing> things = Arrays.asList(
                new TestThing("Office Table", 1),
                new TestThing("Gaming Computer", 2),
                new TestThing("Meeting Chair", 3)
        );

        when(inventoryService.getThings()).thenReturn(things);

        // When
        reportService.printThingReport();

        // Then
        String output = getOutput();
        assertTrue(output.contains("=== THINGS REPORT ==="));
        assertTrue(output.contains("Total things: 3")); // Обратите внимание: здесь опечатка в требовании - должно быть "Total things"
        assertTrue(output.contains("1: Office Table (TestThing)"));
        assertTrue(output.contains("2: Gaming Computer (TestThing)"));
        assertTrue(output.contains("3: Meeting Chair (TestThing)"));

        verify(inventoryService).getThings();
    }

    @Test
    void printContactZooAnimals_WithHerbivores_PrintsOnlyContactAnimals() {
        // Given
        Monkey friendlyMonkey = new Monkey("Friendly", 30, 90, 1, 9); // Высокая доброта
        Rabbit shyRabbit = new Rabbit("Shy", 20, 85, 2, 3); // Низкая доброта
        Tiger dangerousTiger = new Tiger("Danger", 100, 95, 3); // Хищник

        List<Animal> animals = Arrays.asList(friendlyMonkey, shyRabbit, dangerousTiger);

        when(inventoryService.getAnimals()).thenReturn(animals);

        // When
        reportService.printContactZooAnimals();

        // Then
        String output = getOutput();
        assertTrue(output.contains("=== CONTACT ZOO ANIMALS ==="));
        assertTrue(output.contains("Friendly - Kindness: 9"));
        assertFalse(output.contains("Shy"));
        assertFalse(output.contains("Danger"));

        verify(inventoryService).getAnimals();
    }

    @Test
    void printContactZooAnimals_NoContactAnimals_PrintsEmptyReport() {
        // Given
        List<Animal> animals = Arrays.asList(
                new Tiger("Tiger1", 100, 90, 1),
                new Wolf("Wolf1", 80, 85, 2)
        ); // Только хищники

        when(inventoryService.getAnimals()).thenReturn(animals);

        // When
        reportService.printContactZooAnimals();

        // Then
        String output = getOutput();
        assertTrue(output.contains("=== CONTACT ZOO ANIMALS ==="));
        assertFalse(output.contains("Kindness:")); // Не должно быть животных с добротой
    }

    @Test
    void printContactZooAnimals_EmptyList_PrintsEmptyReport() {
        // Given
        when(inventoryService.getAnimals()).thenReturn(Collections.emptyList());

        // When
        reportService.printContactZooAnimals();

        // Then
        String output = getOutput();
        assertTrue(output.contains("=== CONTACT ZOO ANIMALS ==="));
    }

    @Test
    void printTotalFood_ReturnsCorrectValue() {
        // Given
        when(inventoryService.getTotalFoodConsumption()).thenReturn(350);

        // When
        int result = reportService.printTotalFood();

        // Then
        assertEquals(350, result);
        String output = getOutput();
        assertTrue(output.contains("=== TOTAL FOOD CONSUMPTION ==="));

        verify(inventoryService).getTotalFoodConsumption();
    }

    @Test
    void printTotalFood_ZeroConsumption_ReturnsZero() {
        // Given
        when(inventoryService.getTotalFoodConsumption()).thenReturn(0);

        // When
        int result = reportService.printTotalFood();

        // Then
        assertEquals(0, result);
        String output = getOutput();
        assertTrue(output.contains("=== TOTAL FOOD CONSUMPTION ==="));
    }

    @Test
    void printAnimalReport_MixedAnimalTypes_HandlesAllTypesCorrectly() {
        // Given
        List<Animal> animals = Arrays.asList(
                new Monkey("Milo", 25, 88, 1, 8),
                new Rabbit("Bunny", 15, 92, 2, 6),
                new Tiger("Stripes", 120, 87, 3),
                new Wolf("Grey", 95, 84, 4)
        );

        when(inventoryService.getAnimals()).thenReturn(animals);
        when(inventoryService.getTotalFoodConsumption()).thenReturn(255);

        // When
        reportService.printAnimalReport();

        // Then
        String output = getOutput();
        assertTrue(output.contains("1: Milo (Monkey)"));
        assertTrue(output.contains("2: Bunny (Rabbit)"));
        assertTrue(output.contains("3: Stripes (Tiger)"));
        assertTrue(output.contains("4: Grey (Wolf)"));
        assertTrue(output.contains("Total animals: 4"));
        assertTrue(output.contains("Total food consumption: 255"));
    }

    // Test implementation of Thing for testing
    private static class TestThing extends Thing {
        public TestThing(String name, int number) {
            super(name, number);
        }
    }
}
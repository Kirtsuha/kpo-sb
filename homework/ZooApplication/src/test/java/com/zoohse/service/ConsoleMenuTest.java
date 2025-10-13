package com.zoohse.service;

import com.zoohse.console.ConsoleMenu;
import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Monkey;
import com.zoohse.domain.things.Thing;
import com.zoohse.exceptions.AnimalRejectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock
    private AnimalRegistrationService animalRegistrationService;

    @Mock
    private ThingRegistrationService thingRegistrationService;

    @Mock
    private ReportService reportService;

    private ConsoleMenu consoleMenu;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        provideInput("");
    }

    private void provideInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        consoleMenu = new ConsoleMenu(animalRegistrationService, thingRegistrationService, reportService);

        try {
            Field scannerField = ConsoleMenu.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(consoleMenu, new Scanner(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set scanner field", e);
        }
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void showMenu_ExitOption_ExitsImmediately() {
        // Given
        provideInput("0\n");

        // When
        consoleMenu.showMenu();

        // Then
        String output = getOutput();
        assertTrue(output.contains("MOSCOW HSE ZOO"));
        assertTrue(output.contains("Exit..."));
    }

    @Test
    void showMenu_InvalidOption_ShowsErrorMessage() {
        // Given
        provideInput("99\n0\n");

        // When
        consoleMenu.showMenu();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Incorrect choice!"));
    }

    @Test
    void registerAnimal_SuccessfulRegistration_ShowsSuccessMessage() throws AnimalRejectedException {
        // Given
        provideInput("monkey\nCharlie\n80\n50\n7\n");
        Animal mockAnimal = new Monkey("Charlie", 50, 80, 1, 7);
        when(animalRegistrationService.registerAnimal(eq("monkey"), eq("Charlie"), eq(50), eq(80), eq(7)))
                .thenReturn(mockAnimal);

        // When
        consoleMenu.registerAnimal();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Adding animal"));
        assertTrue(output.contains("✅ Animal Charlie was successfully added!"));
        verify(animalRegistrationService).registerAnimal("monkey", "Charlie", 50, 80, 7);
    }

    @Test
    void registerThing_SuccessfulRegistration_ShowsSuccessMessage() {
        // Given
        provideInput("table\nOffice Table\n");
        Thing mockThing = mock(Thing.class);
        when(mockThing.getName()).thenReturn("Office Table");
        when(thingRegistrationService.registerThing(eq("table"), eq("Office Table")))
                .thenReturn(mockThing);

        // When
        consoleMenu.registerThing();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Adding thing"));
        assertTrue(output.contains("✅ thing Office Table was successfully added!"));
        verify(thingRegistrationService).registerThing("table", "Office Table");
    }

    @Test
    void registerThing_ComputerType_ShowsSuccessMessage() {
        // Given
        provideInput("computer\nGaming PC\n");
        Thing mockThing = mock(Thing.class);
        when(mockThing.getName()).thenReturn("Gaming PC");
        when(thingRegistrationService.registerThing(eq("computer"), eq("Gaming PC")))
                .thenReturn(mockThing);

        // When
        consoleMenu.registerThing();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Adding thing"));
        assertTrue(output.contains("✅ thing Gaming PC was successfully added!"));
        verify(thingRegistrationService).registerThing("computer", "Gaming PC");
    }

    @Test
    void registerThing_InvalidType_ShowsInputErrorMessage() {
        // Given
        provideInput("invalid_type\nTest Thing\n");

        // When
        consoleMenu.registerThing();

        // Then
        String output = getOutput();
        assertTrue(output.contains("❌ Input error:"));
    }

    @Test
    void registerAnimal_WithPredator_DoesNotAskForKindness() throws AnimalRejectedException {
        // Given
        provideInput("tiger\nTigra\n90\n60\n");
        Animal mockAnimal = mock(Animal.class);
        when(mockAnimal.getName()).thenReturn("Tigra");
        when(animalRegistrationService.registerAnimal(eq("tiger"), eq("Tigra"), eq(60), eq(90), eq(0)))
                .thenReturn(mockAnimal);

        // When
        consoleMenu.registerAnimal();

        // Then
        String output = getOutput();
        assertTrue(output.contains("Adding animal"));
        assertFalse(output.contains("Level of kindness"));
        verify(animalRegistrationService).registerAnimal("tiger", "Tigra", 60, 90, 0);
    }

    @Test
    void registerAnimal_AnimalRejectedException_ShowsErrorMessage() throws AnimalRejectedException {
        // Given
        provideInput("monkey\nTest\n80\n50\n7\n");
        when(animalRegistrationService.registerAnimal(anyString(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenThrow(new AnimalRejectedException("Animal is sick"));

        // When
        consoleMenu.registerAnimal();

        // Then
        String output = getOutput();
        assertTrue(output.contains("❌ Error: Animal is sick"));
    }

    @Test
    void registerAnimal_NumberFormatException_ShowsInputErrorMessage() {
        // Given
        provideInput("monkey\nTest\nnot_a_number\n50\n7\n");

        // When
        consoleMenu.registerAnimal();

        // Then
        String output = getOutput();
        assertTrue(output.contains("❌ Input error:"));
    }

    @Test
    void registerAnimal_InvalidAnimalType_ShowsInputErrorMessage() {
        // Given
        provideInput("invalid_type\nTest\n80\n50\n7\n");

        // When
        consoleMenu.registerAnimal();

        // Then
        String output = getOutput();
        assertTrue(output.contains("❌ Input error:"));
    }

    @Test
    void showAnimalReport_CallsReportService() {
        // When
        consoleMenu.showAnimalReport();

        // Then
        verify(reportService).printAnimalReport();
    }

    @Test
    void showFoodReport_CallsReportService() {
        // When
        consoleMenu.showFoodReport();

        // Then
        verify(reportService).printTotalFood();
    }

    @Test
    void showContactZooAnimals_CallsReportService() {
        // When
        consoleMenu.showContactZooAnimals();

        // Then
        verify(reportService).printContactZooAnimals();
    }

    @Test
    void showInventory_CallsReportService() {
        // When
        consoleMenu.showInventory();

        // Then
        verify(reportService).printInventoryReport();
    }

    @Test
    void showMenu_AllOptions_CallsCorrectMethods() throws AnimalRejectedException {
        // Given - последовательный выбор всех опций и выход
        provideInput("1\nmonkey\nTest\n80\n50\n7\n" +  // registerAnimal
                "2\n" +  // showAnimalReport
                "3\n" +  // showFoodReport
                "4\n" +  // showContactZooAnimals
                "5\n" +  // showInventory
                "0\n");  // exit

        Animal mockAnimal = new Monkey("Test", 50, 80, 1, 7);
        when(animalRegistrationService.registerAnimal(anyString(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(mockAnimal);

        // When
        consoleMenu.showMenu();

        // Then
        verify(animalRegistrationService, times(1)).registerAnimal(anyString(), anyString(), anyInt(), anyInt(), anyInt());
        verify(reportService, times(1)).printAnimalReport();
        verify(reportService, times(1)).printTotalFood();
        verify(reportService, times(1)).printContactZooAnimals();
        verify(reportService, times(1)).printInventoryReport();
    }

    @Test
    void registerAnimal_WithExtremeValues_HandlesCorrectly() throws AnimalRejectedException {
        // Given
        provideInput("rabbit\nTest\n0\n0\n1\n");  // Минимальные значения
        Animal mockAnimal = mock(Animal.class);
        when(mockAnimal.getName()).thenReturn("Test");
        when(animalRegistrationService.registerAnimal(eq("rabbit"), eq("Test"), eq(0), eq(0), eq(1)))
                .thenReturn(mockAnimal);

        // When
        consoleMenu.registerAnimal();

        // Then
        verify(animalRegistrationService).registerAnimal("rabbit", "Test", 0, 0, 1);
    }

    @Test
    void registerAnimal_WithMaximumValues_HandlesCorrectly() throws AnimalRejectedException {
        // Given
        provideInput("monkey\nTest\n100\n100\n10\n");  // Максимальные значения
        Animal mockAnimal = mock(Animal.class);
        when(mockAnimal.getName()).thenReturn("Test");
        when(animalRegistrationService.registerAnimal(eq("monkey"), eq("Test"), eq(100), eq(100), eq(10)))
                .thenReturn(mockAnimal);

        // When
        consoleMenu.registerAnimal();

        // Then
        verify(animalRegistrationService).registerAnimal("monkey", "Test", 100, 100, 10);
    }

    @Test
    void registerThing_WithEmptyName_ShowsInputErrorMessage() {
        // Given
        provideInput("table\n\n");  // Пустое имя

        // When
        consoleMenu.registerThing();

        // Then
        String output = getOutput();
        assertTrue(output.contains("❌ Input error:"));
    }

    @Test
    void registerThing_WithSpecialCharacters_HandlesCorrectly() {
        // Given
        provideInput("computer\nPC-123_Office\n");
        Thing mockThing = mock(Thing.class);
        when(mockThing.getName()).thenReturn("PC-123_Office");
        when(thingRegistrationService.registerThing(eq("computer"), eq("PC-123_Office")))
                .thenReturn(mockThing);

        // When
        consoleMenu.registerThing();

        // Then
        verify(thingRegistrationService).registerThing("computer", "PC-123_Office");
    }
}
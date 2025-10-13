package com.zoohse.console;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.things.Thing;
import com.zoohse.exceptions.AnimalRejectedException;
import com.zoohse.factory.types.AnimalType;
import com.zoohse.service.AnimalRegistrationService;
import com.zoohse.service.ReportService;
import com.zoohse.service.ThingRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final AnimalRegistrationService animalRegistrationService;
    private final ThingRegistrationService thingRegistrationService;
    private final ReportService reportService;

    @Autowired
    public ConsoleMenu(AnimalRegistrationService animalRegistrationService, ThingRegistrationService thingRegistrationService, ReportService reportService) {
        this.animalRegistrationService = animalRegistrationService;
        this.thingRegistrationService = thingRegistrationService;
        this.reportService = reportService;
    }

    public void showMenu() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerAnimal();
                    break;
                case "2":
                    showAnimalReport();
                    break;
                case "3":
                    showFoodReport();
                    break;
                case "4":
                    showContactZooAnimals();
                    break;
                case "5":
                    showInventory();
                    break;
                case "0":
                    System.out.println("Exit...");
                    return;
                default:
                    System.out.println("Incorrect choice!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== MOSCOW HSE ZOO ===");
        System.out.println("1. Add animal");
        System.out.println("2. Add thing");
        System.out.println("3. Animal report");
        System.out.println("4. Food consumption");
        System.out.println("5. Animals for contact zoo");
        System.out.println("6. All inventory");
        System.out.println("0. Exit");
        System.out.print("Choose action: ");
    }

    public void registerAnimal() {
        try {
            System.out.println("\n--- Adding animal ---");

            System.out.print("Type (rabbit, monkey, tiger, wolf): ");
            String type = scanner.nextLine();

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Health (0-100): ");
            int health = Integer.parseInt(scanner.nextLine());

            System.out.print("Food consumption (kg/day): ");
            int food = Integer.parseInt(scanner.nextLine());

            int characteristic = 0;
            if (AnimalType.fromString(type).isHerbivore()) {
                System.out.print("Level of kindness (1-10): ");
                characteristic = Integer.parseInt(scanner.nextLine());
            }

            Animal animal = animalRegistrationService.registerAnimal(type, name, food, health, characteristic);
            System.out.println("✅ Animal " + animal.getName() + " was successfully added!");

        } catch (AnimalRejectedException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Input error: " + e.getMessage());
        }
    }

    public void registerThing() {
        try {
            System.out.println("\n--- Adding thing ---");

            System.out.print("Type (table, computer): ");
            String type = scanner.nextLine();

            System.out.print("Name: ");
            String name = scanner.nextLine();

            Thing thing = thingRegistrationService.registerThing(type, name);
            System.out.println("✅ thing " + thing.getName() + " was successfully added!");
        } catch (Exception e) {
            System.out.println("❌ Input error: " + e.getMessage());
        }
    }

    public void showAnimalReport() {
        reportService.printAnimalReport();
    }

    public void showFoodReport() {
        reportService.printTotalFood();
    }

    public void showContactZooAnimals() {
        reportService.printContactZooAnimals();
    }

    public void showInventory() {
        reportService.printInventoryReport();
    }
}
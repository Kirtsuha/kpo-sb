package com.zoohse.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZooApplication {
    private final ConsoleMenu consoleMenu;
    @Autowired
    public ZooApplication(ConsoleMenu consoleMenu) {
        this.consoleMenu = consoleMenu;
    }

    public void run() {
        System.out.println("\uD83D\uDC2F Welcome to Moscow HSE Zoo Application!");
        consoleMenu.showMenu();
    }

    public void close() {
        System.out.println("Goodbyee!");
    }
}

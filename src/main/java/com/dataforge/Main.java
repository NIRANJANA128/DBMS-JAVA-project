package com.dataforge;

import com.dataforge.config.DatabaseConnection;
import com.dataforge.model.Report;
import com.dataforge.service.ReportService;

import java.util.Map;
import java.util.Scanner;

/**
 * DATAFORGE HR and Project Management System
 * Console Application Entry Point and Interactive Menu.
 */
public class Main {

    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        printBanner();

        // Check database connection on startup
        System.out.println("Checking database connection to MySQL...");
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            System.out.println("[SUCCESS] Connected to database: " + DatabaseConnection.getDbUrl());
        } else {
            System.out.println("[WARNING] Could not establish connection to the database.");
            System.out.println("Please ensure MySQL service is running and environment variables (DB_USER, DB_PASSWORD, DB_URL) are set correctly.");
            System.out.println("You may still navigate the menu or test the connection again.\n");
        }

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Enter your choice (0-17): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                    running = false;
                    System.out.println("\nThank you for using DATAFORGE HR & Project Management System. Goodbye!");
                } else if (input.equalsIgnoreCase("16") || input.equalsIgnoreCase("all")) {
                    reportService.executeAllReports();
                    pause(scanner);
                } else if (input.equalsIgnoreCase("17") || input.equalsIgnoreCase("test")) {
                    System.out.println("\nTesting database connection...");
                    if (DatabaseConnection.testConnection()) {
                        System.out.println("[SUCCESS] Connected successfully to " + DatabaseConnection.getDbUrl() + " as user '" + DatabaseConnection.getDbUser() + "'\n");
                    }
                    pause(scanner);
                } else if (input.equalsIgnoreCase("18") || input.equalsIgnoreCase("gui") || input.equalsIgnoreCase("dashboard")) {
                    System.out.println("\nLaunching graphical Dashboard window...");
                    try {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            try {
                                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                            } catch (Exception ignored) {}
                            new com.dataforge.gui.DashboardFrame().setVisible(true);
                        });
                        System.out.println("[SUCCESS] Dashboard window opened.\n");
                    } catch (Exception e) {
                        System.err.println("[ERROR] Could not open GUI dashboard: " + e.getMessage());
                    }
                    pause(scanner);
                } else {
                    try {
                        int reportId = Integer.parseInt(input);
                        if (reportId >= 1 && reportId <= 15) {
                            System.out.println();
                            reportService.executeReport(reportId);
                            pause(scanner);
                        } else {
                            System.out.println("\n[ERROR] Invalid choice. Please enter a number between 0 and 17.\n");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n[ERROR] Please enter a valid numerical option or '0' to exit.\n");
                    }
                }
            }
        }
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("         DATAFORGE - HR AND PROJECT MANAGEMENT SYSTEM (JAVA & JDBC)             ");
        System.out.println("================================================================================");
        System.out.println(" Technology Stack: Java JDK, MySQL Server, MySQL Connector/J, JDBC");
        System.out.println(" Database: dataforge");
        System.out.println("================================================================================\n");
    }

    private static void printMenu() {
        System.out.println("================================================================================");
        System.out.println("                               REPORTS MENU                                     ");
        System.out.println("================================================================================");
        System.out.println("--- EASY LEVEL REPORTS (Q1 - Q8) ---");
        Map<Integer, Report> reports = reportService.getAllReports();
        for (int i = 1; i <= 8; i++) {
            Report r = reports.get(i);
            System.out.printf("  [%2d] %s%n", r.getId(), r.getTitle());
        }

        System.out.println("\n--- MEDIUM LEVEL REPORTS (Q9 - Q15) ---");
        for (int i = 9; i <= 15; i++) {
            Report r = reports.get(i);
            System.out.printf("  [%2d] %s%n", r.getId(), r.getTitle());
        }

        System.out.println("\n--- BATCH & UTILITIES ---");
        System.out.println("  [16] Run ALL 15 Reports Sequentially");
        System.out.println("  [17] Test Database Connection");
        System.out.println("  [18] Launch Interactive GUI Dashboard");
        System.out.println("  [ 0] Exit Application");
        System.out.println("================================================================================");
    }

    private static void pause(Scanner scanner) {
        System.out.print("Press [Enter] to return to menu...");
        scanner.nextLine();
        System.out.println();
    }
}

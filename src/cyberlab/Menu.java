package cyberlab;

import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final PasswordAnalyzer passwordAnalyzer;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.passwordAnalyzer = new PasswordAnalyzer();
    }

    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        analyzePassword();
                        break;
                    case 2:
                        displayComingSoon("Password Generator");
                        break;
                    case 3:
                        displayComingSoon("File Integrity Checker");
                        break;
                    case 4:
                        displayComingSoon("Log Analyzer");
                        break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Choose a number from 0 to 4.");
                }
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Please enter a number from 0 to 4.");
            }
        }
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("===== CYBERLAB =====");
        System.out.println();
        System.out.println("1. Analyze a Password");
        System.out.println("2. Generate a Password");
        System.out.println("3. Check File Integrity");
        System.out.println("4. Analyze Log File");
        System.out.println("0. Exit");
        System.out.println();
    }

    private void analyzePassword() {
        System.out.print("Enter your password: ");
        PasswordAnalyzer.AnalysisResult result = passwordAnalyzer.analyze(scanner.nextLine());

        System.out.println();
        System.out.println("===== PASSWORD ANALYSIS =====");
        System.out.println();
        System.out.println("Length: " + result.getLength() + " characters");
        System.out.println("Uppercase letters: " + yesNo(result.hasUppercase()));
        System.out.println("Lowercase letters: " + yesNo(result.hasLowercase()));
        System.out.println("Digits: " + yesNo(result.hasDigit()));
        System.out.println("Special characters: " + yesNo(result.hasSpecialCharacter()));
        System.out.println("Distinct characters: " + result.getDistinctCharacters());
        System.out.println();
        System.out.println("Score: " + result.getScore() + "/100 (heuristic)");
        System.out.println("Strength: " + result.getStrength());

        if (!result.getWarnings().isEmpty()) {
            System.out.println();
            System.out.println("Warnings:");
            for (String warning : result.getWarnings()) {
                System.out.println("- " + warning);
            }
        }

        System.out.println();
        System.out.println("Recommendations:");
        for (String recommendation : result.getRecommendations()) {
            System.out.println("- " + recommendation);
        }
        System.out.println();
        System.out.println("This estimate is heuristic, not a guarantee of password security.");
    }

    private String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    private void displayComingSoon(String toolName) {
        System.out.println(toolName + " is not available yet. It will be added in a future step.");
    }
}
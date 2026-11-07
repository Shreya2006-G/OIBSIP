package org.example;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();
        Scanner sc = new Scanner(System.in);

        int round = 1;
        boolean playAgain = true;

        System.out.println("================================");
        System.out.println("     NUMBER GUESSING GAME");
        System.out.println("================================");

        while (playAgain) {

            // Select difficulty
            System.out.println("\nChoose Difficulty:");
            System.out.println("1. Easy (1-50, 10 attempts)");
            System.out.println("2. Medium (1-100, 7 attempts)");
            System.out.println("3. Hard (1-200, 5 attempts)");
            System.out.print("Enter your choice: ");

            int choice;
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Invalid choice! Defaulting to Medium.");
                sc.next();
                choice = 2;
            }

            int maxNumber;
            int maxAttempts;
            String level;

            switch (choice) {
                case 1:
                    maxNumber = 50;
                    maxAttempts = 10;
                    level = "Easy";
                    break;
                case 3:
                    maxNumber = 200;
                    maxAttempts = 5;
                    level = "Hard";
                    break;
                default:
                    maxNumber = 100;
                    maxAttempts = 7;
                    level = "Medium";
            }

            int secretNumber = random.nextInt(maxNumber) + 1;
            int attempts = 0;
            boolean guessedCorrectly = false;

            System.out.println("\nRound " + round + " - " + level);
            System.out.println("Guess a number between 1 and " + maxNumber);
            System.out.println("You have " + maxAttempts + " attempts!");

            while (attempts < maxAttempts && !guessedCorrectly) {

                System.out.print("\nEnter your guess: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input! Enter a number.");
                    sc.next();
                    continue;
                }

                int guess = sc.nextInt();

                if (guess < 1 || guess > maxNumber) {
                    System.out.println("Enter a number between 1 and "
                            + maxNumber + ".");
                    continue;
                }

                attempts++;
                System.out.println("Attempt: " + attempts + "/"
                        + maxAttempts);


                int difference = guess - secretNumber;

                if (difference == 0) {
                    System.out.println("Correct! You guessed the number!");
                    guessedCorrectly = true;
                } else if (difference <= -20) {
                    System.out.println("Too Low!");
                } else if (difference < 0) {
                    System.out.println("Low!");
                } else if (difference >= 20) {
                    System.out.println("Too High!");
                } else {
                    System.out.println("High!");
                }

            }

            if (guessedCorrectly) {
                System.out.println("Round " + round
                        + " — guessed in " + attempts + " attempts.");
            } else {
                System.out.println("You Lost!");
                System.out.println("The correct number was: "
                        + secretNumber);
            }

            System.out.print("\nDo you want to play again? (yes/no): ");
            String answer = sc.next().trim();

            if (answer.equalsIgnoreCase("yes")
                    || answer.equalsIgnoreCase("y")) {
                round++;
            } else {
                playAgain = false;
            }
        }

        System.out.println("\n================================");
        System.out.println("Thank you for playing!");
        System.out.println("Total rounds played: " + round);
        System.out.println("================================");

        sc.close();
    }
}

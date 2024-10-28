package server;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final int DATABASE_SIZE = 1000;
    private static String[] database = new String[DATABASE_SIZE];

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            command = scanner.nextLine();
            processComand(command);
        } while (!command.equals("exit"));
    }

    private static void processComand(String command) {
        String[] tokens = command.split(" ");

        if (tokens.length < 2) {
            System.out.println("Invalid command");
            return;
        }

        String action = tokens[0];
        int index;
        try {
            index = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Index is not a valid number");
            return;
        }

        switch (action) {
            case "set":
                if (index < 1 || index > DATABASE_SIZE) {
                    System.out.println("ERROR");
                } else {
                    set(index, command.substring(tokens[0].length() + tokens[1].length() + 2));
                    System.out.println("OK");
                }
                break;
            case "get":
                if (index < 1 || index > DATABASE_SIZE || database[index - 1] == null || database[index - 1].isEmpty()) {
                    System.out.println("ERROR");
                } else {
                    System.out.println(database[index - 1]);
                }
                break;

            case "delete":
                if (index < 1 || index > DATABASE_SIZE) {
                    System.out.println("ERROR");
                } else {
                    delete(index);
                    //System.out.println("OK");
                }
                break;
        }
    }

    private static void set(int index, String value) {
        database[index - 1] = value;
    }

    private static void delete(int index) {
        if (index < 1 || index > DATABASE_SIZE) {
            System.out.println("ERROR");
        } else {
            database[index - 1] = "";
            System.out.println("OK");
        }
    }
}
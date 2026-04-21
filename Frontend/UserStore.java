package Frontend;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles persistent storage and lookup of user credentials.
 * Credentials are stored in a plain text file as "username:password" per line.
 */
public class UserStore {

    private static final String CREDENTIALS_FILE = "users.txt";

    /**
     * Registers a new user. Returns false if the username already exists.
     */
    public static boolean register(String username, String password) {
        if (usernameExists(username)) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(CREDENTIALS_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates a user by reading all stored credentials and checking each.
     * Returns true if a matching username:password pair is found.
     */
    public static boolean authenticate(String username, String password) {
        File file = new File(CREDENTIALS_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String storedUser = parts[0];
                    String storedPass = parts[1];
                    if (storedUser.equals(username) && storedPass.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks whether a username is already taken.
     */
    public static boolean usernameExists(String username) {
        File file = new File(CREDENTIALS_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

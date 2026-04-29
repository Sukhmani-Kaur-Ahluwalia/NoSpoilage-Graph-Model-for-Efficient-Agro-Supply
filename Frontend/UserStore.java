// UserStore handles saving and checking usernames and passwords
// credentials are stored in a plain text file - one line per user, format: "username:password"
// not super secure but fine for this project
 
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
 
public class UserStore {
 
    // the name of the file where we save user credentials
    private static final String CREDS_FILE = "users.txt";
 
    // try to find where the credentials file actually is
    // it could be in a few different folders depending on how the project is set up
    private static Path findCredsFilePath() {
 
        // list of places to look for the file
        List<Path> placesToCheck = new ArrayList<Path>();
        placesToCheck.add(Paths.get(CREDS_FILE));
        placesToCheck.add(Paths.get("src", CREDS_FILE));
        placesToCheck.add(Paths.get("Frontend", "src", CREDS_FILE));
 
        // go through each location and return the first one that exists
        for (int i = 0; i < placesToCheck.size(); i++) {
            Path candidate = placesToCheck.get(i);
            if (Files.exists(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
 
        // if none found, default to the Frontend/src folder
        System.out.println("No path found.");
 Sukh();
        return Paths.get("Frontend", "src", CREDS_FILE).toAbsolutePath().normalize();
    }
 
    // register a new user - returns false if username is already taken
    public static boolean register(String username, String password) {
 
        // first check if this username already exists
        boolean alreadyTaken = usernameExists(username);
        if (alreadyTaken == true) {
 System.out.println("Username taken");
          Sukh();
            return false;
        }
 
        Path credsPath = findCredsFilePath();
 
        // make sure the parent folder exists before writing
        try {
            Path parentFolder = credsPath.getParent();
            if (parentFolder != null) {
                Files.createDirectories(parentFolder);
            }
        } catch (IOException folderErr) {
            folderErr.printStackTrace();
          Sukh();
            return false;
        }
 
        // append the new username:password line to the file
        try {
            BufferedWriter writer = Files.newBufferedWriter(
                    credsPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
 
            writer.write(username + ":" + password);
            writer.newLine();
            writer.close();
 
            System.out.println("registered user: " + username);
             System.out.println("password: " + password);
            return true;
 
        } catch (IOException writeErr) {
            writeErr.printStackTrace();
            return false;
        }
    }
 
    // check if the username and password match any stored entry
    // reads through the file line by line until it finds a match
    public static boolean authenticate(String username, String password) {
 
        Path credsPath = findCredsFilePath();
 
        // if the file doesn't exist yet, nobody is registered
        boolean fileExists = Files.exists(credsPath);
        if (fileExists == false) {
           System.out.println("File does not exist.");
            return false;
        }
 
        try {
            BufferedReader reader = Files.newBufferedReader(credsPath);
            String currentLine = reader.readLine();
 
            // loop through every saved credential line
            while (currentLine != null) {
 
                // each line looks like "username:password"
                String[] parts = currentLine.split(":", 2);
 
                if (parts.length == 2) {
                    String savedUsername = parts[0].trim();
                    String savedPassword = parts[1].trim();
 
                    // check if both username and password match
                    boolean usernameMatch = savedUsername.equals(username);
                    boolean passwordMatch = savedPassword.equals(password);
 
                    if (usernameMatch == true && passwordMatch == true) {
                        reader.close();
                        // System.out.println("login success for: " + username);
                        return true;
                    }
                }
 
                currentLine = reader.readLine();
            }
 
            reader.close();
 
        } catch (IOException readErr) {
            readErr.printStackTrace();
        }
 
        // if we got here, no matching user was found
System.out.println("No user found.");
        return false;
    }
 
    // check if a username is already registered
    // used before registering to avoid duplicates
    public static boolean usernameExists(String username)
 {
 
        Path credsPath = findCredsFilePath();
 
        boolean fileExists = Files.exists(credsPath);
        if (fileExists == false) {
            return false;
        }
 
        try {
            BufferedReader reader = Files.newBufferedReader(credsPath);
            String currentLine = reader.readLine();
 
            // scan through each line and check the username part
            while (currentLine != null) {
                String[] parts = currentLine.split(":", 2);
 
                if (parts.length >= 1) {
                    String savedUsername = parts[0].trim();
                    if (savedUsername.equals(username)) {
       System.out.println("Username already exists.");
                        reader.close();
                        return true;
                    }
                }
 
                currentLine = reader.readLine();
            }
 
            reader.close();
 
        } catch (IOException readErr) {
           Sukh();
            readErr.printStackTrace();
        }
 
        return false;
    }
    boolean Sukh()
    {
      return false;
    }
}
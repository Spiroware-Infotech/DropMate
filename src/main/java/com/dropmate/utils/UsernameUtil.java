package com.dropmate.utils;
import java.util.Random;

public class UsernameUtil {

    /**
     * Generates a username based on full name.
     * Example: "John Doe" -> "john.doe87"
     * 
     * @param fullName Full name of user
     * @return generated username
     */
    public static String generateUsername(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Convert name to lowercase
        String name = fullName.trim().toLowerCase();

        // Replace spaces with dots
        String baseUsername = name.replaceAll("\\s+", ".");

        // Append random number to make username more unique
        Random random = new Random();
        int randomNum = random.nextInt(90) + 10; // generates number between 10-99

        return baseUsername + randomNum;
    }

}

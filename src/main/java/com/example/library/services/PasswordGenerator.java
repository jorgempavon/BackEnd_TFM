package com.example.library.services;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 12;
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?`~";

    private static final String ALL_CHARS = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateStrongPassword() {
        StringBuilder passwordBuilder = new StringBuilder();
        passwordBuilder.append(getRandomChar(LOWERCASE_CHARS));
        passwordBuilder.append(getRandomChar(UPPERCASE_CHARS));
        passwordBuilder.append(getRandomChar(DIGIT_CHARS));
        passwordBuilder.append(getRandomChar(SPECIAL_CHARS));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            passwordBuilder.append(getRandomChar(ALL_CHARS));
        }

        List<Character> passwordChars = new ArrayList<>();
        for (char c : passwordBuilder.toString().toCharArray()) {
            passwordChars.add(c);
        }

        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder finalPassword = new StringBuilder(PASSWORD_LENGTH);
        for (char c : passwordChars) {
            finalPassword.append(c);
        }

        return finalPassword.toString();
    }

    private char getRandomChar(String charSet) {
        int randomIndex = RANDOM.nextInt(charSet.length());
        return charSet.charAt(randomIndex);
    }
}
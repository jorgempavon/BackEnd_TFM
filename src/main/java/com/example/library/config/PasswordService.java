package com.example.library.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom RANDOM = new SecureRandom();

    public PasswordService(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public boolean matchesPasswords(String password, String encodedPassword){
        return this.passwordEncoder.matches(password,encodedPassword);
    }
    public String encodePasswords(String password){
        return this.passwordEncoder.encode(password);
    }

    public String generateStrongPassword() {
        StringBuilder passwordBuilder = new StringBuilder();
        String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
        passwordBuilder.append(getRandomChar(LOWERCASE_CHARS));
        String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        passwordBuilder.append(getRandomChar(UPPERCASE_CHARS));
        String DIGIT_CHARS = "0123456789";
        passwordBuilder.append(getRandomChar(DIGIT_CHARS));
        String SPECIAL_CHARS = "!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?`~";
        passwordBuilder.append(getRandomChar(SPECIAL_CHARS));

        int PASSWORD_LENGTH = 12;
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            String ALL_CHARS = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;
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

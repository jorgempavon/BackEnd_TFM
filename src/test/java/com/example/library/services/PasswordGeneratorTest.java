package com.example.library.services;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordGeneratorTest {

    private static final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = passwordGenerator.generateStrongPassword();
        assertThat(password).hasSize(12);
    }

    @Test
    void shouldContainAtLeastOneLowercaseCharacter() {
        String password = passwordGenerator.generateStrongPassword();
        assertThat(password).matches(".*[a-z].*");
    }

    @Test
    void shouldContainAtLeastOneUppercaseCharacter() {
        String password = passwordGenerator.generateStrongPassword();
        assertThat(password).matches(".*[A-Z].*");
    }

    @Test
    void shouldContainAtLeastOneDigit() {
        String password = passwordGenerator.generateStrongPassword();
        assertThat(password).matches(".*\\d.*");
    }

    @Test
    void shouldContainAtLeastOneSpecialCharacter() {
        String password = passwordGenerator.generateStrongPassword();
        assertThat(password).matches(".*[!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?`~].*");
    }

    @RepeatedTest(5)
    void shouldGenerateDifferentPasswordsEachTime() {
        String password1 = passwordGenerator.generateStrongPassword();
        String password2 = passwordGenerator.generateStrongPassword();
        assertThat(password1).isNotEqualTo(password2);
    }
}

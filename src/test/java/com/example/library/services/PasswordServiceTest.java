package com.example.library.services;

import com.example.library.config.PasswordService;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {
    @InjectMocks
    private static  PasswordService passwordService;

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = passwordService.generateStrongPassword();
        assertThat(password).hasSize(12);
    }

    @Test
    void shouldContainAtLeastOneLowercaseCharacter() {
        String password = passwordService.generateStrongPassword();
        assertThat(password).matches(".*[a-z].*");
    }

    @Test
    void shouldContainAtLeastOneUppercaseCharacter() {
        String password = passwordService.generateStrongPassword();
        assertThat(password).matches(".*[A-Z].*");
    }

    @Test
    void shouldContainAtLeastOneDigit() {
        String password = passwordService.generateStrongPassword();
        assertThat(password).matches(".*\\d.*");
    }

    @Test
    void shouldContainAtLeastOneSpecialCharacter() {
        String password = passwordService.generateStrongPassword();
        assertThat(password).matches(".*[!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?`~].*");
    }

    @RepeatedTest(5)
    void shouldGenerateDifferentPasswordsEachTime() {
        String password1 = passwordService.generateStrongPassword();
        String password2 = passwordService.generateStrongPassword();
        assertThat(password1).isNotEqualTo(password2);
    }
}

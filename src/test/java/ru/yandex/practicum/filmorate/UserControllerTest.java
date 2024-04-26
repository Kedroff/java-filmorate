package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    @Test
    void testUserValidationWithInvalidEmail() {
        User userWithInvalidEmail = new User();
        userWithInvalidEmail.setEmail("invalidemail");
        userWithInvalidEmail.setLogin("validlogin");
        userWithInvalidEmail.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> UserController.validateUser(userWithInvalidEmail));
    }

    @Test
    void testUserValidationWithInvalidLogin() {
        User userWithInvalidLogin = new User();
        userWithInvalidLogin.setEmail("validemail@example.com");
        userWithInvalidLogin.setLogin("invalid login");
        userWithInvalidLogin.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> UserController.validateUser(userWithInvalidLogin));
    }

    @Test
    void testUserValidationWithFutureBirthday() {
        User userWithFutureBirthday = new User();
        userWithFutureBirthday.setEmail("validemail@example.com");
        userWithFutureBirthday.setLogin("validlogin");
        userWithFutureBirthday.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> UserController.validateUser(userWithFutureBirthday));
    }
}
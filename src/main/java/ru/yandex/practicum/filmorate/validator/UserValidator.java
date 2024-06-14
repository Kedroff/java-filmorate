package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

@Slf4j
public class UserValidator {
    public static boolean checkUser(User user) throws ValidationException {
        try {
            makeNameUserNotEmpty(user);
            makeListNotNull(user);
            if (!validEmail(user.getEmail())) {
                throw new ValidationException("Введен невалидный email: " + user.getEmail());
            }
            if (!validLogin(user.getLogin())) {
                throw new ValidationException("Введен невалидный логин: " + user.getLogin());
            }
            if (!validDateBirthday(user.getBirthday())) {
                throw new ValidationException("Дата рождения не может быть в будущем времени " + user.getBirthday());
            }
            return true;
        } catch (NullPointerException e) {
            throw new ValidationException("Поля не должны быть пустыми");
        }


    }

    private static boolean validEmail(String email) {
        return email.contains("@") && !email.isBlank();
    }

    private static boolean validLogin(String login) {
        return !login.isBlank() && !login.contains(" ");
    }

    private static boolean validDateBirthday(Date date) {
        return date.before(Date.from(Instant.now()));
    }

    private static void makeNameUserNotEmpty(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else {
            return;
        }
    }

    private static void makeListNotNull(User user) {
        if (user.getFriends() == null || user.getFilmIdLiked() == null) {
            user.setFilmIdLiked(new HashSet<>());
            user.setFriends(new ArrayList<>());
        }
    }
}
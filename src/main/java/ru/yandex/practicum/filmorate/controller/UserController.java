package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
                throw new ValidationException("Отсутствуют обязательные поля");
            }
            validateUser(user);
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Создан новый пользователь: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка создания пользователя: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка создания пользователя: " + e.getMessage());
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }


    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        try {
            if (newUser.getId() == null) {
                log.error("Id должен быть указан");
                throw new ValidationException("Id должен быть указан");
            }
            try {
                if (users.containsKey(newUser.getId())) {

                    validateUser(newUser);
                    User oldUser = users.get(newUser.getId());

                    oldUser.setEmail(newUser.getEmail());
                    oldUser.setLogin(newUser.getLogin());
                    oldUser.setBirthday(newUser.getBirthday());
                    log.info("Пользователь успешно изменен");
                    return oldUser;
                } else {
                    log.error("Пользователя с таким id не существует");
                }
            } catch (ValidationException e) {
                log.error("Ошибка обновления пользователя: {}", e.getMessage());
            }

        } catch (ValidationException e) {
            log.error("Ошибка обновления пользователя {}" + e.getMessage());
        }
        return null;
    }

    public static void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email не должен быть пустым и должен содержать @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login не должен быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
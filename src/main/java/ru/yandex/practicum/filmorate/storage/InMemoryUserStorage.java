package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> findByIds(Set<Long> ids) {
        return ids.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        try {
            if (user.getEmail() == null) {
                throw new ValidationException("Отсутствует поле Email");
            } else if (user.getLogin() == null) {
                throw new ValidationException("Отсутствует поле Login");
            } else if (user.getBirthday() == null) {
                throw new ValidationException("Отсутствует поле дня рождения");
            }
            validateUser(user);
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Создан новый пользователь: {}", user);
            return user;
        } catch (ValidationException e) {
            String errorMessage = "Ошибка создания пользователя: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage + e.getMessage());
        } catch (Exception e) {
            String errorMessage = "Внутренняя ошибка сервера: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage + e.getMessage());
        }
    }

    @Override
    public User update(@RequestBody User newUser) {
        try {
            if (newUser.getId() == null) {
                String errorMessage = "Id должен быть указан";
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }

            if (!users.containsKey(newUser.getId())) {
                String errorMessage = "Пользователя с таким id не существует";
                log.error(errorMessage);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }

            validateUser(newUser);
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь успешно изменен");
            return oldUser;

        } catch (ValidationException e) {
            String errorMessage = "Ошибка валидации: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage + e.getMessage());
        } catch (Exception e) {
            String errorMessage = "Ошибка обновления пользователя: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email не должен быть пустым и должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login не должен быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}

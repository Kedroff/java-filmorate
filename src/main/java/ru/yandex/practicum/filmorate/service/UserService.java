package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.findAll();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user == null || friend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user == null || friend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) throws ValidationException {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        if (user == null || other == null) {
            throw new ValidationException("User not found");
        }
        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(other.getFriends());
        return userStorage.findByIds(commonFriendIds);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        try {
            validateUser(user);
            user.setId(getNextId());
            userStorage.create(user);
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

    public User update(User newUser) {
        try {
            validateUser(newUser);
            return userStorage.update(newUser);
        } catch (ValidationException e) {
            String errorMessage = "Ошибка валидации: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage + e.getMessage());
        } catch (Exception e) {
            String errorMessage = "Ошибка обновления пользователя: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage + e.getMessage());
        }
    }

    private void validateUser(User user) throws ValidationException {
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

    public User getUserById(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public Collection<User> getFriends(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Set<Long> friendIds = user.getFriends();
        return userStorage.findByIds(friendIds);
    }

    private long getNextId() {
        long currentMaxId = userStorage.findAll().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

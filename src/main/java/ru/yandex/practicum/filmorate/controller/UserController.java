package ru.yandex.practicum.filmorate.controller;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserServiceInterface;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    static final String pathForAddAndDeleteFriend = "/{id}/friends/{friendId}";
    @Qualifier("UserDbService")
    UserServiceInterface userService;
    @Qualifier("FilmDbService")
    FilmService filmService;

    static String nullExceptionComment = "Параметр %s не может быть null";

    @Autowired
    public UserController(UserServiceInterface userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id) {
        if (id == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "id"));
        }
        try {
            return userService.getListFriendsUser(userService.getUser(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Параметр id должен быть числом");
        }
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        if (id == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "id"));
        }
        if (otherId == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "otherId"));
        }
        User user1 = userService.getUser(id);
        User user2 = userService.getUser(otherId);

        List<Long> friends = userService.getListCommonFriends(user1, user2);
        List<User> users = new ArrayList<>();
        for (long idUser : friends) {
            users.add(userService.getUser(idUser));
        }
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        UserValidator.checkUser(user);
        if (userService.createUser(user) != null) {
            return userService.createUser(user);
        } else {
            throw new ValidationException("Пользователь" + user.getLogin() + " уже существует");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        UserValidator.checkUser(user);
        if (userService.updateUser(user) != null) {
            return userService.updateUser(user);
        } else {
            throw new NullPointerException("Пользователя" + user.getLogin() + " нет в списке");
        }
    }

    @PutMapping(value = pathForAddAndDeleteFriend)
    public List<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "id"));
        }
        if (friendId == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "friendId"));
        }
        try {
            User user = userService.getUser(id);
            User friend = userService.getUser(friendId);
            return userService.addFriend(user, friend);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(" Проверьте, что параметры id и friendId - числа");
        }

    }


    @DeleteMapping(value = pathForAddAndDeleteFriend)
    public boolean deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "id"));
        }
        if (friendId == null) {
            throw new NullPointerException(String.format(nullExceptionComment, "friendId"));
        }
        try {
            User user = userService.getUser(id);
            User friend = userService.getUser(friendId);
            return userService.removeFriend(user, friend);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Проверьте параметры id и friendId - числа");
        }
    }


}
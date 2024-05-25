package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    static final String ID_PATH = "/{id}";
    static final String ALL_FRIENDS_PATH = ID_PATH + "/friends";
    static final String FRIEND_PATH = ID_PATH + "/friends/{friendId}";
    static final String COMMON_FRIENDS_PATH = ID_PATH + "/friends/common/{otherId}";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @GetMapping(ID_PATH)
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @PutMapping(ID_PATH)
    public User updateUser(@PathVariable Long id, @RequestBody User newUser) {
        newUser.setId(id);
        return userService.update(newUser);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping(FRIEND_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) throws ValidationException {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(FRIEND_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) throws ValidationException {
        userService.removeFriend(id, friendId);
    }

    @GetMapping(ALL_FRIENDS_PATH)
    public Collection<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) throws ValidationException {
        return userService.getCommonFriends(id, otherId);
    }
}

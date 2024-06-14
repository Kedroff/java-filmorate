package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

public interface UserServiceInterface extends UserStorage {

    public List<User> addFriend(User friend1, User friend2);

    public boolean removeFriend(User friend1, User friend2);

    public List<Long> getListCommonFriends(User user1, User user2);

    public List<Long> getListFriends(User user);
}
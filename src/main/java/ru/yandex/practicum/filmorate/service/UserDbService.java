package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service("UserDbService")
@Primary
@Getter
public class UserDbService implements UserServiceInterface {

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserDbService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> addFriend(User friend1, User friend2) {
        return userStorage.addFriend(friend1, friend2);
    }

    @Override
    public boolean removeFriend(User friend1, User friend2) {
        return userStorage.removeFriend(friend1, friend2);
    }

    @Override
    public List<Long> getListCommonFriends(User user1, User user2) {
        List<Long> userFriends1 = userStorage.getListFriends(user1);
        List<Long> userFriends2 = userStorage.getListFriends(user2);
        List<Long> commonFriends = new ArrayList<>();
        for (long id : userFriends1) {
            if (userFriends2.contains(id)) {
                commonFriends.add(id);
            }
        }
        return commonFriends;
    }

    @Override
    public List<Long> getListFriends(User user) {
        return userStorage.getListFriends(user);
    }

    @Override
    public List<User> getListFriendsUser(User user) {
        return userStorage.getListFriendsUser(user);
    }

}
package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserService implements UserServiceInterface {

    final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    //
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
        if (!friend1.getFriends().contains(friend2.getId())) {
            friend1.getFriends().add(friend2.getId());
            if (friend1.getId() != friend2.getId()) {
                friend2.getFriends().add(friend1.getId());
            }
            List<User> list = new ArrayList<>();
            list.add(userStorage.getUserHashMap().get(friend2.getId()));
            return list;
        }
        return null;
    }


    @Override
    public boolean removeFriend(User friend1, User friend2) {
        if (friend1.getFriends().contains(friend2.getId())) {
            friend1.getFriends().remove(friend2.getId());
            friend2.getFriends().remove(friend1.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<Long> getListCommonFriends(User user1, User user2) {
        List<Long> commonFriends = new ArrayList<>();
        commonFriends = user1.getFriends().stream()
                .filter(id -> user2.getFriends().contains(id))
                .collect(Collectors.toList());
        return commonFriends;
    }

    @Override
    public List<Long> getListFriends(User user) {
        return new ArrayList<>(user.getFriends());
    }

}
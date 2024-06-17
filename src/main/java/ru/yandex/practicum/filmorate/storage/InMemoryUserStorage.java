package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public class InMemoryUserStorage implements UserStorage {

    @Getter
    HashMap<Long, User> userHashMap = new HashMap<>();
    int idGen = 1;


    @Override
    public User getUser(long id) {
        return userHashMap.getOrDefault(id, null);
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public User createUser(User user) {
        user.setId((long) idGen++);
        userHashMap.put(user.getId(), user);
        return user;
    }


    @Override
    public void deleteUser(long id) {
        if (!userHashMap.containsKey(id)) {
            userHashMap.remove(id);
        } else {
            System.out.println(String.format("Пользователь с id %s не найден", id));

        }
    }

    @Override
    public User updateUser(User user) {
        if (userHashMap.containsKey(user.getId())) {
            userHashMap.put(user.getId(), user);
            return user;
        } else {
            System.out.println("Данного пользователя не существует");
            return null;
        }
    }
}
package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    User create(@RequestBody User user) throws ValidationException;

    Collection<User> findAll();

    User update(@RequestBody User newUser) throws ValidationException;

    void validateUser(User user) throws ValidationException;

    void delete(Long id);

    User findById(Long id);

    Collection<User> findByIds(Set<Long> ids);

}

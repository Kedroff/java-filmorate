package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {


    private final JdbcTemplate jdbcTemplate;

    LocalDate date = LocalDate.of(1990, 1, 1);

    @Test
    public void testCreateUser() {
        // Подготавливаем данные для теста
        User newUser = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        User savedUser = userStorage.createUser(newUser);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testGetUserById() {
        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(newUser);

        User savedUser = userStorage.getUser(newUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testGetUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser1 = User.builder()
                .id(5L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUser2 = User.builder()
                .id(6L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);
        List<User> users = userStorage.getUsers();

        List<User> users1 = new ArrayList<>();

        users1.add(newUser1);
        users1.add(newUser2);

        assertThat(users)
                .isNotNull()
                .isEqualTo(users1);
    }

    @Test
    public void testDeleteUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .id(7L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser);
        userStorage.deleteUser(newUser.getId());

        assertThat(userStorage.getUser(newUser.getId()))
                .isEqualTo(null);
    }

    @Test
    public void testUpdateUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();


        userStorage.createUser(newUser);

        User newUserNew = User.builder()
                .id(newUser.getId())
                .name("Ivan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .filmIdLiked(new HashSet<>())
                .build();

        userStorage.updateUser(newUserNew);

        assertThat(userStorage.getUser(newUser.getId()))
                .isNotNull()
                .isEqualTo(newUserNew);

    }

    @Test
    public void testAddFriend() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUserNew = User.builder()
                .id(newUser.getId())
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser);
        userStorage.createUser(newUserNew);

        userStorage.addFriend(newUser, newUserNew);

        List<Long> friends = new ArrayList<>();
        friends.add(newUserNew.getId());

        assertThat(userStorage.getListFriends(newUser))
                .isNotNull()
                .isEqualTo(friends);
    }

    @Test
    public void testDeleteFriend() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUserNew = User.builder()
                .id(newUser.getId())
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser);
        userStorage.createUser(newUserNew);

        userStorage.addFriend(newUser, newUserNew);
        userStorage.removeFriend(newUser, newUserNew);


        assertThat(userStorage.getListFriends(newUser))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    public void testGetListFriends() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUserNew = User.builder()
                .id(newUser.getId())
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser);
        userStorage.createUser(newUserNew);

        userStorage.addFriend(newUser, newUserNew);

        List<Long> friendId = new ArrayList<>();
        friendId.add(newUserNew.getId());

        assertThat(userStorage.getListFriends(newUser))
                .isNotNull()
                .isEqualTo(friendId);
    }

    @Test
    public void testGetListFriendsUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUserNew = User.builder()
                .id(newUser.getId())
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser);
        userStorage.createUser(newUserNew);

        userStorage.addFriend(newUser, newUserNew);

        List<User> friendId = new ArrayList<>();
        friendId.add(newUserNew);

        assertThat(userStorage.getListFriendsUser(newUser))
                .isNotNull()
                .isEqualTo(friendId);
    }

    @Test
    public void testCommonFriendsUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User newUser1 = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUser2 = User.builder()
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        User newUser3 = User.builder()
                .name("NeIvan PetrovNew")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(date))
                .friends(new ArrayList<>())
                .build();

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);
        userStorage.createUser(newUser3);


        userStorage.addFriend(newUser1, newUser3);
        userStorage.addFriend(newUser2, newUser3);


        List<Long> friendId = new ArrayList<>();
        friendId.add(newUser3.getId());

        assertThat(userStorage.getListCommonFriends(newUser1, newUser2))
                .isNotNull()
                .isEqualTo(friendId);
    }

}

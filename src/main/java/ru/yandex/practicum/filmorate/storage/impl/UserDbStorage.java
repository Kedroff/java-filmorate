package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(long id) {
        String sql = "select * from users where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (sqlRowSet.next()) {
            User user = User.builder().build();
            user.setId(id);
            user.setEmail(sqlRowSet.getString("email"));
            user.setLogin(sqlRowSet.getString("login"));
            user.setName(sqlRowSet.getString("name"));
            user.setBirthday(sqlRowSet.getDate("birthday"));
            List<Long> friendsId = this.getListFriends(user);
            user.setFriends(friendsId);
            user.setFilmIdLiked(this.getUserLikedFilms(user));
            return user;
        } else {
            return null;
        }
    }

    private Set<Long> getUserLikedFilms(User user) {
        Set<Long> filmLiked = new HashSet<>();
        String sql = "select films_id from film_liked where users_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        while (sqlRowSet.next()) {
            Long id = sqlRowSet.getLong("films_id");
            filmLiked.add(id);
        }
        return filmLiked;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        Set<Long> filmLiked = new HashSet<>();
        String sql = "select * from users";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            User user = User.builder().build();
            user.setId((long) sqlRowSet.getInt("user_id"));
            user.setEmail(sqlRowSet.getString("email"));
            user.setLogin(sqlRowSet.getString("login"));
            user.setName(sqlRowSet.getString("name"));
            user.setBirthday(sqlRowSet.getDate("birthday"));
            users.add(user);
            this.getListFriends(user);
            if (user.getFilmIdLiked() == null) {
                user.setFilmIdLiked(filmLiked);
            }
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        String sql = "insert into users (email, login, name, birthday) values(?,?,?,?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1",
                Long.class);
        Set<Long> filmLiked = new HashSet<>();
        user.setId(userId);
        if (user.getFilmIdLiked() == null) {
            user.setFilmIdLiked(filmLiked);
        }
        this.fillFriends(user);
        this.updateFriendsUser(user);
        return this.getUser(user.getId());
    }

    private void fillFriends(User user) {
        for (long id : user.getFriends()) {
            String sql = "insert into user_friend(user_id, friend_id) values(?,?)";
            jdbcTemplate.update(sql, user.getId(), id);
        }
    }

    @Override
    public void deleteUser(long id) {
        String sql = "delete from users where user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        this.updateFriendsUser(user);
        this.getUserLikedFilms(user);
        return this.getUser(user.getId());
    }

    private void updateFriendsUser(User user) {
        String sql = "select friend_id from user_friend where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        List<Long> friends = new ArrayList<>();
        user.setFriends(friends);
        while (sqlRowSet.next()) {
            user.getFriends().add(sqlRowSet.getLong("friend_id"));
        }
    }

    @Override
    public List<Long> getListFriends(User user) {
        List<Long> friendsId = new ArrayList<>();
        user.setFriends(friendsId);
        String sql = "select friend_id from user_friend where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        while (sqlRowSet.next()) {
            Long id = sqlRowSet.getLong("friend_id");
            friendsId.add(id);
        }
        return friendsId;
    }

    @Override
    public List<User> getListFriendsUser(User user) {
        List<User> friends = new ArrayList<>();
        List<Long> friendsId = this.getListFriends(user);

        for (long id : friendsId) {
            User friend = this.getUser(id);
            friends.add(friend);
        }
        return friends;
    }

    public List<Long> getListCommonFriends(User user1, User user2) { // доработать
        List<Long> commonFriendsId = new ArrayList<>();
        String sql = "select friend_id from user_friend where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, user1.getId());
        while (sqlRowSet.next()) {
            Long id = sqlRowSet.getLong("friend_id");
            commonFriendsId.add(id);
        }
        return commonFriendsId;
    }

    @Override
    public List<User> addFriend(User user1, User user2) { // доработать
        List<Long> friends = new ArrayList<>();
        List<User> usersFriends = new ArrayList<>();

        String sql = "insert into user_friend (user_id, friend_id) values(?,?)";
        jdbcTemplate.update(sql, user1.getId(), user2.getId());
        usersFriends.add(user2);
        friends.add(user2.getId());
        if (user1.getFriends() == null) {
            user1.setFriends(friends);
        } else {
            user1.getFriends().add(user2.getId());
        }
        return usersFriends;
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        String sql = "delete from user_friend where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, user.getId(), friend.getId());
        this.updateUser(user);
        this.updateUser(friend);
        return true;
    }
}
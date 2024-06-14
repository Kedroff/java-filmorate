# java-filmorate
Template repository for Filmorate project.

БД состоит из нескольких таблиц, связанных между собой:

таблица хранения пользователей (users)
таблица хранения фильмов (films)
таблица друзей (user_friends)
таблица лайков к к фильмам (film_liked)
таблица хранения статусов пользователя (status)
таблица хранения жанров (genre)
Запросы к БД:

Получить все фильмы:
SELECT * FROM films;

Получить фильм по id:
SELECT * FROM films WHERE film_id = id;

Получить определенное количество фильмов n:
SELECT * FROM films LIMIT n

Получить всех пользователей:
SELECT * FROM users;

Получить пользователя по id:
SELECT * FROM users WHERE user_id = id;

Получить друзей определенного user-а по id:
SELECT * FROM user_friends WHERE user_id = id

Получить общих друзей пользователя user1 (id1) с user2 по id (id2):
SELECT uf.user_id, u.login FROM user_friends AS uf INNER JOIN users AS u ON u.user_id = uf.user_id WHERE (uf.user_id) = id1 AND (uf.user_id) = id2 AND COUNT (user_id) > 1 GROUP BY uf.user_id
![img_1.png](img.png)
create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER
);

create unique index IF NOT EXISTS FILM_GENRE_FILM_ID_GENRE_ID_UINDEX
    on FILM_GENRE (FILM_ID, GENRE_ID);

create table IF NOT EXISTS FILM_RATINGS
(
    ID     INTEGER auto_increment,
    RATING CHARACTER VARYING(10) not null
);

create unique index IF NOT EXISTS FILM_RATINGS_ID_UINDEX
    on FILM_RATINGS (ID);

create unique index IF NOT EXISTS FILM_RATINGS_RATING_UINDEX
    on FILM_RATINGS (RATING);

alter table FILM_RATINGS
    add constraint IF NOT EXISTS FILM_RATINGS_PK
        primary key (ID);

create table IF NOT EXISTS FILMS
(
    FILM_ID       INTEGER auto_increment,
    FILM_NAME     CHARACTER VARYING(50) not null,
    DESCRIPTION   CHARACTER VARYING(200),
    RELEASE_DATE  DATE,
    FILM_DURATION INTEGER,
    RATING_ID     INTEGER,
    constraint FILMS_FILM_RATINGS_ID_FK
        foreign key (RATING_ID) references FILM_RATINGS
            on delete set null
);

create unique index IF NOT EXISTS FILMS_FILM_ID_UINDEX
    on FILMS (FILM_ID);

alter table FILMS
    add constraint IF NOT EXISTS FILMS_PK
        primary key (FILM_ID);

create table IF NOT EXISTS GENRES
(
    ID    INTEGER auto_increment,
    GENRE CHARACTER VARYING(30) not null
);

create unique index IF NOT EXISTS FILM_GENRES_GENRE_UINDEX
    on GENRES (GENRE);

create unique index IF NOT EXISTS FILM_GENRES_ID_UINDEX
    on GENRES (ID);

alter table GENRES
    add constraint IF NOT EXISTS FILM_GENRES_PK
        primary key (ID);

create table IF NOT EXISTS USERS
(
    USER_ID   INTEGER auto_increment,
    EMAIL     CHARACTER VARYING(100) not null,
    LOGIN     CHARACTER VARYING(50)  not null,
    USER_NAME CHARACTER VARYING(50),
    BIRTHDAY  DATE,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FILM_LIKES
(
    USER_ID INTEGER not null,
    FILM_ID INTEGER not null,
    constraint FILM_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create unique index IF NOT EXISTS FILM_USER_INDEX
    on FILM_LIKES (USER_ID, FILM_ID);

create table IF NOT EXISTS FRIENDSHIPS
(
    USER_ID      INTEGER not null,
    FRIEND_ID    INTEGER not null,
    CONFIRMATION BOOLEAN not null,
    constraint FRIENDSHIPS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint FRIENDSHIPS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);


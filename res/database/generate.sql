create table listtrack
(
    listid   INTEGER,
    trackurl VARCHAR(100),
    position INTEGER
);

create table listuser
(
    listid INTEGER
        constraint listuser_pk
            primary key autoincrement,
    userid VARCHAR(60)
);

create table playlist
(
    listid INTEGER
        constraint playlist_pk
            primary key autoincrement,
    name   VARCHAR(60),
    userid VARCHAR(60)
);

create table track
(
    url  VARCHAR(100),
    name VARCHAR(100)
);

create table trackuser
(
    listid VARCHAR(60)
        references playlist
            on update cascade on delete cascade,
    uri    VARCHAR(200),
    userid VARCHAR(100)
);

create table user
(
    id     VARCHAR(60),
    exp    VARCHAR(60),
    level  INTEGER,
    time   VARCHAR(60),
    credit VARCHAR(60),
    guild  VARCHAR(60)
);

create table username
(
    name VARCHAR(60),
    id   VARCHAR(60)
);



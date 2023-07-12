CREATE TABLE IF NOT EXISTS persons
(
    id serial primary key not null,
    login varchar(2000) NOT NULL unique,
    password varchar(2000)
);

comment on table persons is 'Пользователь';
comment on column persons.id is 'Идентификатор пользователя';
comment on column persons.login is 'Логин пользователя';
comment on column persons.password is 'Пароль пользователя';

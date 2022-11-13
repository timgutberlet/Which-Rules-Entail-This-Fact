create table objects
(
    id  integer not null
        constraint objects_pk
            primary key,
    txt varchar
);

alter table objects
    owner to postgres;


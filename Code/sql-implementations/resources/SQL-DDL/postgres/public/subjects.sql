create table subjects
(
    id  integer not null
        constraint subjects_pk
            primary key,
    txt text
);

alter table subjects
    owner to postgres;


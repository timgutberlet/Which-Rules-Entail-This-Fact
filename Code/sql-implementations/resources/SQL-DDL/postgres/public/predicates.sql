create table predicates
(
    id  integer not null
        constraint predicates_pk
            primary key,
    txt varchar
);

alter table predicates
    owner to postgres;


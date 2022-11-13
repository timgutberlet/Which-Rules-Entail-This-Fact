create table iku
(
    sub integer not null,
    pre integer not null,
    obj integer not null,
    constraint indexed_knowledgegraph_unique_pk
        primary key (pre, sub, obj)
);

alter table iku
    owner to postgres;


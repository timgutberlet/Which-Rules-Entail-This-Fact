create table knowledgegraph
(
    sub integer,
    pre integer,
    obj integer
);

alter table knowledgegraph
    owner to postgres;


create table ik
(
    sub integer,
    pre integer,
    obj integer
);

alter table ik
    owner to postgres;

create index indexed_knowledgegraph_predicate_subject_object_index
    on ik (pre, sub, obj);


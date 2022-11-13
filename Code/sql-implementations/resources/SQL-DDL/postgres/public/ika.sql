create table ika
(
    sub integer,
    pre integer,
    obj integer
);

alter table ika
    owner to postgres;

create index indexed_knowledgegraph_all_pre_index
    on ika (pre);

create index indexed_knowledgegraph_all_pre_obj_index
    on ika (pre, obj);

create index indexed_knowledgegraph_all_pre_sub_index
    on ika (pre, sub);

create index indexed_knowledgegraph_all_pre_sub_obj_index
    on ika (pre, sub, obj);


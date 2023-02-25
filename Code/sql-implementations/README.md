# Setup Development
1. Add pgsql driver to dependencies
2. Add database view to IntelliJ
3. Create Tables:

### SQL Create commands for the sql-implementation Database:
create table knowledgegraph
(
sub integer,
pre integer,
obj integer
);

alter table knowledgegraph
owner to postgres;

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

create table predicates
(
id  integer not null
constraint predicates_pk
primary key,
txt varchar
);

alter table predicates
owner to postgres;

create table subjects
(
id  integer not null
constraint subjects_pk
primary key,
txt text
);

alter table subjects
owner to postgres;

create table objects
(
id  integer not null
constraint objects_pk
primary key,
txt varchar
);

alter table objects
owner to postgres;

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


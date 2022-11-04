# Setup Development
1. Add pgsql driver to dependencies
2. Add database view to IntelliJ
3. Create Tables:

###SQL Create commands for the sql-implementation Database:
create table knowledgegraph
(
sub integer,
pre integer,
obj integer
);

alter table knowledgegraph
owner to postgres;

create table indexed_knowledgegraph
(
sub  integer,
pred integer,
obj  integer
);

alter table indexed_knowledgegraph
owner to postgres;

create index indexed_knowledgegraph_predicate_subject_object_index
on indexed_knowledgegraph (pred, sub, obj);

create table indexed_knowledgegraph_unique
(
sub integer,
pre integer,
obj integer
);

alter table indexed_knowledgegraph_unique
owner to postgres;

create unique index createindexeduniqueknowledgegraphdb_predicate_subject_object_ui
on indexed_knowledgegraph_unique (pre, sub, obj);

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


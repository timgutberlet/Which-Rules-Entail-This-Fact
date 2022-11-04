# Online Rule Search Database
Tim Gutberlet (tgutberl@mail.uni-mannheim.de) & Janik Sauerbier (jsauerbi@mail.uni-mannheim.de)

## Folder structure

### Code
This folder contains the codebase for relevant software (e.g., AnyBURL) as well as experimental scripts and databases created throughout our project.

### Meeting Notes
This folder contains all Meeting Notes including the aganda and resulting ToDo's within the context of the project.

### Related Work
A collection of related work, notes on related work and a collection of questions and answers that came up along the way.

## Projects Tab
All tasks and responsibilities are managed with a kanban board on the Projects Tab.

## SQL Create commands for the sql-implementation Database:
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

create index indexed_knowledgegraph_predicate_index
on indexed_knowledgegraph (pred);

create index indexed_knowledgegraph_predicate_object_index
on indexed_knowledgegraph (pred, obj);

create index indexed_knowledgegraph_predicate_subject_index
on indexed_knowledgegraph (pred, sub);

create index indexed_knowledgegraph_predicate_subject_object_index
on indexed_knowledgegraph (pred, sub, obj);

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

create table indexed_knowledgegraph_unique
(
sub integer
constraint createindexeduniqueknowledgegraphdb_subjects_id_fk
references subjects,
pre integer
constraint createindexeduniqueknowledgegraphdb_predicates_id_fk
references predicates,
obj integer
constraint createindexeduniqueknowledgegraphdb_objects_id_fk
references objects
);

alter table indexed_knowledgegraph_unique
owner to postgres;

create unique index createindexeduniqueknowledgegraphdb_predicate_subject_object_ui
on indexed_knowledgegraph_unique (pre, sub, obj);


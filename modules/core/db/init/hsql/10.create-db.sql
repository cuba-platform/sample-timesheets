-- begin TS_HOLIDAY
create table TS_HOLIDAY (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    START_DATE date not null,
    END_DATE date not null,
    NAME varchar(100) not null,
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TS_HOLIDAY
-- begin TS_CLIENT
create table TS_CLIENT (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    CODE varchar(50) not null,
    --
    primary key (ID)
)^
-- end TS_CLIENT
-- begin TS_PROJECT_ROLE
create table TS_PROJECT_ROLE (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TS_PROJECT_ROLE
-- begin TS_TASK_TYPE
create table TS_TASK_TYPE (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TS_TASK_TYPE
-- begin TS_TAG
create table TS_TAG (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(255),
    TAG_TYPE_ID varchar(36),
    --
    primary key (ID)
)^
-- end TS_TAG
-- begin TS_TAG_TYPE
create table TS_TAG_TYPE (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(255),
    PROJECT_ID varchar(36),
    --
    primary key (ID)
)^
-- end TS_TAG_TYPE
-- begin TS_PROJECT
create table TS_PROJECT (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    PARENT_ID varchar(36),
    CLIENT_ID varchar(36) not null,
    DESCRIPTION varchar(255),
    STATUS integer not null,
    --
    primary key (ID)
)^
-- end TS_PROJECT
-- begin TS_TASK
create table TS_TASK (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(255),
    PROJECT_ID varchar(36) not null,
    TYPE_ID varchar(36) not null,
    STATUS integer not null,
    --
    primary key (ID)
)^
-- end TS_TASK
-- begin TS_TIME_ENTRY
create table TS_TIME_ENTRY (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID varchar(36) not null,
    TASK_NAME varchar(100),
    USER_ID varchar(36) not null,
    DATE_ date not null,
    TIME_ time not null,
    STATUS integer not null,
    TICKET varchar(255),
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TS_TIME_ENTRY
-- begin TS_PROJECT_PARTICIPANT
create table TS_PROJECT_PARTICIPANT (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID varchar(36) not null,
    PROJECT_ID varchar(36) not null,
    CODE varchar(50) not null,
    ROLE_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end TS_PROJECT_PARTICIPANT
-- begin TS_TASK_PROJECT_PARTICIPANT_LINK
create table TS_TASK_PROJECT_PARTICIPANT_LINK (
    TASK_ID varchar(36) not null,
    PROJECT_PARTICIPANT_ID varchar(36) not null,
    primary key (TASK_ID, PROJECT_PARTICIPANT_ID)
)^
-- end TS_TASK_PROJECT_PARTICIPANT_LINK
-- begin TS_TIME_ENTRY_TAG_LINK
create table TS_TIME_ENTRY_TAG_LINK (
    TIME_ENTRY_ID varchar(36) not null,
    TAG_ID varchar(36) not null,
    primary key (TIME_ENTRY_ID, TAG_ID)
)^
-- end TS_TIME_ENTRY_TAG_LINK
-- begin TS_TASK_TAG_TYPE_LINK
create table TS_TASK_TAG_TYPE_LINK (
    TASK_ID varchar(36) not null,
    TAG_TYPE_ID varchar(36) not null,
    primary key (TASK_ID, TAG_TYPE_ID)
)^
-- end TS_TASK_TAG_TYPE_LINK
-- begin TS_TASK_TAG_LINK
create table TS_TASK_TAG_LINK (
    TASK_ID varchar(36) not null,
    TAG_ID varchar(36) not null,
    primary key (TASK_ID, TAG_ID)
)^
-- end TS_TASK_TAG_LINK
-- begin SEC_USER
alter table SEC_USER add column WORK_HOURS_FOR_WEEK double precision ^
-- end SEC_USER

------------------------------------------------------------------------------------------------------------------------

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('5bc577ab-44f3-a652-da29-4ae06c02d43b', 'Manager', '001', 'Can create sub-projects, tasks, change project properties, approve tomsheets', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('6a8e8c24-d639-ef26-fac8-57e13bbfed48', 'Worker', '002', 'Can create time entries. Only see time entries related to him.', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('3182a9b0-5ffb-6062-d58c-0148b7b3af3e', 'Approver', '003', 'Can approve timesheets', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('4111a014-534c-3935-bf70-146a2e5c0970', 'Observer', '004', 'Can only view projects, task and time entries.', 0, current_timestamp);

-- begin TS_HOLIDAY
create table TS_HOLIDAY (
    ID uuid,
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
    ID uuid,
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
)^-- end TS_CLIENT
-- begin TS_PROJECT_ROLE
create table TS_PROJECT_ROLE (
    ID uuid,
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
)^-- end TS_PROJECT_ROLE
-- begin TS_TASK_TYPE
create table TS_TASK_TYPE (
    ID uuid,
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
)^-- end TS_TASK_TYPE
-- begin TS_TAG
create table TS_TAG (
    ID uuid,
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
    TAG_TYPE_ID uuid not null,
    --
    primary key (ID)
)^-- end TS_TAG
-- begin TS_TAG_TYPE
create table TS_TAG_TYPE (
    ID uuid,
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
)^-- end TS_TAG_TYPE
-- begin TS_PROJECT
create table TS_PROJECT (
    ID uuid,
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
    PARENT_ID uuid,
    CLIENT_ID uuid not null,
    DESCRIPTION varchar(255),
    STATUS varchar(50) not null,
    TIME_ENTRY_NAME_PATTERN varchar(500),
    --
    primary key (ID)
)^-- end TS_PROJECT
-- begin TS_TASK
create table TS_TASK (
    ID uuid,
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
    PROJECT_ID uuid not null,
    TYPE_ID uuid,
    STATUS varchar(50) not null,
    --
    primary key (ID)
)^-- end TS_TASK
-- begin TS_TIME_ENTRY
create table TS_TIME_ENTRY (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid not null,
    TASK_NAME varchar(100),
    USER_ID uuid not null,
    DATE_ date not null,
    TIME_IN_MINUTES integer not null,
    STATUS varchar(50) not null,
    DESCRIPTION varchar(255),
    REJECTION_REASON varchar(255),
    ACTIVITY_TYPE_ID uuid,
    TIME_IN_HOURS decimal(10, 2) not null,
    --
    primary key (ID)
)^-- end TS_TIME_ENTRY
-- begin TS_PROJECT_PARTICIPANT
create table TS_PROJECT_PARTICIPANT (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID uuid not null,
    PROJECT_ID uuid not null,
    ROLE_ID uuid not null,
    --
    primary key (ID)
)^-- end TS_PROJECT_PARTICIPANT
-- begin TS_TASK_PROJECT_PARTICIPANT_LINK
create table TS_TASK_PROJECT_PARTICIPANT_LINK (
    TASK_ID uuid,
    PROJECT_PARTICIPANT_ID uuid,
    primary key (TASK_ID, PROJECT_PARTICIPANT_ID)
)^
-- end TS_TASK_PROJECT_PARTICIPANT_LINK
-- begin TS_TIME_ENTRY_TAG_LINK
create table TS_TIME_ENTRY_TAG_LINK (
    TIME_ENTRY_ID uuid,
    TAG_ID uuid,
    primary key (TIME_ENTRY_ID, TAG_ID)
)^
-- end TS_TIME_ENTRY_TAG_LINK
-- begin TS_TASK_TAG_TYPE_LINK
create table TS_TASK_TAG_TYPE_LINK (
    TASK_ID uuid,
    TAG_TYPE_ID uuid,
    primary key (TASK_ID, TAG_TYPE_ID)
)^
-- end TS_TASK_TAG_TYPE_LINK
-- begin TS_TASK_TAG_LINK
create table TS_TASK_TAG_LINK (
    TASK_ID uuid,
    TAG_ID uuid,
    primary key (TASK_ID, TAG_ID)
)^
-- end TS_TASK_TAG_LINK
-- begin SEC_USER
alter table SEC_USER add column WORK_HOURS_FOR_WEEK decimal(19, 2) ^
update SEC_USER set WORK_HOURS_FOR_WEEK = 0 where WORK_HOURS_FOR_WEEK is null ^
alter table SEC_USER alter column WORK_HOURS_FOR_WEEK set not null ^
alter table SEC_USER add column DTYPE varchar(100) ^
update SEC_USER set DTYPE = 'ts$ExtUser' where DTYPE is null ^
-- end SEC_USER
-- begin TS_TAG_TYPE_PROJECT_LINK
create table TS_TAG_TYPE_PROJECT_LINK (
    TAG_TYPE_ID uuid,
    PROJECT_ID uuid,
    primary key (TAG_TYPE_ID, PROJECT_ID)
)^
-- end TS_TAG_TYPE_PROJECT_LINK
-- begin TS_ACTIVITY_TYPE
create table TS_ACTIVITY_TYPE (
    ID uuid,
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
-- end TS_ACTIVITY_TYPE
-- begin TS_ACTIVITY_TYPE_PROJECT_LINK
create table TS_ACTIVITY_TYPE_PROJECT_LINK (
    ACTIVITY_TYPE_ID uuid,
    PROJECT_ID uuid,
    primary key (ACTIVITY_TYPE_ID, PROJECT_ID)
)^
-- end TS_ACTIVITY_TYPE_PROJECT_LINK

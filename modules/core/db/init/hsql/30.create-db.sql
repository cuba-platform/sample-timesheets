--The following field used to emulate unique index with null value
--Base HSQL unique index does not support nulls http://hsqldb.org/doc/guide/ch02.html#N102DF
alter table TS_TIME_ENTRY add column INDEX_FIELD varchar(100) GENERATED ALWAYS AS (TASK_ID || ' ' || DATE_ || ' ' || case when DELETE_TS is not null then 'DELETED' else 'NOT_DELETED' end )^
create unique index IDX_TS_TIME_ENTRY_UNIQ_TASK_DATE on TS_TIME_ENTRY (INDEX_FIELD)^

------------------------------------------------------------------------------------------------------------------------
insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('5bc577ab-44f3-a652-da29-4ae06c02d43b', 'Manager', 'manager', 'Can create sub-projects, tasks, change project properties, approve tomsheets', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('6a8e8c24-d639-ef26-fac8-57e13bbfed48', 'Worker', 'worker', 'Can create time entries. Only see time entries related to him.', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('3182a9b0-5ffb-6062-d58c-0148b7b3af3e', 'Approver', 'approver', 'Can approve timesheets', 0, current_timestamp);

insert into TS_PROJECT_ROLE
(ID, NAME, CODE, DESCRIPTION, VERSION, CREATE_TS)
values ('4111a014-534c-3935-bf70-146a2e5c0970', 'Observer', 'observer', 'Can only view projects, task and time entries.', 0, current_timestamp);

------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------

insert into SEC_USER
(WORK_HOURS_FOR_WEEK, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (40.0, 'glebfox', 'glebfox', '0fb48e0f9dc5f35a70e560d95c4b98d6ab387166', 'Gorelov G. ', 'Gleb', 'Gorelov', null, 'Java Developer', null, 'ru', null, null, true, false, '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', null, 1, '2015-05-12 05:25:23', null, null, null, '508b3379-2ed4-ad62-2f82-e3f7cc1dca89', '2015-05-12 05:25:23', 'admin');

insert into SEC_USER
(WORK_HOURS_FOR_WEEK, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (40.0, 'doe', 'doe', 'f5e458b04289a25d9c9af7e1ce1da3205aa032fc', 'Doe J. ', 'John', 'Doe', null, 'QA', null, 'ru', null, null, true, false, '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', null, 1, '2015-05-12 05:25:50', null, null, null, '67c48a40-0d90-7105-92a9-6ea95de6275d', '2015-05-12 05:25:50', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_CLIENT
(NAME, CODE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Haulmont', '001', 1, '2015-04-24 04:45:09', null, null, null, 'f2e1555d-51a7-ea52-4505-fc65c5dde1c6', '2015-04-24 04:45:09', 'admin');

insert into TS_CLIENT
(NAME, CODE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Monkey Code, Inc.', '007', 1, '2015-04-29 02:54:57', null, null, null, '625dcc5d-7a06-86b5-a8e5-617bc127a9f2', '2015-04-29 02:54:57', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_TASK_TYPE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Developing', '001', null, 1, '2015-04-24 04:48:09', null, null, null, '3e31ca9a-943d-f272-cc82-5d8f47f604d3', '2015-04-24 04:48:09', 'admin');

insert into TS_TASK_TYPE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', '002', null, 1, '2015-04-24 08:09:48', null, null, null, 'dbfca5dd-5d1c-8cee-8114-64853e888aa9', '2015-04-24 08:09:48', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_TAG_TYPE
(NAME, CODE, DESCRIPTION, PROJECT_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Developing', '001', null, null, 1, '2015-04-24 05:01:41', null, null, null, '9ad8c4a4-cb19-342f-545f-425083ddb811', '2015-04-24 05:01:41', 'admin');

insert into TS_TAG_TYPE
(NAME, CODE, DESCRIPTION, PROJECT_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', '002', null, null, 1, '2015-04-24 05:01:59', null, null, null, '60a91eee-52f4-f0a2-aeb6-1d081cac82a8', '2015-04-24 05:01:59', 'admin');

------------------------------------------------------------------------------------------------------------------------

--insert into TS_TAG
--(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
--values ('Backend', '001', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-04-24 08:10:55', null, null, null, '4f532d67-b00e-46c8-4029-73be8f9fb91b', '2015-04-24 08:10:55', 'admin');
--
--insert into TS_TAG
--(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
--values ('GUI', '002', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-04-24 05:03:19', null, null, null, '0df15894-b872-2ce7-7611-19cb0bcc2dac', '2015-04-24 05:03:19', 'admin');
--
--insert into TS_TAG
--(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
--values ('GUI', '005', null, '60a91eee-52f4-f0a2-aeb6-1d081cac82a8', 1, '2015-04-24 05:03:40', null, null, null, '3b71215e-4cac-a10a-5fa4-d82efa966f11', '2015-04-24 05:03:40', 'admin');
--
--insert into TS_TAG
--(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
--values ('Design', '009', null, null, 1, '2015-04-24 05:05:10', null, null, null, 'aab3ed9c-ee87-8860-c316-0f18e1b2578b', '2015-04-24 05:05:10', 'admin');

------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------------------------------------------------------------

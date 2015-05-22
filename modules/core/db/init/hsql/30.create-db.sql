--The following field used to emulate unique index with null value
--Base HSQL unique index does not support nulls http://hsqldb.org/doc/guide/ch02.html#N102DF
alter table TS_TIME_ENTRY add column INDEX_FIELD varchar(100) GENERATED ALWAYS AS (TASK_ID || ' ' || DATE_ || ' ' || case when DELETE_TS is not null then '-'||DELETE_TS else '-NOT_DELETED' end )^
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

insert into SEC_USER
(WORK_HOURS_FOR_WEEK, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (40.0, 'glebfox', 'glebfox', '0fb48e0f9dc5f35a70e560d95c4b98d6ab387166', 'Gorelov G. ', 'Gleb', 'Gorelov', null, 'Java Developer', null, 'ru', null, null, true, false, '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', null, 1, '2015-05-12 05:25:23', null, null, null, '508b3379-2ed4-ad62-2f82-e3f7cc1dca89', '2015-05-12 05:25:23', 'admin');

insert into SEC_USER
(WORK_HOURS_FOR_WEEK, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (40.0, 'doe', 'doe', 'f5e458b04289a25d9c9af7e1ce1da3205aa032fc', 'Doe J. ', 'John', 'Doe', null, 'QA', null, 'ru', null, null, true, false, '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', null, 1, '2015-05-12 05:25:50', null, null, null, '67c48a40-0d90-7105-92a9-6ea95de6275d', '2015-05-12 05:25:50', 'admin');

insert into SEC_USER
(WORK_HOURS_FOR_WEEK, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (40.0, 'degtyarjov', 'degtyarjov', '9031846c28dc8f93b3b23c133d48567dec6ffd68', 'Degtyarjov E. ', 'Eugeniy', 'Degtyarjov', null, 'Tech lead', null, 'ru', null, null, true, false, '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', null, 1, '2015-05-20 10:00:55', null, null, null, '683f3fa9-4f91-510d-1b29-93426970e67c', '2015-05-20 10:00:55', 'admin');

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
values ('Development', '001', null, 1, '2015-04-24 04:48:09', null, null, null, '3e31ca9a-943d-f272-cc82-5d8f47f604d3', '2015-04-24 04:48:09', 'admin');

insert into TS_TASK_TYPE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', '002', null, 1, '2015-04-24 08:09:48', null, null, null, 'dbfca5dd-5d1c-8cee-8114-64853e888aa9', '2015-04-24 08:09:48', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_TAG_TYPE
(NAME, CODE, DESCRIPTION, PROJECT_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Development', '001', null, null, 1, '2015-04-24 05:01:41', null, null, null, '9ad8c4a4-cb19-342f-545f-425083ddb811', '2015-04-24 05:01:41', 'admin');

insert into TS_TAG_TYPE
(NAME, CODE, DESCRIPTION, PROJECT_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', '002', null, null, 1, '2015-04-24 05:01:59', null, null, null, '60a91eee-52f4-f0a2-aeb6-1d081cac82a8', '2015-04-24 05:01:59', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Middleware', 'MIDDLEWARE', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-05-20 09:50:29', null, null, null, '940bff55-c079-071d-3323-9e4454d27a7e', '2015-05-20 09:50:29', 'admin');

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Client', 'DEV_CLIENT', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-05-20 09:53:29', null, null, null, 'b1f3dc3a-70ef-f3da-d6ef-f820670b997c', '2015-05-20 09:53:29', 'glebfox');

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('GUI', 'TEST_GUI', null, '60a91eee-52f4-f0a2-aeb6-1d081cac82a8', 1, '2015-05-20 09:51:05', null, null, null, '970d0943-b949-2db1-e550-49c59038a5eb', '2015-05-20 09:51:05', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_PROJECT
(NAME, CODE, PARENT_ID, CLIENT_ID, DESCRIPTION, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Platform', 'PL', null, 'f2e1555d-51a7-ea52-4505-fc65c5dde1c6', null, 'open', 2, '2015-05-20 09:44:16', 'glebfox', null, null, '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', '2015-05-20 09:43:40', 'admin');

insert into TS_PROJECT
(NAME, CODE, PARENT_ID, CLIENT_ID, DESCRIPTION, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('TimeSheets', 'TS', null, 'f2e1555d-51a7-ea52-4505-fc65c5dde1c6', null, 'open', 1, '2015-05-20 09:44:12', null, null, null, 'df7778dd-0451-84c9-40e7-2144118fb83b', '2015-05-20 09:44:12', 'admin');

insert into TS_PROJECT
(NAME, CODE, PARENT_ID, CLIENT_ID, DESCRIPTION, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Demo', 'TS_DEMO', 'df7778dd-0451-84c9-40e7-2144118fb83b', 'f2e1555d-51a7-ea52-4505-fc65c5dde1c6', null, 'open', 1, '2015-05-20 09:44:33', null, null, null, '898fcc5a-3316-b8a1-0ad2-c1c45f84da9b', '2015-05-20 09:44:33', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('508b3379-2ed4-ad62-2f82-e3f7cc1dca89', '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', '6a8e8c24-d639-ef26-fac8-57e13bbfed48', 1, '2015-05-20 09:47:57', null, null, null, '48af185e-37d9-2e3f-491d-3cb80a62b0b1', '2015-05-20 09:47:57', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('67c48a40-0d90-7105-92a9-6ea95de6275d', '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', '5bc577ab-44f3-a652-da29-4ae06c02d43b', 1, '2015-05-20 09:48:03', null, null, null, '33cc4eb6-4eea-0107-96c8-635b912125a5', '2015-05-20 09:48:03', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('508b3379-2ed4-ad62-2f82-e3f7cc1dca89', 'df7778dd-0451-84c9-40e7-2144118fb83b', '5bc577ab-44f3-a652-da29-4ae06c02d43b', 1, '2015-05-20 09:48:34', null, null, null, '085acdae-69bd-e2fc-efa6-5d47d8a55b1a', '2015-05-20 09:48:34', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('508b3379-2ed4-ad62-2f82-e3f7cc1dca89', '898fcc5a-3316-b8a1-0ad2-c1c45f84da9b', '3182a9b0-5ffb-6062-d58c-0148b7b3af3e', 1, '2015-05-20 09:48:54', null, null, null, '31a9af66-a18c-192d-3fac-788aa9b86145', '2015-05-20 09:48:54', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('67c48a40-0d90-7105-92a9-6ea95de6275d', '898fcc5a-3316-b8a1-0ad2-c1c45f84da9b', '6a8e8c24-d639-ef26-fac8-57e13bbfed48', 1, '2015-05-20 09:48:59', null, null, null, '4649b515-df51-3353-1e65-5a127d799e97', '2015-05-20 09:48:59', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('683f3fa9-4f91-510d-1b29-93426970e67c', 'df7778dd-0451-84c9-40e7-2144118fb83b', '5bc577ab-44f3-a652-da29-4ae06c02d43b', 1, '2015-05-20 10:01:35', null, null, null, '5376e418-d9d6-8b0a-0f7d-b9c8cf135c1a', '2015-05-20 10:01:35', 'admin');

insert into TS_PROJECT_PARTICIPANT
(USER_ID, PROJECT_ID, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('683f3fa9-4f91-510d-1b29-93426970e67c', '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', '6a8e8c24-d639-ef26-fac8-57e13bbfed48', 1, '2015-05-20 10:02:22', null, null, null, '476c7554-968e-a904-a1bf-de5c1c544154', '2015-05-20 10:02:22', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_TASK
(NAME, CODE, DESCRIPTION, PROJECT_ID, TYPE_ID, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Development (common)', 'PL_DEV', null, '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', '3e31ca9a-943d-f272-cc82-5d8f47f604d3', 'active', 1, '2015-05-20 09:56:19', null, null, null, 'db5a82ab-648c-9597-fe4a-63566aa67cb4', '2015-05-20 09:56:19', 'admin');

insert into TS_TASK
(NAME, CODE, DESCRIPTION, PROJECT_ID, TYPE_ID, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', 'PL_TEST', null, '9ee57d7f-f599-6a22-d7bf-ea8c828aa9ca', 'dbfca5dd-5d1c-8cee-8114-64853e888aa9', 'active', 1, '2015-05-20 09:57:00', null, null, null, '876aca5e-e4cb-c989-794d-b9b43941a447', '2015-05-20 09:57:00', 'admin');

insert into TS_TASK
(NAME, CODE, DESCRIPTION, PROJECT_ID, TYPE_ID, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Development', 'TS_DEV', null, 'df7778dd-0451-84c9-40e7-2144118fb83b', '3e31ca9a-943d-f272-cc82-5d8f47f604d3', 'active', 2, '2015-05-20 10:02:05', 'glebfox', null, null, '9e4da6c7-0e86-5bbb-c9ee-30a6ade5875d', '2015-05-20 09:58:34', 'admin');

insert into TS_TASK
(NAME, CODE, DESCRIPTION, PROJECT_ID, TYPE_ID, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Testing', 'TS_TEST', null, 'df7778dd-0451-84c9-40e7-2144118fb83b', 'dbfca5dd-5d1c-8cee-8114-64853e888aa9', 'active', 1, '2015-05-20 10:03:35', null, null, null, '263d2d06-47c0-b0db-e845-74d5fa1628ed', '2015-05-20 10:03:35', 'admin');

insert into TS_TASK
(NAME, CODE, DESCRIPTION, PROJECT_ID, TYPE_ID, STATUS, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Development', 'TS_DEMO_DEV', null, '898fcc5a-3316-b8a1-0ad2-c1c45f84da9b', '3e31ca9a-943d-f272-cc82-5d8f47f604d3', 'active', 2, '2015-05-20 10:04:16', 'glebfox', null, null, '8d5f8d57-8dac-7702-32d9-d58f423a9edb', '2015-05-20 10:04:06', 'admin');

------------------------------------------------------------------------------------------------------------------------

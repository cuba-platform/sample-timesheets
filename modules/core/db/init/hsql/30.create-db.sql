--The following field used to emulate unique index with null value
--Base HSQL unique index does not support nulls http://hsqldb.org/doc/guide/ch02.html#N102DF
alter table TS_TIME_ENTRY add column INDEX_FIELD varchar(200)
GENERATED ALWAYS AS (USER_ID || ' ' || TASK_ID || ' ' || DATE_ || ' ' || case when DELETE_TS is not null then '-'||DELETE_TS else '-NOT_DELETED' end )^
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
insert into SEC_ROLE
(NAME, LOC_NAME, DESCRIPTION, ROLE_TYPE, IS_DEFAULT_ROLE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Base role', 'Базовая роль', null, 0, true, 3, '2015-05-22 10:52:43', 'admin', null, null, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', '2015-05-22 10:46:37', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '48f19459-a075-daf6-265c-7c61aee243cd', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'f4a70a8d-1604-7d28-ae28-563fb821b325', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Group:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '73766a21-0f50-c85f-d93c-1c9b28ea0cab', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Role:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '3fd61ce5-9b53-c036-daa9-85abd15f9ff8', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'afb82f6e-7a4c-0fff-a8cc-3408bf04d6ff', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '8b026708-cc90-da7f-795f-2c2941402ab4', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'a3b8c4a4-e6f8-c40b-60da-894d388ef5ea', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'ad009476-9ea0-b0f1-d5be-11df0b94364f', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Group:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '01d87b5d-941a-91b8-4c45-c545152a0e19', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'c506c40c-393b-6c8e-6d93-5e0698824158', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '77e8b6fe-43fd-3b8e-65d9-431a5a8da513', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '3359999f-27fa-9478-b25c-3e94459a9001', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '2c48125f-f9ed-24be-9647-01ab12b6101a', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '4eba33a6-f39d-9eda-9410-99b6be3a909e', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '33c73fa6-ca87-e9c4-0428-0fa7ccf1bd4d', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '7e8a9f8f-dee6-56b2-041a-65ed69839cb8', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Group:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'eb8c8b98-0f01-4879-0de2-2ecec881a15f', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'f8644405-2939-526f-03e7-2a361a62e889', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '0534657e-9a7a-a504-33b6-01cbab3d9295', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '5ea8da7a-d0ae-3d56-c4fe-770a28384a46', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '210f814c-070c-7383-01d0-980b8ef4f92c', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '2af1326d-ed2f-3e34-29ca-08e57bbd97d2', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheet-settings', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:46:37', null, null, null, '655e7d89-4771-71c0-a28a-64966c7e11df', '2015-05-22 10:46:37', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'fe4dea1e-36ca-8fed-2230-80e528b26cd7', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '9c098b81-b8bd-fd73-d669-2dd09e3cacd6', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '72ad6a53-015c-73e0-5a3a-5c83785d859b', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'a50c5432-26b1-3c0f-a055-87715b218343', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'a10bee36-6671-0559-8859-6bd564a0adcc', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Group:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '47c02d53-83d4-89b7-e369-a2ff96d0e3da', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '08cf9e49-3ca5-b0ae-795a-2cef151d9001', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '80213d97-f927-8870-7888-33f0e658dc65', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'a10ef068-f015-4134-286e-fbd2d2b5ba7a', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '74654941-62fe-8837-d3cf-5148efcf712d', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '186385b7-f0a7-b495-3636-d8ade89f863a', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '2f1c36b3-cab0-41fa-ae4f-80a9218bed2f', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'a01dd543-f004-1659-1e59-809908e1db42', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '1ec3b9f0-744b-f7c3-cc68-770e3b029455', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Role:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'b2e9eea5-519d-fbe7-38c8-d49667620eb3', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'cc9710f7-95f4-0ebd-ebb5-b60c42e9d3e9', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '152f690d-d335-f8cc-9368-66c26eed0af3', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '03b465d6-d50c-f4cd-e3b4-7e1e303fd656', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'd3376f7b-b048-aa97-455e-b47a13ab6320', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '40e6b34d-e2c2-4461-5be2-4b5dbf5c996a', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'projects', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:46:37', null, null, null, '7bfc5acd-3886-944e-f6ae-490df732c54d', '2015-05-22 10:46:37', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheets', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:50:46', null, null, null, '348539dd-611c-4233-be16-aa80247cdb1a', '2015-05-22 10:50:46', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'bb0d0544-b2b3-d351-eaf9-f5390f5de041', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'a2112651-0fa8-1677-ddae-f51243983e21', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '1697f17d-4fbb-8e9f-179b-1266dbed7ab8', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'administration', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:46:37', null, null, null, '8ba86a69-455f-e4d1-acc6-fb069436c491', '2015-05-22 10:46:37', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:create', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '215784d2-0618-3a75-1f26-57ac28c14081', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '65dcc10c-896e-a483-f2a8-aeb98459f99c', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Role:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '62d3bf65-a6b4-9228-ad04-ed5891f698e5', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:update', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, '18cbd606-3bf6-5664-6ec9-c18000c310d4', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$Role:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'a7e5b964-a2ce-9283-ec3f-ed5bdee5db7c', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '7154a0cb-f824-118e-f025-b686ccf5b3f9', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:read', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 10:57:56', null, null, null, 'c81d7ac9-405a-a6e8-aab5-3b7a9795601d', '2015-05-22 10:57:56', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, 'fa112601-53dd-3c23-d7a8-9a465d2c0021', '2015-05-22 11:23:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:delete', 0, 'daba747f-8376-b3b5-ccfa-e19e8e841fa2', 1, '2015-05-22 11:23:58', null, null, null, '301c1ab6-cd9b-6324-f069-228139676d99', '2015-05-22 11:23:58', 'admin');

insert into SEC_ROLE
(NAME, LOC_NAME, DESCRIPTION, ROLE_TYPE, IS_DEFAULT_ROLE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Worker', 'Сотрудник', null, 0, null, 1, '2015-05-22 10:52:28', null, null, null, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, '7c0f9e62-e6e2-3b76-d00b-982158371d76', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, '6e8a516d-f63f-b709-fd19-12093f1bdd40', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:update', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, '8588d89e-7812-8cb7-c050-a54d7c587f8b', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheets', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, 'b5dda179-9ef9-e511-af72-b92e2d704f40', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, '6b080b46-2829-15fe-1f36-e4ec348fa684', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:delete', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 13:09:44', null, null, null, '3217f0a7-a427-3240-f132-1ed794a5f2dd', '2015-05-22 13:09:44', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:create', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 13:09:44', null, null, null, '792cabe9-e30d-f1bb-0ed5-35d506a06ec0', '2015-05-22 13:09:44', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, 'f10d3c0f-b2d1-1b3c-54e6-df8221c6a630', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, 'd85d5449-467a-ed48-276e-ee7c540a3b0e', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, '0d57f6dc-3095-2ea4-6c87-9bd944bebcee', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, '5ff2c753-4b9e-5a5e-36dd-0fd6c065f2d7', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:delete', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 13:09:44', null, null, null, 'd39fb3ba-20a0-c3fa-4c66-85b45fb36a89', '2015-05-22 13:09:44', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:update', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, '6b870309-1c18-a08e-fa07-693f67dae7d7', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:update', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 10:52:28', null, null, null, '469b2aff-0fd5-c9db-12e7-f0b68e9376b5', '2015-05-22 10:52:28', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, 'dcee6ebd-0814-d2df-3cd4-44d7f5dfb26e', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, 'e5ffcbd2-f19b-15e3-865a-72099c9124fe', '2015-05-22 11:00:58', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:create', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 13:09:44', null, null, null, 'e98faf59-eabe-f999-aad2-3b2424661672', '2015-05-22 13:09:44', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:read', 1, '5b0826b2-7e1a-7b5b-6344-ce0d387e0359', 1, '2015-05-22 11:00:58', null, null, null, '6d29ed58-3acb-d545-6821-cf47ea2f4ec4', '2015-05-22 11:00:58', 'admin');

insert into SEC_ROLE
(NAME, LOC_NAME, DESCRIPTION, ROLE_TYPE, IS_DEFAULT_ROLE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Approver', 'Утверждающий таймшиты', null, 0, null, 1, '2015-05-22 11:20:48', null, null, null, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'dbfcd4df-45e2-200f-bf3b-dc43295e5221', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, '610f6155-b7ed-6838-b3fa-1ea975a4bf00', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:29:45', null, null, null, 'dd85d385-741c-7496-c4f0-2387bdedf150', '2015-05-22 11:29:45', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'a4e4c6ff-799f-0d51-12f8-ef651764b245', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '12d1a63d-f3a2-53cb-95ac-1cabb3f868b0', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'b67028f4-d47e-02b0-bf5a-8ee1052c72d6', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:update', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 13:17:51', null, null, null, 'a630c537-4d28-1b24-f9c3-19b7f0507a14', '2015-05-22 13:17:51', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'approve-screen', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '5eec6862-5f8e-5dea-cb92-b09d471faf6c', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:update', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '03633c32-5454-46bd-6405-7bc455495fbd', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'bf085a81-7dc4-71dc-db90-946592880e67', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, '97f489c2-0258-a446-82e7-194b053954af', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:update', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '7f1189a7-a5c3-eed7-8fd5-e400a50f43db', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'b4b96f78-89f6-2fec-20df-ee85d2e1a4ad', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:create', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, 'd2f9b7f3-48d0-1ec6-9d4b-ecd2c8049a86', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'ts$Project.browse', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, 'fd2323f6-d909-7199-9a24-98bdb2d931eb', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, '7ffc9abf-a5fb-3e3a-dbcb-1af8f7b0d234', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:delete', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 13:17:51', null, null, null, '66ca6fd4-5f3d-2ea8-90fa-ad14852364eb', '2015-05-22 13:17:51', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:22:00', null, null, null, 'b5efd698-4bce-ad82-c8ab-75ee789f9dbc', '2015-05-22 11:22:00', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:read', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, 'f5a2085a-8187-ea90-d76c-c05c36bfacc2', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:delete', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '3295f5fe-4dfa-9634-855d-67f74340d4cd', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheets', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '7b78be74-83ec-7bd5-82d7-6fb08684e5ad', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'projects', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 11:20:48', null, null, null, '988cb9a5-7e5c-a079-c48a-d08a3da286aa', '2015-05-22 11:20:48', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:create', 1, 'd85439b8-cbd9-5b5b-974b-96502853b1ab', 1, '2015-05-22 13:17:51', null, null, null, 'ae9f3dac-bda3-38e9-9886-75486dce9297', '2015-05-22 13:17:51', 'admin');

insert into SEC_ROLE
(NAME, LOC_NAME, DESCRIPTION, ROLE_TYPE, IS_DEFAULT_ROLE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Manager', 'Менеджер проектов', null, 0, null, 1, '2015-05-22 10:55:38', null, null, null, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'ts$Project.browse', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, 'daa836c6-f603-eb8c-9c81-7e58affea752', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheet-settings', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, 'ca4e304e-273b-c52c-7c2c-90d26e185dac', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'projects', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, '80c8db05-44a1-e685-cff1-ba2c9bd531bc', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'e39daef7-5055-5d28-0f2a-63fad33fd00f', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'sec$User:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:05:32', null, null, null, '536aec02-4386-aa29-894e-15974f3c93fb', '2015-05-22 11:05:32', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectRole:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '313ef3f5-8977-5e53-f784-82c0908414a6', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:create', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 13:15:43', null, null, null, '1fe1f345-9f7e-2a0e-231a-8d62baf157ce', '2015-05-22 13:15:43', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'tags', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, 'a02f8bf0-281e-6ccf-012c-2d92dadf7be5', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:create', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '1be9cf57-4b00-b100-0867-71f41c724e98', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'approve-screen', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, '234441c4-06a1-706e-fb66-ea605b362c41', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Client:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '89a6b459-1d81-b06e-105a-20f3f301779f', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '7722063a-898a-1488-b753-cc0af3a71891', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '3733e188-1bd3-4df1-0d65-858de70ef917', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '3f9218cd-21ac-dd00-f3e6-e4db7d78959d', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'ts$TaskType.browse', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, 'c50d78d9-5dad-2dbc-d412-4b25e3ce0fbe', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (10, 'timesheets', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 10:55:38', null, null, null, 'd7b8e2d9-2b7e-f8df-a047-9f29954cc220', '2015-05-22 10:55:38', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'd8a5afad-6443-53a4-cc3e-0f08bcca0d57', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TaskType:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '65947162-d8ce-df55-53a9-4ce81f554fff', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:delete', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'c5525073-22cf-d91c-d229-4de6bd184d8f', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '94ffded0-40df-f65a-81e2-a0d2bf9985c0', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:create', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '42c99e5f-2b0d-44e8-afe9-1fc296f1a950', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '3a9a1d9d-f259-7c37-1961-a4e1fbb5507a', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '87f78722-c5d5-5799-8a5b-7b190f7eca6e', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Task:delete', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '8c42de45-09bd-3d57-654a-bda8b97d5eed', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:delete', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 13:15:43', null, null, null, 'c7f96b29-f287-5f5e-fa64-0fcad0759864', '2015-05-22 13:15:43', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Holiday:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'ad62e90e-e05a-dc4c-c6d5-0fb51424cb27', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:create', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '39dbb33e-b86a-982a-6c08-beb98ec29c6a', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TagType:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 13:15:43', null, null, null, '1503d42a-f68c-5fbb-c13a-c5dc884ecd42', '2015-05-22 13:15:43', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:delete', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'a3b8972c-6b2f-5181-7453-2897fc89740b', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$WeeklyReportEntry:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'ff66889a-5e23-cfc4-3867-de2eb7d1eec2', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Project:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '378ade6c-bbc0-de05-fd7b-c27674852845', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:create', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, '934ecd92-322d-99c4-53ce-b529dcef536d', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$Tag:update', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'bc7ca147-9165-724f-84b7-3fe61bf545fd', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$ProjectParticipant:delete', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'ab686e98-1a99-368b-3328-df7c3f6e1224', '2015-05-22 11:04:24', 'admin');

insert into SEC_PERMISSION
(PERMISSION_TYPE, TARGET, VALUE, ROLE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values (20, 'ts$TimeEntry:read', 1, 'd771afba-33c9-caf0-47ed-df2d2b2631f2', 1, '2015-05-22 11:04:24', null, null, null, 'e9dfd076-3fb6-527b-4ee0-61570cbce4b3', '2015-05-22 11:04:24', 'admin');------------------------------------------------------------------------------------------------------------------------

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

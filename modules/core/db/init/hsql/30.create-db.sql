------------------------------------------------------------------------------------------------------------------------

insert into TS_CLIENT
(NAME, CODE, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Haulmont', '001', 1, '2015-04-24 04:45:09', null, null, null, 'f2e1555d-51a7-ea52-4505-fc65c5dde1c6', '2015-04-24 04:45:09', 'admin');

------------------------------------------------------------------------------------------------------------------------

insert into TS_PROJECT_ROLE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Worker', '001', null, 1, '2015-04-24 04:47:10', null, null, null, '6a8e8c24-d639-ef26-fac8-57e13bbfed48', '2015-04-24 04:47:10', 'admin');

insert into TS_PROJECT_ROLE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Manager', '002', null, 1, '2015-04-24 04:47:16', null, null, null, '5bc577ab-44f3-a652-da29-4ae06c02d43b', '2015-04-24 04:47:16', 'admin');

insert into TS_PROJECT_ROLE
(NAME, CODE, DESCRIPTION, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Approver', '003', null, 1, '2015-04-24 04:47:22', null, null, null, '3182a9b0-5ffb-6062-d58c-0148b7b3af3e', '2015-04-24 04:47:22', 'admin');

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

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Backend', '001', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-04-24 08:10:55', null, null, null, '4f532d67-b00e-46c8-4029-73be8f9fb91b', '2015-04-24 08:10:55', 'admin');

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('GUI', '002', null, '9ad8c4a4-cb19-342f-545f-425083ddb811', 1, '2015-04-24 05:03:19', null, null, null, '0df15894-b872-2ce7-7611-19cb0bcc2dac', '2015-04-24 05:03:19', 'admin');

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('GUI', '005', null, '60a91eee-52f4-f0a2-aeb6-1d081cac82a8', 1, '2015-04-24 05:03:40', null, null, null, '3b71215e-4cac-a10a-5fa4-d82efa966f11', '2015-04-24 05:03:40', 'admin');

insert into TS_TAG
(NAME, CODE, DESCRIPTION, TAG_TYPE_ID, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, ID, CREATE_TS, CREATED_BY)
values ('Design', '009', null, null, 1, '2015-04-24 05:05:10', null, null, null, 'aab3ed9c-ee87-8860-c316-0f18e1b2578b', '2015-04-24 05:05:10', 'admin');

------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------------------------------------------------------------

drop index IDX_TS_CLIENT_UNIQ_CODE ;
drop index IDX_TS_PROJECT_UNIQ_CODE ;
drop index IDX_TS_PROJECT_PARTICIPANT_UNIQ_USER_PROJECT ;
drop index IDX_TS_PROJECT_ROLE_UNIQ_CODE ;
drop index IDX_TS_TAG_UNIQ_CODE ;
drop index IDX_TS_TAG_TYPE_UNIQ_CODE ;
drop index IDX_TS_TASK_UNIQ_CODE ;
drop index IDX_TS_TASK_TYPE_UNIQ_CODE ;
create unique index I_IDX_TS_PROJECT_PARTICIPANT_UNIQ_USER_PROJECT on TS_PROJECT_PARTICIPANT (USER_ID, PROJECT_ID) where delete_ts is null^
create unique index I_IDX_TS_PROJECT_UNIQ_CODE on TS_PROJECT (CODE) where delete_ts is null^
create unique index I_IDX_TS_CLIENT_UNIQ_CODE on TS_CLIENT (CODE) where delete_ts is null^
create unique index I_IDX_TS_PROJECT_ROLE_UNIQ_CODE on TS_PROJECT_ROLE (CODE) where delete_ts is null^
create unique index I_IDX_TS_TAG_UNIQ_CODE on TS_TAG (CODE) where delete_ts is null^
create unique index I_IDX_TS_TAG_TYPE_UNIQ_CODE on TS_TAG_TYPE (CODE) where delete_ts is null^
create unique index I_IDX_TS_TASK_UNIQ_CODE on TS_TASK (CODE) where delete_ts is null^
create unique index I_IDX_TS_TASK_TYPE_UNIQ_CODE on TS_TASK_TYPE (CODE) where delete_ts is null^
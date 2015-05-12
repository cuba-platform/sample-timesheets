-- begin TS_TAGalter table TS_TAG add constraint FK_TS_TAG_TAG_TYPE_ID foreign key (TAG_TYPE_ID) references TS_TAG_TYPE(ID)^
create unique index IDX_TS_TAG_UNIQ_CODE on TS_TAG (CODE) ^
create index IDX_TS_TAG_TAG_TYPE on TS_TAG (TAG_TYPE_ID)^
-- end TS_TAG
-- begin TS_TAG_TYPEalter table TS_TAG_TYPE add constraint FK_TS_TAG_TYPE_PROJECT_ID foreign key (PROJECT_ID) references TS_PROJECT(ID)^
create unique index IDX_TS_TAG_TYPE_UNIQ_CODE on TS_TAG_TYPE (CODE) ^
create index IDX_TS_TAG_TYPE_PROJECT on TS_TAG_TYPE (PROJECT_ID)^
-- end TS_TAG_TYPE
-- begin TS_PROJECTalter table TS_PROJECT add constraint FK_TS_PROJECT_PARENT_ID foreign key (PARENT_ID) references TS_PROJECT(ID)^
alter table TS_PROJECT add constraint FK_TS_PROJECT_CLIENT_ID foreign key (CLIENT_ID) references TS_CLIENT(ID)^
create unique index IDX_TS_PROJECT_UNIQ_CODE on TS_PROJECT (CODE) ^
create index IDX_TS_PROJECT_CLIENT on TS_PROJECT (CLIENT_ID)^
create index IDX_TS_PROJECT_PARENT on TS_PROJECT (PARENT_ID)^
-- end TS_PROJECT
-- begin TS_TASKalter table TS_TASK add constraint FK_TS_TASK_PROJECT_ID foreign key (PROJECT_ID) references TS_PROJECT(ID)^
alter table TS_TASK add constraint FK_TS_TASK_TYPE_ID foreign key (TYPE_ID) references TS_TASK_TYPE(ID)^
create unique index IDX_TS_TASK_UNIQ_CODE on TS_TASK (CODE) ^
create index IDX_TS_TASK_PROJECT on TS_TASK (PROJECT_ID)^
create index IDX_TS_TASK_TYPE on TS_TASK (TYPE_ID)^
-- end TS_TASK
-- begin TS_TIME_ENTRYalter table TS_TIME_ENTRY add constraint FK_TS_TIME_ENTRY_TASK_ID foreign key (TASK_ID) references TS_TASK(ID)^
alter table TS_TIME_ENTRY add constraint FK_TS_TIME_ENTRY_USER_ID foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_TS_TIME_ENTRY_USER on TS_TIME_ENTRY (USER_ID)^
create index IDX_TS_TIME_ENTRY_TASK on TS_TIME_ENTRY (TASK_ID)^
-- end TS_TIME_ENTRY
-- begin TS_PROJECT_PARTICIPANTalter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_USER_ID foreign key (USER_ID) references SEC_USER(ID)^
alter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_PROJECT_ID foreign key (PROJECT_ID) references TS_PROJECT(ID)^
alter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_ROLE_ID foreign key (ROLE_ID) references TS_PROJECT_ROLE(ID)^
create unique index IDX_TS_PROJECT_PARTICIPANT_UNIQ_CODE on TS_PROJECT_PARTICIPANT (CODE) ^
create index IDX_TS_PROJECT_PARTICIPANT_USER on TS_PROJECT_PARTICIPANT (USER_ID)^
create index IDX_TS_PROJECT_PARTICIPANT_PROJECT on TS_PROJECT_PARTICIPANT (PROJECT_ID)^
create index IDX_TS_PROJECT_PARTICIPANT_ROLE on TS_PROJECT_PARTICIPANT (ROLE_ID)^
-- end TS_PROJECT_PARTICIPANT
-- begin TS_TASK_PROJECT_PARTICIPANT_LINK
alter table TS_TASK_PROJECT_PARTICIPANT_LINK add constraint FK_TTPPL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_PROJECT_PARTICIPANT_LINK add constraint FK_TTPPL_PROJECT_PARTICIPANT foreign key (PROJECT_PARTICIPANT_ID) references TS_PROJECT_PARTICIPANT (ID)^
-- end TS_TASK_PROJECT_PARTICIPANT_LINK
-- begin TS_TIME_ENTRY_TAG_LINK
alter table TS_TIME_ENTRY_TAG_LINK add constraint FK_TTETL_TIME_ENTRY foreign key (TIME_ENTRY_ID) references TS_TIME_ENTRY (ID)^
alter table TS_TIME_ENTRY_TAG_LINK add constraint FK_TTETL_TAG foreign key (TAG_ID) references TS_TAG (ID)^
-- end TS_TIME_ENTRY_TAG_LINK
-- begin TS_TASK_TAG_TYPE_LINK
alter table TS_TASK_TAG_TYPE_LINK add constraint FK_TTTTL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_TAG_TYPE_LINK add constraint FK_TTTTL_TAG_TYPE foreign key (TAG_TYPE_ID) references TS_TAG_TYPE (ID)^
-- end TS_TASK_TAG_TYPE_LINK
-- begin TS_TASK_TAG_LINK
alter table TS_TASK_TAG_LINK add constraint FK_TTTL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_TAG_LINK add constraint FK_TTTL_TAG foreign key (TAG_ID) references TS_TAG (ID)^
-- end TS_TASK_TAG_LINK
-- begin TS_CLIENT
create unique index IDX_TS_CLIENT_UNIQ_CODE on TS_CLIENT (CODE) ^
-- end TS_CLIENT
-- begin TS_PROJECT_ROLE
create unique index IDX_TS_PROJECT_ROLE_UNIQ_CODE on TS_PROJECT_ROLE (CODE) ^
-- end TS_PROJECT_ROLE
-- begin TS_TASK_TYPE
create unique index IDX_TS_TASK_TYPE_UNIQ_CODE on TS_TASK_TYPE (CODE) ^
-- end TS_TASK_TYPE

-- begin TS_TAG
alter table TS_TAG add constraint FK_TS_TAG_TAG_TYPE_ID foreign key (TAG_TYPE_ID) references TS_TAG_TYPE(ID)^
create index IDX_TS_TAG_TAG_TYPE on TS_TAG (TAG_TYPE_ID)^
-- end TS_TAG
-- begin TS_PROJECT
alter table TS_PROJECT add constraint FK_TS_PROJECT_PARENT_ID foreign key (PARENT_ID) references TS_PROJECT(ID)^
alter table TS_PROJECT add constraint FK_TS_PROJECT_CLIENT_ID foreign key (CLIENT_ID) references TS_CLIENT(ID)^
create index IDX_TS_PROJECT_CLIENT on TS_PROJECT (CLIENT_ID)^
create index IDX_TS_PROJECT_PARENT on TS_PROJECT (PARENT_ID)^
-- end TS_PROJECT
-- begin TS_TASK
alter table TS_TASK add constraint FK_TS_TASK_PROJECT_ID foreign key (PROJECT_ID) references TS_PROJECT(ID)^
alter table TS_TASK add constraint FK_TS_TASK_TYPE_ID foreign key (TYPE_ID) references TS_TASK_TYPE(ID)^
create index IDX_TS_TASK_TYPE on TS_TASK (TYPE_ID)^
create index IDX_TS_TASK_PROJECT on TS_TASK (PROJECT_ID)^
-- end TS_TASK
-- begin TS_TIME_ENTRY
alter table TS_TIME_ENTRY add constraint FK_TS_TIME_ENTRY_TASK_ID foreign key (TASK_ID) references TS_TASK(ID)^
alter table TS_TIME_ENTRY add constraint FK_TS_TIME_ENTRY_USER_ID foreign key (USER_ID) references SEC_USER(ID)^
alter table TS_TIME_ENTRY add constraint FK_TS_TIME_ENTRY_ACTIVITY_TYPE_ID foreign key (ACTIVITY_TYPE_ID) references TS_ACTIVITY_TYPE(ID)^
create index IDX_TS_TIME_ENTRY_ACTIVITY_TYPE on TS_TIME_ENTRY (ACTIVITY_TYPE_ID)^
create index IDX_TS_TIME_ENTRY_USER on TS_TIME_ENTRY (USER_ID)^
create index IDX_TS_TIME_ENTRY_TASK on TS_TIME_ENTRY (TASK_ID)^
-- end TS_TIME_ENTRY
-- begin TS_PROJECT_PARTICIPANT
alter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_USER_ID foreign key (USER_ID) references SEC_USER(ID)^
alter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_PROJECT_ID foreign key (PROJECT_ID) references TS_PROJECT(ID)^
alter table TS_PROJECT_PARTICIPANT add constraint FK_TS_PROJECT_PARTICIPANT_ROLE_ID foreign key (ROLE_ID) references TS_PROJECT_ROLE(ID)^
create index IDX_TS_PROJECT_PARTICIPANT_USER on TS_PROJECT_PARTICIPANT (USER_ID)^
create index IDX_TS_PROJECT_PARTICIPANT_ROLE on TS_PROJECT_PARTICIPANT (ROLE_ID)^
create index IDX_TS_PROJECT_PARTICIPANT_PROJECT on TS_PROJECT_PARTICIPANT (PROJECT_ID)^
-- end TS_PROJECT_PARTICIPANT
-- begin TS_TASK_TAG_TYPE_LINK
alter table TS_TASK_TAG_TYPE_LINK add constraint FK_TTTTL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_TAG_TYPE_LINK add constraint FK_TTTTL_TAG_TYPE foreign key (TAG_TYPE_ID) references TS_TAG_TYPE (ID)^
-- end TS_TASK_TAG_TYPE_LINK
-- begin TS_TAG_TYPE_PROJECT_LINK
alter table TS_TAG_TYPE_PROJECT_LINK add constraint FK_TTTPL_TAG_TYPE foreign key (TAG_TYPE_ID) references TS_TAG_TYPE (ID)^
alter table TS_TAG_TYPE_PROJECT_LINK add constraint FK_TTTPL_PROJECT foreign key (PROJECT_ID) references TS_PROJECT (ID)^
-- end TS_TAG_TYPE_PROJECT_LINK
-- begin TS_TASK_PROJECT_PARTICIPANT_LINK
alter table TS_TASK_PROJECT_PARTICIPANT_LINK add constraint FK_TTPPL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_PROJECT_PARTICIPANT_LINK add constraint FK_TTPPL_PROJECT_PARTICIPANT foreign key (PROJECT_PARTICIPANT_ID) references TS_PROJECT_PARTICIPANT (ID)^
-- end TS_TASK_PROJECT_PARTICIPANT_LINK
-- begin TS_TIME_ENTRY_TAG_LINK
alter table TS_TIME_ENTRY_TAG_LINK add constraint FK_TTETL_TIME_ENTRY foreign key (TIME_ENTRY_ID) references TS_TIME_ENTRY (ID)^
alter table TS_TIME_ENTRY_TAG_LINK add constraint FK_TTETL_TAG foreign key (TAG_ID) references TS_TAG (ID)^
-- end TS_TIME_ENTRY_TAG_LINK
-- begin TS_TASK_TAG_LINK
alter table TS_TASK_TAG_LINK add constraint FK_TTTL_TASK foreign key (TASK_ID) references TS_TASK (ID)^
alter table TS_TASK_TAG_LINK add constraint FK_TTTL_TAG foreign key (TAG_ID) references TS_TAG (ID)^
-- end TS_TASK_TAG_LINK
-- begin TS_ACTIVITY_TYPE_PROJECT_LINK
alter table TS_ACTIVITY_TYPE_PROJECT_LINK add constraint FK_TATPL_ACTIVITY_TYPE foreign key (ACTIVITY_TYPE_ID) references TS_ACTIVITY_TYPE (ID)^
alter table TS_ACTIVITY_TYPE_PROJECT_LINK add constraint FK_TATPL_PROJECT foreign key (PROJECT_ID) references TS_PROJECT (ID)^
-- end TS_ACTIVITY_TYPE_PROJECT_LINK

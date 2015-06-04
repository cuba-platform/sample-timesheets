alter table TS_ACTIVITY_TYPE_PROJECT_LINK add constraint FK_TATPL_ACTIVITY_TYPE foreign key (ACTIVITY_TYPE_ID) references TS_ACTIVITY_TYPE (ID);
alter table TS_ACTIVITY_TYPE_PROJECT_LINK add constraint FK_TATPL_PROJECT foreign key (PROJECT_ID) references TS_PROJECT (ID);

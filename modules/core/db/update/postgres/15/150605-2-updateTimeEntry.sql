alter table TS_TIME_ENTRY add column TIME_IN_MINUTES integer ;
update TS_TIME_ENTRY set TIME_IN_MINUTES = 0 where TIME_IN_MINUTES is null ;
alter table TS_TIME_ENTRY alter column TIME_IN_MINUTES set not null ;
alter table TS_TIME_ENTRY drop column TIME_ cascade ;
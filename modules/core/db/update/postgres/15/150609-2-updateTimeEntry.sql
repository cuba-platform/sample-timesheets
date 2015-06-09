alter table TS_TIME_ENTRY add column TIME_IN_HOURS decimal(10, 2) ;
update TS_TIME_ENTRY set TIME_IN_HOURS = 0 where TIME_IN_HOURS is null ;
alter table TS_TIME_ENTRY alter column TIME_IN_HOURS set not null ;

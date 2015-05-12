alter table SEC_USER add column WORK_HOURS_FOR_WEEK double precision ;
alter table SEC_USER drop column RATE cascade ;

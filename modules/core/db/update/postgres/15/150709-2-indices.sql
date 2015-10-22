create index I_IDX_TS_TIME_ENTRY_USER on TS_TIME_ENTRY (USER_ID) where delete_ts is null^
create index I_IDX_TS_TIME_ENTRY_DATE on TS_TIME_ENTRY (DATE_) where delete_ts is null^
create index I_IDX_TS_HOLIDAY_START on TS_HOLIDAY (START_DATE) where delete_ts is null^
create index I_IDX_TS_HOLIDAY_END on TS_HOLIDAY (END_DATE) where delete_ts is null^
create table TS_ACTIVITY_TYPE_PROJECT_LINK (
    ACTIVITY_TYPE_ID uuid,
    PROJECT_ID uuid,
    primary key (ACTIVITY_TYPE_ID, PROJECT_ID)
);

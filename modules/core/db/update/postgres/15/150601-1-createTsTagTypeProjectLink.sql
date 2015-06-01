create table TS_TAG_TYPE_PROJECT_LINK (
    TAG_TYPE_ID uuid,
    PROJECT_ID uuid,
    primary key (TAG_TYPE_ID, PROJECT_ID)
);

create table indexing_jobs (
    id bigserial primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    base_url varchar(2048) not null,
    status varchar(255) not null,
    description varchar(255),
    started_at timestamp,
    finished_at timestamp
);

create table crawl_seeds (
    id bigserial primary key,
    job_id bigint not null references indexing_jobs(id) on delete cascade,
    type varchar(255) not null,
    value varchar(2048) not null
);

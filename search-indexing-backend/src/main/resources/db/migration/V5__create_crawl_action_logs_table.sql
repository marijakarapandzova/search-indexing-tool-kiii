create table crawl_action_logs (
    id bigserial primary key,
    job_id bigint not null references indexing_jobs(id) on delete cascade,
    action_type varchar(255) not null,
    details text,
    successful boolean not null,
    occurred_at timestamp not null
);

create index idx_crawl_action_logs_job on crawl_action_logs(job_id);

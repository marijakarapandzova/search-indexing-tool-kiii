create table indexed_documents (
    id bigserial primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    job_id bigint not null references indexing_jobs(id) on delete cascade,
    doc_id varchar(255),
    url varchar(2048) not null,
    title varchar(255),
    content text,
    resource_type varchar(255) not null,
    indexed_at timestamp,
    macedonian_confidence double precision,
    version bigint not null
);

create table media_items (
    id bigserial primary key,
    document_id bigint not null references indexed_documents(id) on delete cascade,
    type varchar(255) not null,
    source_url varchar(2048) not null,
    storage_path varchar(2048)
);

create index idx_indexed_documents_job on indexed_documents(job_id);

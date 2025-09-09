create table if not exists tenant (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists project (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id uuid not null references tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists version (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    project_id uuid not null references project(id) on delete restrict on update restrict,
    major bigint not null,
    minor bigint not null,
    patch bigint not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists stage (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id uuid not null references tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

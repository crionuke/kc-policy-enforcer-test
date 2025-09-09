create table if not exists omgtenants_event (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    qualifier text not null,
    resource_id uuid not null,
    deleted boolean not null
);

create table if not exists omgtenants_tenant (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgtenants_project (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id uuid not null references omgtenants_tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgtenants_version (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    project_id uuid not null references omgtenants_project(id) on delete restrict on update restrict,
    major bigint not null,
    minor bigint not null,
    patch bigint not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgtenants_stage (
    id uuid primary key,
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id uuid not null references omgtenants_tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

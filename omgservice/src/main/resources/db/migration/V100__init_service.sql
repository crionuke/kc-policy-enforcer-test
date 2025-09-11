create sequence omgservice_id_sequence start with 1 increment by 50;

create table if not exists omgservice_event (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    qualifier text not null,
    resource_id bigint not null,
    failed boolean not null,
    deleted boolean not null
);

create table if not exists omgservice_tenant (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgservice_project (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id bigint not null references omgservice_tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgservice_version (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    project_id bigint not null references omgservice_project(id) on delete restrict on update restrict,
    major bigint not null,
    minor bigint not null,
    patch bigint not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgservice_stage (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    tenant_id bigint not null references omgservice_tenant(id) on delete restrict on update restrict,
    name text not null,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);

create table if not exists omgservice_deployment (
    id bigint primary key default nextval('omgservice_id_sequence'),
    created timestamp with time zone not null,
    modified timestamp with time zone not null,
    stage_id bigint not null references omgservice_stage(id) on delete restrict on update restrict,
    version_id bigint not null references omgservice_version(id) on delete restrict on update restrict,
    status text not null,
    config jsonb not null,
    deleted boolean not null
);
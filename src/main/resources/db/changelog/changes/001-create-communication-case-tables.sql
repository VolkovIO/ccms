-- liquibase formatted sql

-- changeset volkovio:001-create-communication-case-tables
create table communication_case (
    id uuid primary key,
    customer_full_name varchar(255) not null,
    customer_phone_number varchar(50) not null,
    source_system varchar(100) not null,
    external_order_id varchar(100) not null,
    order_summary varchar(1000) not null,
    contact_reason varchar(100) not null,
    status varchar(100) not null,
    created_by varchar(100) not null,
    opened_at timestamp with time zone not null,
    closed_at timestamp with time zone null
);

create table call_attempt (
    id uuid primary key,
    communication_case_id uuid not null references communication_case(id) on delete cascade,
    attempted_by varchar(100) not null,
    attempted_at timestamp with time zone not null,
    result varchar(50) not null
);

create table message (
    id uuid primary key,
    communication_case_id uuid not null references communication_case(id) on delete cascade,
    direction varchar(50) not null,
    channel varchar(50) not null,
    text varchar(4000) not null,
    delivery_status varchar(50) null,
    created_at timestamp with time zone not null
);

create index idx_communication_case_phone_number
    on communication_case(customer_phone_number);

create index idx_communication_case_customer_full_name
    on communication_case(customer_full_name);

create index idx_communication_case_status
    on communication_case(status);

create index idx_call_attempt_case_id
    on call_attempt(communication_case_id);

create index idx_message_case_id
    on message(communication_case_id);
create table if not exists accounts (
    user_id text primary key,
    balance numeric(19,2) not null,
    version int not null
);

create table if not exists inbox (
    event_id uuid primary key,
    type text not null,
    payload text not null,
    received_at timestamptz not null,
    processed_at timestamptz null
);

create table if not exists payment_transactions (
    id uuid primary key,
    order_id uuid not null unique,
    user_id text not null,
    amount numeric(19,2) not null,
    status text not null,
    created_at timestamptz not null
);

create table if not exists outbox (
    id uuid primary key,
    aggregate_type text not null,
    aggregate_id uuid not null,
    type text not null,
    payload text not null,
    created_at timestamptz not null,
    sent_at timestamptz null
);

create index if not exists idx_outbox_unsent on outbox(sent_at) where sent_at is null;

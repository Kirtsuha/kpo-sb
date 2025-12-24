create table if not exists orders (
    id uuid primary key,
    user_id text not null,
    amount numeric(19,2) not null,
    description text,
    status text not null,
    created_at timestamptz not null
);

create index if not exists idx_orders_user on orders(user_id);

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

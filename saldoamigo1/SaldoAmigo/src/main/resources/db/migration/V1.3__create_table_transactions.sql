create table if not exists transactions (
    id serial not null,
    value numeric not null,
    date date not null,
    account_id bigint not null,
    group_id bigint not null,
    constraint pk_transactions primary key (id),
    constraint fk_transaction_accounts foreign key (account_id) references accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint fk_transaction_groups foreign key (group_id) references groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

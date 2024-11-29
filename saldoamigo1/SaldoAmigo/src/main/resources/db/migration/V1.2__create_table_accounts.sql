create table if not exists accounts (
    id bigint not null,
    name varchar(100) not null,
    pix_key varchar(100) not null,
    city varchar(100) not null,
    user_id bigint not null,
    constraint pk_accounts primary key (id),
    constraint fk_account_users foreign key (user_id) references users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

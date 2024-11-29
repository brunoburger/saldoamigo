create table if not exists groups (
    id serial not null,
    name varchar(100) not null,
    description text,
    user_id bigint not null,
    constraint pk_groups primary key (id),
    constraint fk_groups_users foreign key (user_id) references users(id) ON DELETE CASCADE ON UPDATE CASCADE
);
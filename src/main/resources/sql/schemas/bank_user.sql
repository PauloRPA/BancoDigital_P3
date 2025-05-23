create table if not exists bank_user (
	id bigserial not null unique,
  	username varchar not null unique,
  	email varchar not null unique,
  	password  varchar not null unique,
	roles varchar[] not null,

	constraint bank_user_pk primary key (id)
);
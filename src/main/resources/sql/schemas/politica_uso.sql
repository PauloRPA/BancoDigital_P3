create table if not exists politica_uso (
	id serial not null unique,
	limite_credito numeric(38,2) not null,
	limite_diario_uso numeric(38,2) not null,
	constraint politica_uso_pk primary key (id)
);
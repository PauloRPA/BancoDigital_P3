create collation if not exists ignore_accent_case (provider = icu, deterministic = false, locale = 'und-u-ks-level1');

create table if not exists tier (
	id serial not null unique,
	nome varchar not null unique collate ignore_accent_case,
	constraint tier_pk primary key (id)
);

alter table tier add column if not exists politica_uso_fk bigint constraint politica_uso_fk references politica_uso(id);

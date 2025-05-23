create collation if not exists ignore_accent_case (provider = icu, deterministic = false, locale = 'und-u-ks-level1');

create table if not exists cliente (
	id bigserial not null unique,
    nome varchar not null collate ignore_accent_case,
    cpf varchar not null unique collate ignore_accent_case,
    data_nascimento date not null,

	constraint cliente_pk primary key (id)
);

alter table cliente add column if not exists endereco_fk bigint constraint endereco_fk references endereco(id);
alter table cliente add column if not exists tier_fk bigint constraint tier_fk references tier(id);

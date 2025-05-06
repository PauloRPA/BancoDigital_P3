create collation if not exists ignore_accent_case (provider = icu, deterministic = false, locale = 'und-u-ks-level1');

create table if not exists endereco (
	id serial not null unique,
    cep varchar not null collate ignore_accent_case,
    complemento varchar not null collate ignore_accent_case,
    numero integer not null,
    rua varchar not null collate ignore_accent_case,
    bairro varchar not null collate ignore_accent_case,
    cidade varchar not null collate ignore_accent_case,
    estado varchar not null collate ignore_accent_case,

	constraint endereco_pk primary key (id),
    constraint unique_endereco unique (cep, complemento, numero, rua, bairro, cidade, estado)
);

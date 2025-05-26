create table if not exists fatura (
	id serial not null unique,
	abertura date not null,
	fechamento date not null,
	valor numeric(38,2) not null,
    taxa_utilizacao_cobrada boolean not null,
    pago boolean not null,

	constraint fatura_pk primary key (id)
);

alter table fatura add column if not exists cartao_fk bigint constraint fatura_cartao_fk references cartao(id);

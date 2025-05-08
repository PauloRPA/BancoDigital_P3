-- Cria tipos enumerados no banco de dados caso não existam para os enums java TipoCartao
do
 '
begin
	if not exists (select 1 from pg_type where typname = ''tipo_cartao'') then
        create type tipo_cartao as enum (''CARTAO_CREDITO'', ''CARTAO_DEBITO'');
    end if;
end;
';

-- Cria função para fazer o parsing das strings java para o tipo tipo_cartao definido acima
create or replace function cast_varchar_tipo_cartao(varchar) returns tipo_cartao as '
    select case $1
        when ''CARTAO_CREDITO'' then ''CARTAO_CREDITO''::tipo_cartao
        when ''CARTAO_DEBITO'' then ''CARTAO_DEBITO''::tipo_cartao
    end;
' language sql;

-- Cria casting automático para atribuições feitas via string para o valores tipo_cartao
do
'
begin
    if not exists (
		select 1 from pg_cast c
		inner join pg_type t
		on t.oid = c.casttarget
		where t.typname = ''tipo_cartao''
	) then
        CREATE CAST (varchar AS tipo_cartao) WITH FUNCTION cast_varchar_tipo_cartao(varchar) AS ASSIGNMENT;
    end if;
end;
';

-- Cria tabela cartao
create table if not exists cartao (
	id serial not null unique,
	numero varchar not null,
	vencimento date not null,
	ccv varchar not null,
    ativo boolean not null,
    senha varchar not null,
    limite_credito numeric(38,2) not null,
    limite_diario numeric(38,2) not null,
	tipo tipo_cartao not null,

	constraint cartao_pk primary key (id),
    constraint unique_cartao_numero_vencimento_ccv unique (numero,vencimento,ccv)
);

alter table cartao add column if not exists conta_fk bigint constraint cartao_conta_fk references conta(id);
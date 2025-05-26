-- Cria tipos enumerados no banco de dados caso não existam para os enums java TipoConta
do
 '
begin
	if not exists (select 1 from pg_type where typname = ''tipo_conta'') then
        create type tipo_conta as enum (''CONTA_CORRENTE'', ''CONTA_POUPANCA'');
    end if;
end;
';

-- Cria função para fazer o parsing das strings java para o tipo tipo_conta definido acima
create or replace function cast_varchar_tipo_conta(varchar) returns tipo_conta as '
    select case $1
        when ''CONTA_CORRENTE'' then ''CONTA_CORRENTE''::tipo_conta
        when ''CONTA_POUPANCA'' then ''CONTA_POUPANCA''::tipo_conta
    end;
' language sql;

-- Cria casting automático para atribuições feitas via string para o valores tipo_conta
do
'
begin
    if not exists (
		select 1 from pg_cast c
		inner join pg_type t
		on t.oid = c.casttarget
		where t.typname = ''tipo_conta''
	) then
        CREATE CAST (varchar AS tipo_conta) WITH FUNCTION cast_varchar_tipo_conta(varchar) AS ASSIGNMENT;
    end if;
end;
';

-- Cria tabela conta
create table if not exists conta (
	id bigserial not null unique,
  	agencia varchar not null,
	numero varchar not null,
	saldo numeric(38,2) not null,
	tipo tipo_conta not null,

	constraint conta_pk primary key (id),
    constraint unique_conta_agencia_numero unique (agencia, numero)
);

alter table conta add column if not exists cliente_fk bigint constraint conta_cliente_fk references cliente(id);
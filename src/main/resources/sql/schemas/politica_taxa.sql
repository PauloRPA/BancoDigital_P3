-- Cria tipos enumerados no banco de dados caso não existam para os enums java tipo_taxa e unidade_taxa
do
 '
begin
    if not exists (select 1 from pg_type where typname = ''tipo_taxa'') then
        create type tipo_taxa as enum (''MANUTENCAO'', ''RENDIMENTO'');
    end if;
	if not exists (select 1 from pg_type where typname = ''unidade_quantia'') then
        create type unidade_quantia as enum (''FIXO'', ''PORCENTAGEM'');
    end if;
end;
';

-- Cria função para fazer o parsing das strings java para o tipo tipo_taxa definido acima
create or replace function cast_varchar_tipo_taxa(varchar) returns tipo_taxa as '
    select case $1
        when ''MANUTENCAO'' then ''MANUTENCAO''::tipo_taxa
        when ''RENDIMENTO'' then ''RENDIMENTO''::tipo_taxa
    end;
' language sql;

-- Cria função para fazer o parsing das strings java para o tipo unidade_quantia definido acima
create or replace function cast_varchar_unidade_quantia(varchar) returns unidade_quantia as '
    select case $1
        when ''FIXO'' then ''FIXO''::unidade_quantia
        when ''PORCENTAGEM'' then ''PORCENTAGEM''::unidade_quantia
    end;
' language sql;

-- Cria casting automático para atribuições feitas via string para o valores tipo_taxa
do
'
begin
    if not exists (
		select 1 from pg_cast c
		inner join pg_type t
		on t.oid = c.casttarget
		where t.typname = ''tipo_taxa''
	) then
        CREATE CAST (varchar AS tipo_taxa) WITH FUNCTION cast_varchar_tipo_taxa(varchar) AS ASSIGNMENT;
    end if;
end;
';

-- Cria casting automático para atribuições feitas via string para o valores unidade_taxa
do
'
begin
    if not exists (
		select 1 from pg_cast c
		inner join pg_type t
		on t.oid = c.casttarget
		where t.typname = ''unidade_quantia''
	) then
        CREATE CAST (varchar AS unidade_quantia) WITH FUNCTION cast_varchar_unidade_quantia(varchar) AS ASSIGNMENT;
    end if;
end;
';

-- Cria tabela politica_taxa
create table if not exists politica_taxa (
	id bigserial not null unique,
	nome varchar not null unique,
	quantidade numeric(38,2) not null,
	tipo_taxa tipo_taxa not null,
	unidade_quantia unidade_quantia not null,
	constraint politica_taxa_pk primary key (id)
);
create or replace function public.cliente_find_by_nome_v1(
    p_nome CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cliente 
	where cliente.nome = p_nome;
END;
$BODY$;

create or replace function public.cliente_find_by_cpf_v1(
    p_cpf CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cliente 
	where cliente.cpf = p_cpf;
END;
$BODY$;

create or replace function public.cliente_find_by_tier_v1(
    p_tier_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cliente 
	where cliente.tier_fk = p_tier_fk;
END;
$BODY$;

create or replace function public.cliente_find_by_nome_and_data_nascimento_v1(
    p_nome CHARACTER VARYING(255),
    p_data_nascimento DATE
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cliente 
	where cliente.nome = p_nome and cliente.data_nascimento = p_data_nascimento;
END;
$BODY$;

create or replace function public.cliente_insert_v1(
    p_nome CHARACTER VARYING(255),
    p_cpf CHARACTER VARYING(255),
    p_data_nascimento DATE,
    p_endereco_fk BIGINT,
    p_tier_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into cliente (nome, cpf, data_nascimento, endereco_fk, tier_fk)
    values (p_nome, p_cpf, p_data_nascimento, p_endereco_fk, p_tier_fk)
    RETURNING cliente.*;
END;
$BODY$;

create or replace function public.cliente_update_v1(
    p_nome CHARACTER VARYING(255),
    p_cpf CHARACTER VARYING(255),
    p_data_nascimento DATE,
    p_endereco_fk BIGINT,
    p_tier_fk BIGINT,
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    cpf CHARACTER VARYING(255),
    data_nascimento DATE,
    endereco_fk BIGINT,
    tier_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update cliente
    set nome = p_nome, cpf = p_cpf, data_nascimento = p_data_nascimento, endereco_fk = p_endereco_fk, tier_fk = p_tier_fk
    where cliente.id = p_id
    RETURNING cliente.*;
END;
$BODY$;

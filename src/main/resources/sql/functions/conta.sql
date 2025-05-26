create or replace function public.conta_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta;
END;
$BODY$;


create or replace function public.conta_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.conta_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta where conta.id = p_id;
END;
$BODY$;


create or replace function public.conta_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from conta where conta.id = p_id;
END;
$BODY$;



create or replace function public.conta_find_by_agencia_v1(
    p_agencia CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta c 
	where c.agencia = p_agencia;
END;
$BODY$;

create or replace function public.conta_find_by_numero_v1(
    p_numero CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta c 
	where c.numero = p_numero;
END;
$BODY$;

create or replace function public.conta_find_by_cliente_v1(
    p_cliente_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta c 
	where c.cliente_fk = p_cliente_fk;
END;
$BODY$;

create or replace function public.conta_find_by_numero_and_agencia_v1(
    p_numero CHARACTER VARYING(255),
    p_agencia CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from conta c 
	where c.numero = p_numero and c.agencia = p_agencia;
END;
$BODY$;

create or replace function public.conta_insert_v1(
    p_agencia CHARACTER VARYING(255),
    p_numero CHARACTER VARYING(255),
    p_saldo NUMERIC(38,2),
    p_tipo CHARACTER VARYING(255),
    p_cliente_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into conta (agencia, numero, saldo, tipo, cliente_fk) 
	values (p_agencia, p_numero, p_saldo, p_tipo, p_cliente_fk)
    RETURNING conta.*;
END;
$BODY$;

create or replace function public.conta_update_v1(
    p_agencia CHARACTER VARYING(255),
    p_numero CHARACTER VARYING(255),
    p_saldo NUMERIC(38,2),
    p_tipo CHARACTER VARYING(255),
    p_cliente_fk BIGINT,
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    agencia CHARACTER VARYING(255),
    numero CHARACTER VARYING(255),
    saldo NUMERIC(38,2),
    tipo tipo_conta,
    cliente_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update conta
    set agencia = p_agencia, numero = p_numero, saldo = p_saldo, tipo = p_tipo, cliente_fk = p_cliente_fk
    where conta.id = p_id
    RETURNING conta.*;
END;
$BODY$;


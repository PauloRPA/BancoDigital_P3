create or replace function public.cartao_find_by_numero_v1(
    p_numero CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    numero CHARACTER VARYING(255),
    vencimento DATE,
    ccv CHARACTER VARYING(255),
    ativo BOOLEAN,
    senha CHARACTER VARYING(255),
    limite_credito NUMERIC(38,2),
    limite_diario NUMERIC(38,2),
    tipo tipo_cartao,
    conta_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cartao c 
	where c.numero = p_numero;
END;
$BODY$;

create or replace function public.cartao_find_by_conta_v1(
    p_conta_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    numero CHARACTER VARYING(255),
    vencimento DATE,
    ccv CHARACTER VARYING(255),
    ativo BOOLEAN,
    senha CHARACTER VARYING(255),
    limite_credito NUMERIC(38,2),
    limite_diario NUMERIC(38,2),
    tipo tipo_cartao,
    conta_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from cartao c 
	where c.conta_fk = p_conta_fk;
END;
$BODY$;

create or replace function public.cartao_insert_v1(
    p_numero CHARACTER VARYING(255),
    p_vencimento DATE,
    p_ccv CHARACTER VARYING(255),
    p_ativo BOOLEAN,
    p_senha CHARACTER VARYING(255),
    p_limite_credito NUMERIC(38,2),
    p_limite_diario NUMERIC(38,2),
    p_tipo CHARACTER VARYING(255),
    p_conta_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    numero CHARACTER VARYING(255),
    vencimento DATE,
    ccv CHARACTER VARYING(255),
    ativo BOOLEAN,
    senha CHARACTER VARYING(255),
    limite_credito NUMERIC(38,2),
    limite_diario NUMERIC(38,2),
    tipo tipo_cartao,
    conta_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into cartao (numero, vencimento, ccv, ativo, senha, limite_credito, limite_diario, tipo, conta_fk)
    values (p_numero, p_vencimento, p_ccv, p_ativo, p_senha, p_limite_credito, p_limite_diario, p_tipo, p_conta_fk)
    RETURNING cartao.*;
END;
$BODY$;

create or replace function public.cartao_update_v1(
    p_numero CHARACTER VARYING(255),
    p_vencimento DATE,
    p_ccv CHARACTER VARYING(255),
    p_ativo BOOLEAN,
    p_senha CHARACTER VARYING(255),
    p_tipo CHARACTER VARYING(255),
    p_conta_fk BIGINT,
    p_limite_credito NUMERIC(38,2),
    p_limite_diario NUMERIC(38,2),
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    numero CHARACTER VARYING(255),
    vencimento DATE,
    ccv CHARACTER VARYING(255),
    ativo BOOLEAN,
    senha CHARACTER VARYING(255),
    limite_credito NUMERIC(38,2),
    limite_diario NUMERIC(38,2),
    tipo tipo_cartao,
    conta_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update cartao
    set numero = p_numero, vencimento = p_vencimento, ccv = p_ccv, ativo = p_ativo, senha = p_senha, tipo = p_tipo, conta_fk = p_conta_fk, limite_credito = p_limite_credito, limite_diario = p_limite_diario
    where cartao.id = p_id
    RETURNING cartao.*;
END;
$BODY$;


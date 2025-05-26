create or replace function public.fatura_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from fatura;
END;
$BODY$;


create or replace function public.fatura_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from fatura offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.fatura_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from fatura where fatura.id = p_id;
END;
$BODY$;


create or replace function public.fatura_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from fatura where fatura.id = p_id;
END;
$BODY$;



create or replace function public.fatura_find_by_cartao_v1(
    p_cartao_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from fatura f 
	where f.cartao_fk = p_cartao_fk;
END;
$BODY$;

create or replace function public.fatura_insert_v1(
    p_abertura DATE,
    p_fechamento DATE,
    p_valor NUMERIC(38,2),
    p_taxa_utilizacao_cobrada BOOLEAN,
    p_pago BOOLEAN,
    p_cartao_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into fatura (abertura, fechamento, valor, taxa_utilizacao_cobrada, pago, cartao_fk)
    values (p_abertura, p_fechamento, p_valor, p_taxa_utilizacao_cobrada, p_pago, p_cartao_fk)
    RETURNING fatura.*;
END;
$BODY$;

create or replace function public.fatura_update_v1(
    p_abertura DATE,
    p_fechamento DATE,
    p_valor NUMERIC(38,2),
    p_taxa_utilizacao_cobrada BOOLEAN,
    p_pago BOOLEAN,
    p_cartao_fk BIGINT,
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    abertura DATE,
    fechamento DATE,
    valor NUMERIC(38,2),
    taxa_utilizacao_cobrada BOOLEAN,
    pago BOOLEAN,
    cartao_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update fatura
    set abertura = p_abertura, fechamento = p_fechamento, valor = p_valor, taxa_utilizacao_cobrada = p_taxa_utilizacao_cobrada, pago = p_pago, cartao_fk = p_cartao_fk
    where fatura.id = p_id
    RETURNING fatura.*;
END;
$BODY$;


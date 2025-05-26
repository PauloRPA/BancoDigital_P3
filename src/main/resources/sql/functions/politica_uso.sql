create or replace function public.politica_uso_insert_v1(
    p_limite_credito NUMERIC(38,2),
    p_limite_diario_uso NUMERIC(38,2)
)
RETURNS TABLE(
    id BIGINT,
    limite_credito NUMERIC(38,2),
    limite_diario_uso NUMERIC(38,2)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into politica_uso (limite_credito, limite_diario_uso) 
	values (p_limite_credito, p_limite_diario_uso)
    RETURNING politica_uso.*;
END;
$BODY$;

create or replace function public.politica_uso_update_v1(
    p_limite_credito NUMERIC(38,2),
    p_limite_diario_uso NUMERIC(38,2),
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    limite_credito NUMERIC(38,2),
    limite_diario_uso NUMERIC(38,2)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update politica_uso
    set limite_credito = p_limiteCredito, limite_diario_uso = p_limiteDiario
    where politica_uso.id = p_id
    RETURNING politica_uso.*;
END;
$BODY$;

create or replace function public.politica_uso_find_by_tier_id_v1(
    p_tier_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    limite_credito NUMERIC(38,2),
    limite_diario_uso NUMERIC(38,2)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select p.* from politica_uso p inner join tier t on p.id = t.politica_uso_fk where t.id = p_tier_fk;
END;
$BODY$;


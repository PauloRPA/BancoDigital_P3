create or replace function public.politica_taxa_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from politica_taxa;
END;
$BODY$;


create or replace function public.politica_taxa_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from politica_taxa offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.politica_taxa_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from politica_taxa where politica_taxa.id = p_id;
END;
$BODY$;


create or replace function public.politica_taxa_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from politica_taxa where politica_taxa.id = p_id;
END;
$BODY$;



create or replace function public.politica_taxa_find_by_nome_v1(
    p_nome CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from politica_taxa 
	where politica_taxa.nome = p_nome;
END;
$BODY$;

create or replace function public.politica_taxa_find_by_tier_id_v1(
    p_tier_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select p.* from politica_taxa p
    inner join politica_taxa_join_tier j on p.id = j.politica_taxa_id_fk
    inner join tier t on t.id = j.tier_id_fk
    where t.id = p_tier_fk;
END;
$BODY$;

create or replace function public.politica_taxa_insert_v1(
    p_nome CHARACTER VARYING(255),
    p_quantidade NUMERIC(38,2),
    p_tipo_taxa CHARACTER VARYING(255),
    p_unidade_quantia CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into politica_taxa (nome, quantidade, tipo_taxa, unidade_quantia) 
	values (p_nome, p_quantidade, p_tipo_taxa, p_unidade_quantia)
    RETURNING politica_taxa.*;
END;
$BODY$;

create or replace function public.politica_taxa_update_v1(
    p_nome CHARACTER VARYING(255),
    p_quantidade NUMERIC(38,2),
    p_tipo_taxa tipo_taxa,
    p_unidade_quantia unidade_quantia,
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    quantidade NUMERIC(38,2),
    tipo_taxa tipo_taxa,
    unidade_quantia unidade_quantia
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update politica_taxa
    set nome = p_nome, quantidade = p_quantidade, tipo_taxa = p_tipo_taxa, unidade_quantia = p_unidade_quantia
    where politica_taxa.id = p_id
    RETURNING politica_taxa.*;
END;
$BODY$;


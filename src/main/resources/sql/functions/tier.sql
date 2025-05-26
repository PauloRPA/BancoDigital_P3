create or replace function public.tier_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from tier;
END;
$BODY$;


create or replace function public.tier_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from tier offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.tier_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from tier where tier.id = p_id;
END;
$BODY$;


create or replace function public.tier_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from tier where tier.id = p_id;
END;
$BODY$;



create or replace function public.tier_find_by_nome_v1(
    p_nome CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from tier 
	where tier.nome = p_nome;
END;
$BODY$;

create or replace function public.tier_insert_v1(
    p_nome CHARACTER VARYING(255),
    p_politica_uso_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into tier (nome, politica_uso_fk) 
	values (p_nome, p_politica_uso_fk)
    RETURNING tier.*;
END;
$BODY$;

create or replace function public.tier_update_v1(
    p_nome CHARACTER VARYING(255),
    p_politica_uso_fk BIGINT,
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update tier
    set nome = p_nome, politica_uso_fk = p_politica_uso_fk
    where tier.id = p_id
    RETURNING tier.*;
END;
$BODY$;

create or replace function public.tier_find_by_politica_uso_id_v1(
    p_politica_uso_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select t.* from tier t
    inner join politica_uso p on p.id = t.politica_uso_fk
    where p.id = p_politica_uso_fk;
END;
$BODY$;

create or replace function public.tier_find_by_politica_taxa_id_v1(
    p_politica_taxa_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    nome CHARACTER VARYING(255),
    politica_uso_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select t.* from politica_taxa p
    inner join politica_taxa_join_tier j on p.id = j.politica_taxa_id_fk
    inner join tier t on t.id = j.tier_id_fk
    where p.id = p_politica_taxa_fk;     
END;
$BODY$;

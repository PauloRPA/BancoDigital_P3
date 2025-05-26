create or replace function public.politica_taxa_join_tier_insert_v1(
    p_tier_fk BIGINT,
    p_politica_taxa_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    tier_fk BIGINT,
    politica_taxa_fk BIGINT
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into politica_taxa_join_tier (tier_id_fk, politica_taxa_id_fk)
    values (p_tier_fk, p_politica_taxa_fk)
    RETURNING politica_taxa_join_tier.*;
END;
$BODY$;

create or replace function public.politica_taxa_join_tier_delete_by_politica_taxa_id_v1(
    p_politica_taxa_fk BIGINT
)
RETURNS VOID
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from politica_taxa_join_tier j
    where j.politica_taxa_id_fk = p_politica_taxa_fk;
END;
$BODY$;

create or replace function public.politica_taxa_join_tier_delete_by_tier_id_v1(
    p_tier_fk BIGINT
)
RETURNS VOID
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from politica_taxa_join_tier j
    where j.tier_id_fk = p_tier_fk;
END;
$BODY$;

create or replace function public.politica_taxa_join_tier_find_by_tier_id_v1(
    p_tier_fk BIGINT
)
RETURNS TABLE(
    id BIGINT,
    tier_fk BIGINT,
    politica_taxa_fk BIGINT
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


create or replace function public.endereco_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from endereco;
END;
$BODY$;


create or replace function public.endereco_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from endereco offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.endereco_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from endereco where endereco.id = p_id;
END;
$BODY$;


create or replace function public.endereco_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from endereco where endereco.id = p_id;
END;
$BODY$;



create or replace function public.endereco_insert_v1(
    p_cep CHARACTER VARYING(255),
    p_complemento CHARACTER VARYING(255),
    p_numero INTEGER,
    p_rua CHARACTER VARYING(255),
    p_bairro CHARACTER VARYING(255),
    p_cidade CHARACTER VARYING(255),
    p_estado CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into endereco (cep, complemento, numero, rua, bairro, cidade, estado)
    values (p_cep, p_complemento, p_numero, p_rua, p_bairro, p_cidade, p_estado)
    RETURNING endereco.*;
END;
$BODY$;

create or replace function public.endereco_update_v1(
    p_cep CHARACTER VARYING(255),
    p_complemento CHARACTER VARYING(255),
    p_numero INTEGER,
    p_rua CHARACTER VARYING(255),
    p_bairro CHARACTER VARYING(255),
    p_cidade CHARACTER VARYING(255),
    p_estado CHARACTER VARYING(255),
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY update endereco
    set cep = p_cep, complemento = p_complemento, numero = p_numero, rua = p_rua, bairro = p_bairro, cidade = p_cidade, estado = p_estado
    where endereco.id = p_id
    RETURNING endereco.*;
END;
$BODY$;

create or replace function public.endereco_select_by_endereco_v1(
    p_cep CHARACTER VARYING(255),
    p_complemento CHARACTER VARYING(255),
    p_numero INTEGER,
    p_rua CHARACTER VARYING(255),
    p_bairro CHARACTER VARYING(255),
    p_cidade CHARACTER VARYING(255),
    p_estado CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    cep CHARACTER VARYING(255),
    complemento CHARACTER VARYING(255),
    numero INTEGER,
    rua CHARACTER VARYING(255),
    bairro CHARACTER VARYING(255),
    cidade CHARACTER VARYING(255),
    estado CHARACTER VARYING(255)
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from endereco e
    where e.cep = p_cep
    and e.complemento = p_complemento
    and e.numero = p_numero
    and e.rua = p_rua
    and e.bairro = p_bairro
    and e.cidade = p_cidade
    and e.estado = p_estado;
END;
$BODY$;



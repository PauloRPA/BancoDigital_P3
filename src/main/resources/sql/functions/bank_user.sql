create or replace function public.bank_user_find_all_v1()
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from bank_user;
END;
$BODY$;


create or replace function public.bank_user_find_all_pageable_v1(
    p_offset BIGINT, 
    p_size BIGINT
)
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from bank_user offset p_offset limit p_size;
END;
$BODY$;


create or replace function public.bank_user_find_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from bank_user where bank_user.id = p_id;
END;
$BODY$;


create or replace function public.bank_user_delete_by_id_v1(
    p_id BIGINT
)
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    delete from bank_user where bank_user.id = p_id;
END;
$BODY$;



create or replace function public.bank_user_find_by_username_v1(
    p_username CHARACTER VARYING(255)
)
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY select * from bank_user 
	where bank_user.username = p_username;
END;
$BODY$;

create or replace function public.bank_user_insert_v1(
    p_username CHARACTER VARYING(255),
    p_email CHARACTER VARYING(255),
    p_password CHARACTER VARYING(255),
    p_roles CHARACTER VARYING(255)[]
)
RETURNS TABLE(
    id BIGINT,
    username CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    password CHARACTER VARYING(255), 
    roles CHARACTER VARYING(255)[]
)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    RETURN QUERY insert into bank_user (username, email, password, roles) 
	values (p_username, p_email, p_password, p_roles)
    RETURNING bank_user.*;
END;
$BODY$;


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


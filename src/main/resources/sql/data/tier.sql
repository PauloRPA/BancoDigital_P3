insert into tier (id, nome, politica_uso_fk)
values (DEFAULT, 'COMUM', 1)
on conflict do nothing;

insert into tier (id, nome, politica_uso_fk)
values (DEFAULT, 'SUPER', 2)
on conflict do nothing;

insert into tier (id, nome, politica_uso_fk)
values (DEFAULT, 'PREMIUM', 3)
on conflict do nothing;
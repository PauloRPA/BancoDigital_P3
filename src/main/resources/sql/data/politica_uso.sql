insert into politica_uso (id, limite_credito, limite_diario_uso)
values (default, 1000.00, -1.00)
on conflict do nothing;

insert into politica_uso (id, limite_credito, limite_diario_uso)
values (default, 5000.00, -1.00)
on conflict do nothing;

insert into politica_uso (id, limite_credito, limite_diario_uso)
values (default, 10000.00, -1.00)
on conflict do nothing;

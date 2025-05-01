insert into politica_uso (id, limite_credito, limite_diario_uso)
values (1, 1000.00, -1.00)
on conflict do nothing;

insert into politica_uso (id, limite_credito, limite_diario_uso)
values (2, 5000.00, -1.00)
on conflict do nothing;

insert into politica_uso (id, limite_credito, limite_diario_uso)
values (3, 10000.00, -1.00)
on conflict do nothing;

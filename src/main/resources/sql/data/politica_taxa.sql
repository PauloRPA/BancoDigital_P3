insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (1, 'Manutenção conta corrente para clientes SUPER', 8.00, 'MANUTENCAO', 'FIXO')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (2, 'Manutenção conta corrente para clientes COMUM', 12.00, 'MANUTENCAO', 'FIXO')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (3, 'Rendimento conta poupança para clientes PREMIUM', 0.90, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (4, 'Rendimento conta poupança para clientes SUPER', 0.70, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (5, 'Rendimento conta poupança para clientes COMUM', 0.50, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

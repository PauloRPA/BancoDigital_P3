insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (default, 'Manutenção conta corrente para clientes SUPER', 8.00, 'MANUTENCAO', 'FIXO')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (default, 'Manutenção conta corrente para clientes COMUM', 12.00, 'MANUTENCAO', 'FIXO')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (default, 'Rendimento conta poupança para clientes PREMIUM', 0.90, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (default, 'Rendimento conta poupança para clientes SUPER', 0.70, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

insert into politica_taxa (id, nome, quantidade, tipo_taxa, unidade_quantia)
values (default, 'Rendimento conta poupança para clientes COMUM', 0.50, 'RENDIMENTO', 'PORCENTAGEM')
on conflict do nothing;

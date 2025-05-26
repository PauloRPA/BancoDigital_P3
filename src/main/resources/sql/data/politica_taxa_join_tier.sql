insert into politica_taxa_join_tier (politica_taxa_id_fk, tier_id_fk) values (1, 2) on conflict do nothing;
insert into politica_taxa_join_tier (politica_taxa_id_fk, tier_id_fk) values (2, 1) on conflict do nothing;
insert into politica_taxa_join_tier (politica_taxa_id_fk, tier_id_fk) values (3, 3) on conflict do nothing;
insert into politica_taxa_join_tier (politica_taxa_id_fk, tier_id_fk) values (4, 2) on conflict do nothing;
insert into politica_taxa_join_tier (politica_taxa_id_fk, tier_id_fk) values (5, 1) on conflict do nothing;
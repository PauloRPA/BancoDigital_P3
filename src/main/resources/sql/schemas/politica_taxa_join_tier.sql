create table if not exists politica_taxa_join_tier (
	id serial not null unique,
    tier_id_fk bigint constraint tier_id_fk references tier(id),
    politica_taxa_id_fk bigint constraint politica_taxa_id_fk references politica_taxa(id),
	constraint politica_taxa_join_tier_pk primary key (id),
    constraint unique_tier_politica_taxa_fk unique (tier_id_fk, politica_taxa_id_fk)
);
create table if not exists politica_uso (
	id bigserial not null unique,
	limite_credito numeric(38,2) not null,
	limite_diario_uso numeric(38,2) not null,
	constraint politica_uso_pk primary key (id),
	constraint unique_limites_combo unique (limite_credito, limite_diario_uso)
);
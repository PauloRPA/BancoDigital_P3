create table if not exists politica_uso (
	id int primary key auto_increment,
	limite_credito numeric(38,2) not null,
	limite_diario_uso numeric(38,2) not null
);
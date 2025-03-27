package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNome(String nome);

    boolean existsByCpf(String cpf);

}

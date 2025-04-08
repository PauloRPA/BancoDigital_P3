package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNome(String nome);

    boolean existsByCpf(String cpf);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByNomeAndDataNascimento(String nome, LocalDate dataNascimento);

}

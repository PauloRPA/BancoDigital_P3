package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

//    List<ContaBancaria> findByCliente(Cliente cliente);

    boolean existsByNumero(String accountNumber);

    boolean existsByAgencia(String agencyNumber);

    Optional<ContaBancaria> findByNumeroAndAgencia(String numero, String agencia);

}

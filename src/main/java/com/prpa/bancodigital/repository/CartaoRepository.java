package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

  List<Cartao> findByConta(ContaBancaria conta);

  boolean existsByNumero(String number);

}
package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.ContaPoupanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Long> {

}

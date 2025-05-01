package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaTaxa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticaTaxaRepository extends JpaRepository<PoliticaTaxa, Long> {

    boolean existsByNome(String nome);

//    List<PoliticaTaxa> findByTiers(Tier tier);

//    boolean existsByTiers_NomeIgnoreCase(String nome);
}
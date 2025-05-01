package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaUso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticaUsoRepository extends JpaRepository<PoliticaUso, Long> {

//    boolean existsByTiers_NomeIgnoreCase(String requiredTier);

}
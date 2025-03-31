package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TierRepository extends JpaRepository<Tier, Long> {

    boolean existsByNome(String nome);
}

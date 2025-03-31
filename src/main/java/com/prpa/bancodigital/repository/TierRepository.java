package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Tier> findByNomeIgnoreCase(String nome);
}

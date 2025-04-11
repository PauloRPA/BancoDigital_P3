package com.prpa.bancodigital.security.repository;

import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankUserRepository extends JpaRepository<BankUser, Long> {

    Optional<BankUser> findByUsername(String username);

    boolean existsByRoles(List<Role> roles);

}
package com.prpa.bancodigital.security.service;

import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.repository.BankUserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BankUserService implements UserDetailsService {

    private final BankUserRepository bankUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Lazy
    public BankUserService(BankUserRepository bankUserRepository, PasswordEncoder passwordEncoder) {
        this.bankUserRepository = bankUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return bankUserRepository.findByUsername(username)
               .orElseThrow(() -> new BadCredentialsException("Combinação de usuário e senha não encontrada"));
    }

    public void newBankUser(BankUser user) {
        user.setUsername(passwordEncoder.encode(user.getUsername()));
        bankUserRepository.save(user);
    }

}

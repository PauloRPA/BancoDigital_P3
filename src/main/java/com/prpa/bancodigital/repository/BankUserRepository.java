package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.BankUser;
import com.prpa.bancodigital.repository.dao.BankUserDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BankUserRepository {

    private final BankUserDao bankUserDao;

    public BankUserRepository(BankUserDao bankUserDao) {
        this.bankUserDao = bankUserDao;
    }

    public Optional<BankUser> findById(long id) {
        return bankUserDao.findById(id);
    }

    public boolean existsById(long id) {
        return bankUserDao.findById(id).isPresent();
    }

    public List<BankUser> findAll() {
        return bankUserDao.findAll();
    }

    public Page<BankUser> findAll(Pageable page) {
        return bankUserDao.findAll(page);
    }

    public BankUser save(BankUser toSave) {
        return bankUserDao.save(toSave);
    }

    public void deleteById(long id) {
        bankUserDao.deleteById(id);
    }

    public Optional<BankUser> findByUsername(String username) {
        return bankUserDao.findByUsername(username);
    }

}
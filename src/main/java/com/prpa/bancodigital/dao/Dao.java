package com.prpa.bancodigital.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> findById(long id);

    boolean existsById(long id);

    List<T> findAll();

    Page<T> findAll(Pageable page);

    T save(T toSave);

    void deleteById(long id);
}

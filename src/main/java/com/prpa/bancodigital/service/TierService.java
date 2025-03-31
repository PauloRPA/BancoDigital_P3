package com.prpa.bancodigital.service;

import com.prpa.bancodigital.config.ApplicationInitialization;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.TierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TierService {

    private final TierRepository tierRepository;

    @Autowired
    public TierService(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    public List<Tier> findAll(PageRequest page) {
        List<Tier> content = new ArrayList<>(tierRepository.findAll(page).getContent());
        content.sort(Comparator.comparing(Tier::getId));
        return content;
    }

    public Tier newTier(Tier toSave) {
        if (tierRepository.existsByNome(toSave.getNome()))
            throw new ResourceAlreadyExistsException("Um tier com o nome especificado ja existe");
        return tierRepository.save(toSave);
    }

    public void deleteById(long id) {
        if (isRequiredTier(id)) {
            throw new InvalidInputParameterException("Não é possível remover tiers que são obrigatórios para regra de negócio.");
        }
        if (!tierRepository.existsById(id))
            throw new ResourceNotFoundException("Tier com o ID especificado não foi encontrado");
        tierRepository.deleteById(id);
    }

    private boolean isRequiredTier(long id) {
        return id < ApplicationInitialization.REQUIRED_TIERS.length + 1;
    }

}
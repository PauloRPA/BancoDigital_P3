package com.prpa.bancodigital.service;

import com.prpa.bancodigital.config.ApplicationInitialization;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.PoliticaTaxaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PoliticaTaxaService {

    private final PoliticaTaxaRepository politicaTaxaRepository;
    private final TierService tierService;

    public PoliticaTaxaService(PoliticaTaxaRepository politicaTaxaRepository,
                               TierService tierService) {
        this.politicaTaxaRepository = politicaTaxaRepository;
        this.tierService = tierService;
    }

    public List<PoliticaTaxa> findAll(PageRequest page) {
        return politicaTaxaRepository.findAll(page).getContent();
    }

    public PoliticaTaxa findById(long id) {
        return politicaTaxaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado uma politica com o id especificado"));
    }

    public List<PoliticaTaxa> findByTier(Tier tier) {
        return findByTier(tier.getNome());
    }

    public List<PoliticaTaxa> findByTier(String nome) {
        final Tier tier = tierService.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado um tier com o nome especificado"));
        return politicaTaxaRepository.findByTiers(tier);
    }

    public PoliticaTaxa save(PoliticaTaxa politicaTaxa) {
        if (politicaTaxaRepository.existsByNome(politicaTaxa.getNome()))
            throw new ResourceAlreadyExistsException("Ja existe uma politica com este nome");

        Set<Tier> tierSet = politicaTaxa.getTiers().stream()
                .map(tier -> tierService.findByNomeIgnoreCase(tier.getNome()))
                .map(found -> found.orElseThrow(
                        () -> new ResourceNotFoundException("Não foi encontrado um tier com o nome especificado")))
                .collect(Collectors.toSet());
        politicaTaxa.setTiers(tierSet);

        return politicaTaxaRepository.save(politicaTaxa);
    }

    public void deleteById(long id) {
        if (!politicaTaxaRepository.existsById(id))
            throw new ResourceNotFoundException("Não foi encontrada nenhuma politica com esse id");
        if (isRequired(id))
            throw new InvalidInputParameterException("Não é possível remover politicas que são obrigatórios para regra de negócio.");
        politicaTaxaRepository.deleteById(id);
    }

    private boolean isRequired(long id) {
        final int requiredIncomeSize = ApplicationInitialization.REQUIRED_INCOME.size();
        final int requiredTaxSize = ApplicationInitialization.REQUIRED_MAINTENANCE_TAX.size();
        return id < requiredIncomeSize + requiredTaxSize + 1;
    }

}
package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.dtos.PoliticaTaxaDTO;
import com.prpa.bancodigital.model.validator.groups.PostRequired;
import com.prpa.bancodigital.service.PoliticaTaxaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/politicas/taxas")
public class PoliticaTaxaController {

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE = 0;

    private final PoliticaTaxaService politicaTaxaService;

    public PoliticaTaxaController(PoliticaTaxaService politicaTaxaService) {
        this.politicaTaxaService = politicaTaxaService;
    }

    @Operation(summary = "Lista todas politicas de taxa de acordo com os parâmetros de paginação fornecidos")
    @GetMapping("")
    public ResponseEntity<List<PoliticaTaxa>> getPoliticas(
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "-1") int size
    ) {
        page = page < 0 ? DEFAULT_PAGE : page;
        size = size < 0 || size > MAX_PAGE_SIZE ? DEFAULT_PAGE_SIZE : size;
        return ResponseEntity.ok(politicaTaxaService.findAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Busca uma politica de taxa pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<PoliticaTaxa> getPoliticaById(@PathVariable("id") long id) {
        return ResponseEntity.ok(politicaTaxaService.findById(id));
    }

    @Operation(summary = "Busca todas as politicas de taxa associadas a um determinado Tier")
    @GetMapping("/tier/{nome}")
    public ResponseEntity<List<PoliticaTaxa>> getPoliticaByTier(@PathVariable("nome") String nome) {
        if (nome.isBlank())
            throw new ValidationException("O atributo de nome não pode ser vazio");

        List<PoliticaTaxa> politicas = politicaTaxaService.findByTier(nome);
        return ResponseEntity.ok(politicas);
    }

    @Operation(summary = "Insere uma nova politica de taxa")
    @PostMapping("")
    public ResponseEntity<PoliticaTaxa> postPoliticaTaxa(
            @RequestBody @Validated(PostRequired.class) PoliticaTaxaDTO politicaTaxaDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);

        PoliticaTaxa saved = politicaTaxaService.save(politicaTaxaDTO.toPoliticaTaxa());
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @Operation(summary = "Remove uma politica de taxa")
    @DeleteMapping("/{id}")
    public ResponseEntity<PoliticaTaxa> deletePoliticaTaxa(@PathVariable("id") long id) {
        politicaTaxaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
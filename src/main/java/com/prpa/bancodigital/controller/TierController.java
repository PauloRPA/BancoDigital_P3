package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.model.dtos.TierDTO;
import com.prpa.bancodigital.service.TierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@RestController
@SecurityRequirement(name = "swagger-openapi")
@RequestMapping(ApplicationConfig.API_V1 + "/tiers")
public class TierController {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private final TierService tierService;

    @Autowired
    public TierController(TierService tierService) {
        this.tierService = tierService;
    }

    @Operation(summary = "Retorna todos os Tiers cadastrados no sistema de acordo com a paginação descrita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tiers retornados com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Tier.class)))})})
    @GetMapping("")
    public ResponseEntity<List<Tier>> getTiers(
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "-1") int size
    ) {
        page = page < 0 ? DEFAULT_PAGE : page;
        size = size < 0 || size > MAX_PAGE_SIZE ? DEFAULT_PAGE_SIZE : size;

        return ResponseEntity.ok(tierService.findAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Retorna o tier pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tier encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Tier.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tier com esse ID não encontrado", content = @Content)})
    @PostMapping("")
    public ResponseEntity<Tier> getTiers(@RequestBody @Valid TierDTO tierDTO, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Tier tier = tierService.newTier(tierDTO.toTier());
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tier.getId());
        return ResponseEntity.created(location.toUri()).body(tier);
    }

    @Operation(summary = "Remove o tier pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tier removido com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado nenhum cliente com esse ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição com ID inválido", content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable(value = "id") long id) {
        tierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

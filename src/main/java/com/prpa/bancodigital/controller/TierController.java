package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.model.dtos.TierDTO;
import com.prpa.bancodigital.service.TierService;
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

    @GetMapping("")
    public ResponseEntity<List<Tier>> getTiers(
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "-1") int size
    ) {
        page = page < 0 ? DEFAULT_PAGE : page;
        size = size < 0 || size > MAX_PAGE_SIZE ? DEFAULT_PAGE_SIZE : size;

        return ResponseEntity.ok(tierService.findAll(PageRequest.of(page, size)));
    }

    @PostMapping("")
    public ResponseEntity<Tier> getTiers(@RequestBody @Valid TierDTO tierDTO, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Tier tier = tierService.newTier(tierDTO.toTier());
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tier.getId());
        return ResponseEntity.created(location.toUri()).body(tier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable(value = "id") long id) {
        tierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

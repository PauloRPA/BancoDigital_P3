package com.prpa.bancodigital.model.external.cep;

import com.prpa.bancodigital.model.dtos.EnderecoDTO;
import com.prpa.bancodigital.model.validator.CepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CancellationException;

@Slf4j
@Component
public class ViaCepService implements CepService {

    @Value("${application.external.viacep.url}")
    private String viaCepUrl;

    private final CepValidator cepValidator;
    private final RestTemplate restTemplate;

    public ViaCepService(CepValidator cepValidator, RestTemplate restTemplate) {
        this.cepValidator = cepValidator;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<EnderecoDTO> findByCep(String cep) {
        if (!cepValidator.isValid(cep, null))
            return Optional.empty();

        log.debug("Realizando busca para o cep {} usando o serviço: ViaCep", cep);
        try {
            final long start = System.currentTimeMillis();
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(viaCepUrl.formatted(cep), ViaCepResponse.class);
            if (!response.getStatusCode().is2xxSuccessful())
                return Optional.empty();

            final long end = System.currentTimeMillis();
            log.info("ViaCep respondeu com {}. Com tempo total para resposta de {}ms", response.getStatusCode(), end - start);
            ViaCepResponse body = response.getBody();
            if (body.cep() == null) return Optional.empty();
            return Optional.of(new EnderecoDTO(body.complemento(), body.cep(), null, body.logradouro(), body.bairro(), body.localidade(), body.estado()));
        } catch (CancellationException e) {
            e.printStackTrace();
            log.warn("Não foi possível buscar pelo cep {} usando o serviço ViaCep devido a timeout", cep);
            return Optional.empty();
        }
    }

}

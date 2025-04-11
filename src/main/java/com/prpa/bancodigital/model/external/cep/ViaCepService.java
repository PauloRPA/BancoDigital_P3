package com.prpa.bancodigital.model.external.cep;

import com.prpa.bancodigital.model.dtos.EnderecoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class ViaCepService implements CepService {

    @Value("${application.external.viacep.url}")
    private String viaCepUrl;

    @Override
    public Optional<EnderecoDTO> findByCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(viaCepUrl.formatted(cep), ViaCepResponse.class);
        if (!response.getStatusCode().is2xxSuccessful())
            return Optional.empty();
        ViaCepResponse body = response.getBody();
        if (body.cep() == null) return Optional.empty();
        return Optional.of(new EnderecoDTO(body.complemento(), body.cep(), null, body.logradouro(), body.bairro(), body.localidade(), body.estado()));
    }

}

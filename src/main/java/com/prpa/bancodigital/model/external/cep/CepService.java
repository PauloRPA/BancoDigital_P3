package com.prpa.bancodigital.model.external.cep;

import com.prpa.bancodigital.model.dtos.EnderecoDTO;

import java.util.Optional;

public interface CepService {

    Optional<EnderecoDTO> findByCep(String cep);

}

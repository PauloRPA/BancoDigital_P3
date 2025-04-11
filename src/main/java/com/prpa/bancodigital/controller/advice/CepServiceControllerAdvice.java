package com.prpa.bancodigital.controller.advice;

import com.prpa.bancodigital.model.dtos.ClienteDTO;
import com.prpa.bancodigital.model.dtos.EnderecoDTO;
import com.prpa.bancodigital.model.external.cep.CepService;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.Optional;

import static java.util.Objects.isNull;

@RestControllerAdvice
public class CepServiceControllerAdvice extends RequestBodyAdviceAdapter {

    private CepService cepService;

    public CepServiceControllerAdvice(CepService cepService) {
        this.cepService = cepService;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getParameterType().isAssignableFrom(ClienteDTO.class);
    }

    @Override
    public Object afterBodyRead(Object body,
                                HttpInputMessage inputMessage,
                                MethodParameter parameter,
                                Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        if (!(body instanceof ClienteDTO)) return body;
        ClienteDTO clienteDTO = (ClienteDTO) body;
        if (isNull(clienteDTO.endereco())) return body;
        EnderecoDTO enderecoDTO = clienteDTO.endereco();
        if (isNull(enderecoDTO.cep())) return body;
        if (((enderecoDTO.rua() != null && enderecoDTO.rua().isBlank()) &&
                (enderecoDTO.bairro() != null && enderecoDTO.bairro().isBlank()) &&
                (enderecoDTO.cidade() != null && enderecoDTO.cidade().isBlank()) &&
                (enderecoDTO.estado() != null && enderecoDTO.estado().isBlank()))
        ) return body;

        Optional<EnderecoDTO> optionalCepServiceResponse = cepService.findByCep(clienteDTO.endereco().cep());
        if (optionalCepServiceResponse.isEmpty()) return body;
        EnderecoDTO novoEndereco = getEnderecoDTO(optionalCepServiceResponse.get(), enderecoDTO);

        return new ClienteDTO(clienteDTO.nome(), clienteDTO.cpf(), clienteDTO.dataNascimento(), novoEndereco, clienteDTO.tier());
    }

    private static EnderecoDTO getEnderecoDTO(EnderecoDTO response, EnderecoDTO enderecoDTO) {
        String rua = enderecoDTO.rua() == null || enderecoDTO.rua().isBlank() ? response.rua() : enderecoDTO.rua();
        String bairro = enderecoDTO.bairro() == null || enderecoDTO.bairro().isBlank() ? response.bairro() : enderecoDTO.bairro();
        String cidade = enderecoDTO.cidade() == null || enderecoDTO.cidade().isBlank() ? response.cidade() : enderecoDTO.cidade();
        String estado = enderecoDTO.estado() == null || enderecoDTO.estado().isBlank() ? response.estado() : enderecoDTO.estado();
        return new EnderecoDTO(enderecoDTO.complemento(), enderecoDTO.cep(), enderecoDTO.numero(), rua, bairro, cidade, estado);
    }
}

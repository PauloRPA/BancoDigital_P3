package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.PoliticaUsoDao;
import com.prpa.bancodigital.repository.dao.mapper.PoliticaUsoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static com.prpa.bancodigital.repository.dao.PoliticaUsoDao.POLITICA_USO_TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@JdbcTest
@ActiveProfiles("h2dev")
public class PoliticaUsoDaoTest extends DaoTest<PoliticaUso> {

    public static final String INSERT_QUERY_NAME = "insert";
    public static final String FIND_BY_ID_QUERY_NAME = "findById";

    private PoliticaUsoDao politicaUsoDao;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        politicaUsoDao = new PoliticaUsoDao(client, template, resolver);
    }

    @Override
    protected String getTableName() {
        return "politica_uso";
    }

    @Test
    void whenFindAllOnPoliticaUsoDaoShouldReturnTheThreeRequiredValues() {
        when(mappedQuerySpec.list()).thenReturn(List.of(new PoliticaUso(), new PoliticaUso(), new PoliticaUso()));
        assertEquals(3, politicaUsoDao.findAll().size());
    }

    @Test
    void whenSavePoliticaUsoShouldSucceed() {
        final long expectedId = 4L;
        BigDecimal expectedLimiteDiario = BigDecimal.valueOf(10);
        BigDecimal expectedLimiteCredito = BigDecimal.valueOf(0.5);
        PoliticaUso toSave = new PoliticaUso(expectedId, expectedLimiteDiario, expectedLimiteCredito);

        when(mappedQuerySpec.single())
                .thenReturn(toSave);
        politicaUsoDao.save(toSave);
        verify(client, times(1))
                .sql(resolveGeneric(FIND_BY_ID_QUERY_NAME));
        verify(client, times(1))
                .sql(resolver.get(POLITICA_USO_TABLE_NAME, INSERT_QUERY_NAME));
        verify(spec, times(2))
                .query(any(PoliticaUsoMapper.class));
        verify(mappedQuerySpec, times(1)).single();
    }

}
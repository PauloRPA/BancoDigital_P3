package com.prpa.bancodigital.config.repository;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class SpringYamlQueryResolver implements QueryResolver {

    private final Yaml yaml;
    private final List<QueryDefinition> definitions;

    public SpringYamlQueryResolver(Yaml yaml, String queriesPath) {
        this.yaml = yaml;
        List<String> filenames = findQueryFiles(queriesPath);
        this.definitions = parseDefinitions(queriesPath, filenames);
    }

    private List<String> findQueryFiles(String queriesPath) {
        List<String> filenames = new ArrayList<>();
        InputStream queriesInputStream = this.getClass().getResourceAsStream(queriesPath);
        if (isNull(queriesInputStream)) return filenames;
        try (var reader = new BufferedReader(new InputStreamReader(queriesInputStream))) {
            reader.lines().forEach(filenames::add);
        } catch (IOException e) {
            return filenames;
        }
        return filenames;
    }

    private List<QueryDefinition> parseDefinitions(String queriesPath, List<String> filenames) {
        return filenames.stream()
                .map(filename -> this.getClass().getResourceAsStream(queriesPath + "/" + filename))
                .filter(Objects::nonNull)
                .map(inputStream -> (QueryDefinition) yaml.loadAs(inputStream, QueryDefinition.class))
                .toList();
    }

    @Override
    public String get(String tableName, String queryName) {
        return definitions.stream()
                .filter(definition -> definition.getName().equals(tableName))
                .findFirst()
                .map(definition -> definition.getQueryMap().get(queryName))
                .orElseThrow(() -> new IllegalArgumentException("QueryName invalida"));
    }

}

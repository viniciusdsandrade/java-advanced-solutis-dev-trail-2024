package com.restful.screenmatch.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restful.screenmatch.service.IConverteDados;

/// Implementação da interface 'IConverteDados' que utiliza a biblioteca Jackson para
/// realizar a conversão de dados JSON em objetos Java.
///
/// A classe 'ConverteDados' implementa a lógica de conversão usando a classe 'ObjectMapper'
/// da biblioteca Jackson, tratando exceções de processamento de JSON e retornando o objeto
/// Java correspondente.
///
/// @see com.fasterxml.jackson.databind.ObjectMapper
/// @see IConverteDados
public class ConverteDados implements IConverteDados {

    private final ObjectMapper mapper = new ObjectMapper();

    /// Converte uma string JSON em um objeto Java do tipo especificado.
    ///
    /// Este método utiliza o `ObjectMapper` da biblioteca Jackson para ler a string JSON
    /// e convertê-la em um objeto da classe especificada. Caso ocorra um erro no
    /// processamento do JSON, é lançada uma `RuntimeException`.
    ///
    /// @param json   A string no formato JSON que contém os dados a serem convertidos.
    /// @param classe A classe do tipo de objeto para o qual o JSON será convertido.
    /// @param <T>    O tipo de retorno, determinado pela classe passada como parâmetro.
    /// @return O objeto Java resultante da conversão do JSON.
    /// @throws RuntimeException Caso ocorra um erro ao processar o JSON.
    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.restful.screenmatch.service.impl;

import com.restful.screenmatch.service.IConsumoApi;
import org.slf4j.Logger;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.URI.create;
import static java.time.Duration.ofSeconds;
import static org.slf4j.LoggerFactory.getLogger;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

/// Implementação da interface 'IConsumoApi' que realiza requisições HTTP
/// para consumir dados de APIs externas.
///
/// A classe 'ConsumoApi' utiliza o 'HttpClient' para realizar requisições GET
/// e retornar o corpo da resposta como string. Em caso de falhas, os erros
/// são registrados com o logger, e a exceção é propagada.
///
/// @see HttpClient
/// @see HttpRequest
/// @see HttpResponse
public class ConsumoApi implements IConsumoApi {

    private static final Logger logger = getLogger(ConsumoApi.class);

    /// Realiza uma requisição HTTP GET a uma URL e retorna o corpo da resposta.
    ///
    /// Este metodo utiliza o 'HttpClient' para realizar a requisição GET com um
    /// tempo limite de conexão de 10 segundos. Caso haja falha, a exceção será
    /// capturada e registrada.
    ///
    /// @param url A URL para a qual a requisição será enviada.
    /// @return O corpo da resposta HTTP como string.
    /// @throws Exception Caso ocorra algum erro durante a requisição.
    @Override
    public String obterDados(String url) throws Exception {
        try (HttpClient client = newBuilder()
                .connectTimeout(ofSeconds(10))
                .build()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, ofString());
            return response.body();
        } catch (Exception e) {
            logger.error("Erro ao buscar dados da URL: {}", url, e);
            throw e;
        }
    }
}

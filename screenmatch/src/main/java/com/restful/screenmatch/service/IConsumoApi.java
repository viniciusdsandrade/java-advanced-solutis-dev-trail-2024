package com.restful.screenmatch.service;

/// Interface responsável por definir o metodo para realizar o consumo de APIs externas.
///
/// A interface 'IConsumoApi' define um metodo genérico para obter dados de uma URL
/// específica e retornar esses dados como uma string.
public interface IConsumoApi {

    /// Metodo para realizar uma requisição HTTP GET a uma URL específica.
    ///
    /// Este metodo realiza a requisição e retorna o corpo da resposta como uma string.
    /// Em caso de erro, uma exceção será lançada.
    ///
    /// @param url A URL para a qual a requisição será enviada.
    /// @return O corpo da resposta HTTP como uma string.
    /// @throws Exception Caso ocorra algum erro durante a requisição.
    String obterDados(String url) throws Exception;
}
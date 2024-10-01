package com.restful.screenmatch.service;

/// Interface responsável por definir a conversão de dados JSON em objetos Java.
///
/// A interface 'IConverteDados' define um metodo genérico que converte uma string JSON
/// em um objeto Java do tipo especificado pela classe.
///
public interface IConverteDados {

    /// Metodo genérico para converter uma string JSON em um objeto Java.
    ///
    /// Este metodo recebe uma string JSON e uma classe Java, e utiliza
    /// o mapeador para converter o JSON em um objeto da classe especificada.
    ///
    /// @param json A string no formato JSON que contém os dados a serem convertidos.
    /// @param classe A classe do tipo de objeto para o qual o JSON será convertido.
    /// @param <T> O tipo de retorno, determinado pela classe passada como parâmetro.
    /// @return O objeto Java resultante da conversão do JSON.
    <T> T obterDados(String json, Class<T> classe);
}

package com.restful.screenmatch.service;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;

import java.time.LocalDate;
import java.util.List;

/// Interface responsável pela definição de métodos para obtenção de
/// informações sobre séries, como dados gerais, temporadas e episódios.
///
/// A interface 'SerieService' define métodos que qualquer implementação
/// deve seguir para buscar e tratar dados de séries, incluindo informações
/// específicas sobre temporadas e episódios.
///
/// @see DadosSerie
/// @see DadosTemporada
/// @see DadosEpisodio
public interface SerieService {

    /// Metodo responsável por obter dados gerais sobre uma série.
    ///
    /// Este metodo realiza uma requisição à API externa, utilizando o nome
    /// da série como parâmetro, e retorna um objeto 'DadosSerie' contendo
    /// as informações da série como título, ano, gênero, entre outros.
    ///
    /// @param nomeSerie O nome da série que se deseja obter informações.
    /// @return Um objeto 'DadosSerie' com as informações da série.
    /// @throws Exception Caso ocorra algum erro na requisição à API.
    DadosSerie obterDadosSerie(String nomeSerie) throws Exception;

    /// Metodo responsável por obter informações das temporadas de uma série.
    ///
    /// Este metodo faz uma requisição para cada temporada até o total
    /// especificado, retornando uma lista de objetos 'DadosTemporada' com
    /// os detalhes de cada temporada, como número de episódios e sinopse.
    ///
    /// @param nomeSerie O nome da série.
    /// @param totalTemporadas O número total de temporadas que se deseja obter.
    /// @return Uma lista de objetos 'DadosTemporada' com informações das temporadas.
    /// @throws Exception Caso ocorra algum erro na requisição à API.
    List<DadosTemporada> obterTemporadas(String nomeSerie, int totalTemporadas) throws Exception;

    /// Metodo responsável por obter dados de um episódio específico.
    ///
    /// Com base no nome da série, número da temporada e número do episódio,
    /// este metodo realiza uma requisição à API e retorna um objeto 'DadosEpisodio'
    /// contendo detalhes como título do episódio, duração e data de exibição.
    ///
    /// @param nomeSerie O nome da série.
    /// @param temporada O número da temporada do episódio.
    /// @param episodio O número do episódio na temporada.
    /// @return Um objeto 'DadosEpisodio' com informações detalhadas do episódio.
    /// @throws Exception Caso ocorra algum erro na requisição à API.
    DadosEpisodio obterDadosEpisodio(String nomeSerie, int temporada, int episodio) throws Exception;

    List<DadosEpisodio> obterEpisodiosApartirDeData(String nomeSerie, int temporada, LocalDate data) throws Exception;
}

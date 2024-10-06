package com.restful.screenmatch.service.impl;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;
import com.restful.screenmatch.service.SerieService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;

/// Implementação da interface 'SerieService', responsável por consumir
/// uma API externa para obter informações sobre séries, temporadas e episódios.
///
/// Esta classe utiliza duas dependências principais: 'ConsumoApi' para realizar
/// as requisições à API externa, e 'ConverteDados' para converter o JSON
/// recebido da API em objetos Java. A API consultada é a OMDb API, e as
/// informações retornadas são transformadas em objetos 'DadosSerie',
/// 'DadosTemporada' e 'DadosEpisodio'.
///
/// @see ConsumoApi
/// @see ConverteDados
/// @see DadosSerie
/// @see DadosTemporada
/// @see DadosEpisodio
public class SerieServiceImpl implements SerieService {

    private final ConsumoApi consumoApi;
    private final ConverteDados conversor;
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    /// Construtor da classe 'SerieServiceImpl'.
    ///
    /// Inicializa os componentes necessários para o consumo da API e
    /// conversão dos dados retornados, que serão usados nos métodos
    /// implementados desta classe.
    ///
    /// @param consumoApi Dependência responsável por realizar a requisição HTTP à API externa.
    /// @param conversor  Dependência responsável por converter o JSON da API em objetos Java.
    public SerieServiceImpl(ConsumoApi consumoApi, ConverteDados conversor) {
        this.consumoApi = consumoApi;
        this.conversor = conversor;
    }

    /// Obtém os dados gerais de uma série através do nome.
    ///
    /// O metodo faz uma requisição à OMDb API utilizando o nome da série e retorna
    /// um objeto 'DadosSerie' contendo as informações gerais da série.
    ///
    /// @param nomeSerie O nome da série que se deseja consultar.
    /// @return Um objeto 'DadosSerie' com informações gerais da série.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public DadosSerie obterDadosSerie(String nomeSerie) throws Exception {
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    /// Obtém os dados das temporadas de uma série.
    ///
    /// Para cada temporada, o metodo realiza uma requisição à OMDb API e converte
    /// o resultado em um objeto 'DadosTemporada'. O processo é repetido até
    /// que o número total de temporadas especificado seja atingido.
    ///
    /// @param nomeSerie       O nome da série.
    /// @param totalTemporadas O número total de temporadas a serem consultadas.
    /// @return Uma lista de objetos 'DadosTemporada' com informações das temporadas.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public List<DadosTemporada> obterTemporadas(String nomeSerie, int totalTemporadas) throws Exception {
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= totalTemporadas; i++) {
            String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        return temporadas;
    }

    /// Obtém os dados de um episódio específico.
    ///
    /// O metodo consulta a OMDb API utilizando o nome da série, número da temporada
    /// e número do episódio, e converte o resultado em um objeto 'DadosEpisodio'.
    ///
    /// @param nomeSerie O nome da série.
    /// @param temporada O número da temporada.
    /// @param episodio  O número do episódio na temporada.
    /// @return Um objeto 'DadosEpisodio' com informações detalhadas do episódio.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public DadosEpisodio obterDadosEpisodio(String nomeSerie, int temporada, int episodio) throws Exception {
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + temporada + "&episode=" + episodio + API_KEY);
        return conversor.obterDados(json, DadosEpisodio.class);
    }

    /// Metodo para obter episódios de uma temporada a partir de uma data específica
    @Override
    public List<DadosEpisodio> obterEpisodiosApartirDeData(String nomeSerie, int temporada, LocalDate data) throws Exception {
        // Obter todos os episódios da temporada
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + temporada + API_KEY);
        DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

        // Filtrar episódios a partir da data fornecida
        DateTimeFormatter formatter = ofPattern("yyyy-MM-dd");
        return dadosTemporada.episodios().stream()
                .filter(e ->
                        parse(e.dataLancamento(), formatter).isAfter(data) ||
                        parse(e.dataLancamento(), formatter).isEqual(data))
                .collect(toList());
    }
}
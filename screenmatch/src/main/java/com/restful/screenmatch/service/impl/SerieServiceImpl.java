package com.restful.screenmatch.service.impl;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;
import com.restful.screenmatch.service.SerieService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static java.lang.Double.parseDouble;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/// Implementação da interface 'SerieService', responsável por consumir
/// uma API externa para obter informações sobre séries, temporadas e episódios.
///
/// Esta classe utiliza duas dependências principais: 'ConsumoApi' para realizar
/// as requisições à API externa, e 'ConverteDados' para converter o JSON
/// recebido da API em objetos Java.
///
/// @see ConsumoApi
/// @see ConverteDados
public class SerieServiceImpl implements SerieService {

    private final ConsumoApi consumoApi;
    private final ConverteDados conversor;
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    /// Construtor da classe 'SerieServiceImpl'.
    ///
    /// Inicializa os componentes necessários para o consumo da API e
    /// conversão dos dados retornados.
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

    /// Obtém os episódios de uma temporada a partir de uma data específica.
    ///
    /// O metodo consulta todos os episódios da temporada e filtra aqueles cuja data de lançamento
    /// seja igual ou posterior à data informada.
    ///
    /// @param nomeSerie O nome da série.
    /// @param temporada O número da temporada.
    /// @param data      A data a partir da qual os episódios devem ser filtrados.
    /// @return Uma lista de objetos 'DadosEpisodio' que foram lançados a partir da data especificada.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
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

    /// Obtém os 10 melhores episódios de uma temporada com base nas avaliações.
    ///
    /// O metodo filtra os episódios com avaliações válidas, ordena-os por avaliação de forma decrescente,
    /// e retorna uma lista com os títulos dos 10 episódios mais bem avaliados.
    ///
    /// @param nomeSerie O nome da série.
    /// @param temporada O número da temporada.
    /// @return Uma lista com os títulos dos 10 melhores episódios.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public List<String> obterTop10Episodios(String nomeSerie, int temporada) throws Exception {
        // Obter todos os episódios da temporada
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + temporada + API_KEY);
        DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

        // Filtrar e classificar os episódios com avaliação válida e retornar os 10 melhores
        return dadosTemporada.episodios().stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))  // Filtrar avaliações válidas
                .sorted(comparing(DadosEpisodio::avaliacao).reversed())  // Ordenar por avaliação (decrescente)
                .limit(10)  // Limitar aos 10 melhores
                .map(e -> e.titulo().toUpperCase())  // Transformar o título em maiúsculas
                .collect(toList());  // Coletar resultados
    }

    /// Encontra um episódio de uma temporada com uma avaliação mínima especificada.
    ///
    /// O metodo busca qualquer episódio que tenha uma avaliação maior ou igual à avaliação mínima
    /// informada pelo usuário, utilizando processamento paralelo para maior eficiência.
    ///
    /// @param nomeSerie       O nome da série.
    /// @param temporada       O número da temporada.
    /// @param avaliacaoMinima A avaliação mínima que o episódio deve ter.
    /// @return Um 'Optional<DadosEpisodio>' contendo o episódio encontrado, ou vazio se nenhum episódio atender ao critério.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public Optional<DadosEpisodio> encontrarQualquerEpisodioComAvaliacao(String nomeSerie, int temporada, double avaliacaoMinima) throws Exception {
        // Obter todos os episódios da temporada
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + temporada + API_KEY);
        DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

        // Buscar qualquer episódio com avaliação mínima usando parallelStream e findAny
        return dadosTemporada.episodios().parallelStream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A") && parseDouble(e.avaliacao()) >= avaliacaoMinima)
                .findAny();
    }

    /// Obtém a média das avaliações dos episódios por temporada de uma série especificada.
    ///
    /// O metodo percorre todas as temporadas da série e calcula a média das avaliações dos episódios
    /// que possuem uma avaliação válida. As avaliações são filtradas para considerar apenas aquelas
    /// superiores a 0.0. O resultado é armazenado em um mapa, onde a chave é o número da temporada
    /// e o valor é a média das avaliações para aquela temporada.
    ///
    /// @param nomeSerie       O nome da série.
    /// @param totalTemporadas O número total de temporadas da série.
    /// @return Um mapa onde a chave é o número da temporada e o valor é a média das avaliações dos episódios.
    /// @throws Exception Caso ocorra um erro na requisição ou na conversão dos dados.
    @Override
    public Map<Integer, Double> obterAvaliacoesPorTemporada(String nomeSerie, int totalTemporadas) throws Exception {
        // Inicializa um mapa para armazenar as médias de avaliações por temporada
        Map<Integer, Double> avaliacoesPorTemporada = new HashMap<>();

        // Loop para percorrer todas as temporadas da série
        for (int i = 1; i <= totalTemporadas; i++) {
            // Obter os episódios de cada temporada
            String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

            // Filtrar episódios com avaliações válidas (maior que 0)
            int finalI = i;
            Map<Integer, Double> mediaPorTemporada = dadosTemporada.episodios().stream()
                    .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A") && parseDouble(e.avaliacao()) > 0.0)
                    .collect(groupingBy(
                            e -> finalI,  // Agrupar por número da temporada (i)
                            averagingDouble(e -> parseDouble(e.avaliacao())))); // Calcula a média das avaliações

            // Acumular as médias no mapa principal
            avaliacoesPorTemporada.putAll(mediaPorTemporada);
        }

        // Exibe as médias de avaliações por temporada
        System.out.println("Avaliações por temporada:");
        System.out.println(avaliacoesPorTemporada);

        return avaliacoesPorTemporada;
    }

}

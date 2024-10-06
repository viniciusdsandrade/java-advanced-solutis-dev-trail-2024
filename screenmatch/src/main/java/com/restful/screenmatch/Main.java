package com.restful.screenmatch;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;
import com.restful.screenmatch.service.SerieService;
import com.restful.screenmatch.service.impl.ConsumoApi;
import com.restful.screenmatch.service.impl.ConverteDados;
import com.restful.screenmatch.service.impl.SerieServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.restful.screenmatch.util.InputValidator;

public class Main {

    private final InputValidator inputValidator;
    private final SerieService serieService;

    /// Construtor da classe Main.
    ///
    /// Inicializa as instâncias de 'InputValidator' e 'SerieService' para uso na aplicação.
    /// O 'InputValidator' é responsável pela validação e obtenção de entradas do usuário,
    /// enquanto o 'SerieService' realiza as operações relacionadas ao consumo de dados de séries.
    public Main() {
        this.inputValidator = new InputValidator();
        this.serieService = new SerieServiceImpl(
                new ConsumoApi(),
                new ConverteDados()
        );
    }

    /// Exibe o menu principal e delega as responsabilidades para os métodos corretos com base na escolha do usuário.
    ///
    /// O menu é exibido continuamente até que o usuário escolha a opção de sair. Cada opção
    /// corresponde a uma funcionalidade específica para buscar e exibir informações sobre séries.
    ///
    /// @throws Exception caso ocorra algum erro durante a execução das operações.
    public void exibeMenu() throws Exception {
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Buscar dados gerais da série");
            System.out.println("2. Buscar temporadas de uma série");
            System.out.println("3. Buscar um episódio específico");
            System.out.println("4. Buscar episódios a partir de uma data");
            System.out.println("5. Buscar Top 10 episódios por avaliação");
            System.out.println("6. Buscar qualquer episódio com avaliação mínima");
            System.out.println("7. Exibir médias de avaliações por temporada");
            System.out.println("8. Sair");

            int escolha = inputValidator.obterInt("Escolha uma opção: ");

            switch (escolha) {
                case 1 -> buscarDadosSerie();
                case 2 -> buscarTemporadas();
                case 3 -> buscarEpisodio();
                case 4 -> buscarEpisodiosApartirDeData();
                case 5 -> buscarTop10Episodios();
                case 6 -> buscarQualquerEpisodioComAvaliacaoMinima();
                case 7 -> exibirAvaliacoesPorTemporada();
                case 8 -> {
                    System.out.println("Encerrando o programa...");
                    return;
                }
                default -> System.err.println("Opção inválida, tente novamente.");
            }
        }
    }

    /// Busca e exibe os dados gerais de uma série, como o nome, ano e sinopse.
    ///
    /// Utiliza o 'serieService' para obter os dados da série e exibe-os no console.
    /// A entrada do nome da série é validada e convertida para o formato de consulta correto.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca dos dados da série.
    private void buscarDadosSerie() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série para a busca: ").replace(" ", "+");

        DadosSerie dadosSerie = serieService.obterDadosSerie(nomeSerie);
        System.out.println("Dados da Série: " + dadosSerie);
    }

    /// Busca e exibe todas as temporadas de uma série.
    ///
    /// Primeiro, os dados gerais da série são obtidos e exibidos, depois são listadas todas as temporadas.
    /// Cada temporada é exibida no console. O nome da série é validado e formatado para garantir a precisão da consulta.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca das temporadas.
    private void buscarTemporadas() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");

        DadosSerie dadosSerie = serieService.obterDadosSerie(nomeSerie);
        System.out.println("Total de temporadas: " + dadosSerie.totalTemporadas());

        List<DadosTemporada> temporadas = serieService.obterTemporadas(nomeSerie, dadosSerie.totalTemporadas());
        temporadas.forEach(System.out::println);
    }

    /// Busca e exibe os dados de um episódio específico de uma série.
    ///
    /// O usuário informa o nome da série, a temporada e o número do episódio. O sistema então exibe
    /// os dados desse episódio específico, como título e duração. O nome da série e as informações do episódio
    /// são validados antes de realizar a busca.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca do episódio.
    private void buscarEpisodio() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");
        int episodio = inputValidator.obterInt("Digite o número do episódio: ");

        DadosEpisodio dadosEpisodio = serieService.obterDadosEpisodio(nomeSerie, temporada, episodio);
        System.out.println("Dados do Episódio: " + dadosEpisodio);
    }

    /// Busca e exibe todos os episódios de uma temporada que foram lançados a partir de uma data específica.
    ///
    /// O usuário informa a série, a temporada e a data, e o sistema retorna os episódios lançados após essa data.
    /// O sistema filtra os episódios utilizando a data fornecida e garante que apenas os episódios lançados
    /// a partir dessa data sejam exibidos.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca dos episódios.
    private void buscarEpisodiosApartirDeData() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");
        LocalDate data = inputValidator.obterData("Digite a data (formato yyyy-MM-dd) para filtrar episódios: ", "yyyy-MM-dd");

        List<DadosEpisodio> episodios = serieService.obterEpisodiosApartirDeData(nomeSerie, temporada, data);
        System.out.println("Episódios a partir da data " + data + ":");
        episodios.forEach(System.out::println);
    }

    /// Busca e exibe os 10 melhores episódios de uma temporada, conforme a avaliação dos usuários.
    ///
    /// O usuário informa a série e a temporada, e o sistema retorna uma lista com os 10 episódios mais bem avaliados.
    /// Os episódios são filtrados por sua avaliação e a lista resultante é exibida no console.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca dos episódios.
    private void buscarTop10Episodios() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");

        List<String> top10Episodios = serieService.obterTop10Episodios(nomeSerie, temporada);
        System.out.println("Top 10 episódios da temporada " + temporada + ":");
        top10Episodios.forEach(System.out::println);
    }

    /// Busca e exibe um episódio de uma temporada que tenha uma avaliação mínima especificada pelo usuário.
    ///
    /// O usuário informa o nome da série, a temporada e a avaliação mínima desejada. O sistema faz a busca
    /// de qualquer episódio que atenda ao critério de avaliação mínima. Se um episódio for encontrado, seus dados
    /// são exibidos. Caso contrário, uma mensagem informando que nenhum episódio atende aos critérios é exibida.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca do episódio ou na entrada de dados.
    private void buscarQualquerEpisodioComAvaliacaoMinima() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");
        double avaliacaoMinima = inputValidator.obterDouble("Digite a avaliação mínima (ex: 7.5): ");

        // Busca qualquer episódio da temporada com a avaliação mínima especificada.
        Optional<DadosEpisodio> episodioOpt = serieService.encontrarQualquerEpisodioComAvaliacao(nomeSerie, temporada, avaliacaoMinima);

        // Exibe o episódio encontrado ou uma mensagem informando que nenhum episódio atende à avaliação mínima.
        if (episodioOpt.isPresent()) {
            System.out.println("Episódio encontrado: " + episodioOpt.get());
        } else {
            System.out.println("Nenhum episódio encontrado com a avaliação mínima de " + avaliacaoMinima);
        }
    }

    /// Exibe a média das avaliações de cada temporada de uma série.
    ///
    /// O usuário informa o nome da série e o número total de temporadas. O sistema calcula e exibe
    /// a média das avaliações de todos os episódios para cada temporada.
    ///
    /// @throws Exception caso ocorra algum erro durante a busca ou no cálculo das avaliações.
    private void exibirAvaliacoesPorTemporada() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int totalTemporadas = inputValidator.obterInt("Digite o número total de temporadas: ");

        // Obtém as médias de avaliações por temporada
        Map<Integer, Double> avaliacoesPorTemporada = serieService.obterAvaliacoesPorTemporada(nomeSerie, totalTemporadas);

        // Exibe as médias no console
        System.out.println("Média das avaliações por temporada:");
        avaliacoesPorTemporada.forEach((temporada, media) ->
                System.out.println("Temporada " + temporada + ": Média de avaliação = " + media));
    }
}



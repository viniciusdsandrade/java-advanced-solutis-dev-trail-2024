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

import com.restful.screenmatch.util.InputValidator;

public class Main {

    private final InputValidator inputValidator;
    private final SerieService serieService;

    public Main() {
        this.inputValidator = new InputValidator();
        this.serieService = new SerieServiceImpl(
                new ConsumoApi(),
                new ConverteDados()
        );
    }

    // Exibe o menu e delega as responsabilidades
    public void exibMenu() throws Exception {
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Buscar dados gerais da série");
            System.out.println("2. Buscar temporadas de uma série");
            System.out.println("3. Buscar um episódio específico");
            System.out.println("4. Buscar episódios a partir de uma data");
            System.out.println("5. Sair");

            int escolha = inputValidator.obterInt("Escolha uma opção: ");

            switch (escolha) {
                case 1 -> buscarDadosSerie();
                case 2 -> buscarTemporadas();
                case 3 -> buscarEpisodio();
                case 4 -> buscarEpisodiosApartirDeData();
                case 5 -> {
                    System.out.println("Encerrando o programa...");
                    return;
                }
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    // Busca os dados gerais da série
    private void buscarDadosSerie() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série para a busca: ").replace(" ", "+");

        DadosSerie dadosSerie = serieService.obterDadosSerie(nomeSerie);
        System.out.println("Dados da Série: " + dadosSerie);
    }

    // Busca as temporadas de uma série
    private void buscarTemporadas() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");

        DadosSerie dadosSerie = serieService.obterDadosSerie(nomeSerie);
        System.out.println("Total de temporadas: " + dadosSerie.totalTemporadas());

        List<DadosTemporada> temporadas = serieService.obterTemporadas(nomeSerie, dadosSerie.totalTemporadas());
        temporadas.forEach(System.out::println);
    }

    // Busca um episódio específico
    private void buscarEpisodio() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");
        int episodio = inputValidator.obterInt("Digite o número do episódio: ");

        DadosEpisodio dadosEpisodio = serieService.obterDadosEpisodio(nomeSerie, temporada, episodio);
        System.out.println("Dados do Episódio: " + dadosEpisodio);
    }

    // Busca episódios a partir de uma data
    private void buscarEpisodiosApartirDeData() throws Exception {
        String nomeSerie = inputValidator.obterString("Digite o nome da série: ").replace(" ", "+");
        int temporada = inputValidator.obterInt("Digite a temporada: ");
        LocalDate data = inputValidator.obterData("Digite a data (formato yyyy-MM-dd) para filtrar episódios: ", "yyyy-MM-dd");

        List<DadosEpisodio> episodios = serieService.obterEpisodiosApartirDeData(nomeSerie, temporada, data);
        System.out.println("Episódios a partir da data " + data + ":");
        episodios.forEach(System.out::println);
    }
}



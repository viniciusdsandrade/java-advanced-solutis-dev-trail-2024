package com.restful.screenmatch;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;
import com.restful.screenmatch.service.impl.ConsumoApi;
import com.restful.screenmatch.service.impl.ConverteDados;

import java.util.List;
import java.util.Scanner;

import com.restful.screenmatch.service.SerieService;
import com.restful.screenmatch.service.impl.SerieServiceImpl;

public class Main {

    private final Scanner leitura = new Scanner(System.in);
    private final SerieService serieService;

    public Main() {
        this.serieService = new SerieServiceImpl(new ConsumoApi(), new ConverteDados());
    }

    // Exibe o menu e delega as responsabilidades
    public void exibMenu() throws Exception {
        System.out.print("Digite o nome da série para a busca: ");
        var nomeSerie = leitura.nextLine().replace(" ", "+");

        // Obtém dados da série
        DadosSerie dadosSerie = serieService.obterDadosSerie(nomeSerie);
        System.out.println("Dados da Série: " + dadosSerie);

        // Obtém temporadas
        List<DadosTemporada> temporadas = serieService.obterTemporadas(nomeSerie, dadosSerie.totalTemporadas());
        temporadas.forEach(System.out::println);

        // Exemplo de busca de episódio
        DadosEpisodio dadosEpisodio = serieService.obterDadosEpisodio(nomeSerie, 1, 2);
        System.out.println("Dados do Episódio: " + dadosEpisodio);
    }
}

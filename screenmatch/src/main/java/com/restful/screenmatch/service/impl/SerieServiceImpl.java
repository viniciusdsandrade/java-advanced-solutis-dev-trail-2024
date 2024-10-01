package com.restful.screenmatch.service.impl;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;
import com.restful.screenmatch.service.SerieService;

import java.util.ArrayList;
import java.util.List;

public class SerieServiceImpl implements SerieService {

    private final ConsumoApi consumoApi;
    private final ConverteDados conversor;
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public SerieServiceImpl(ConsumoApi consumoApi, ConverteDados conversor) {
        this.consumoApi = consumoApi;
        this.conversor = conversor;
    }

    @Override
    public DadosSerie obterDadosSerie(String nomeSerie) throws Exception {
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

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

    @Override
    public DadosEpisodio obterDadosEpisodio(String nomeSerie, int temporada, int episodio) throws Exception {
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + temporada + "&episode=" + episodio + API_KEY);
        return conversor.obterDados(json, DadosEpisodio.class);
    }
}
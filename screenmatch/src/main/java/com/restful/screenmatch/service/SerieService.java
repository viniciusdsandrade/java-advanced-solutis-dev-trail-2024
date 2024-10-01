package com.restful.screenmatch.service;

import com.restful.screenmatch.dto.DadosEpisodio;
import com.restful.screenmatch.dto.DadosSerie;
import com.restful.screenmatch.dto.DadosTemporada;

import java.util.List;

public interface SerieService {
    DadosSerie obterDadosSerie(String nomeSerie) throws Exception;

    List<DadosTemporada> obterTemporadas(String nomeSerie, int totalTemporadas) throws Exception;

    DadosEpisodio obterDadosEpisodio(String nomeSerie, int temporada, int episodio) throws Exception;
}
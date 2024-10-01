package com.restful.screenmatch.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/// Representa os dados de um episódio de uma série, com informações
/// como título, número do episódio, avaliação e data de lançamento.
///
/// A classe 'DadosEpisodio' utiliza anotações da biblioteca Jackson para mapear os
/// dados JSON recebidos. Cada campo é mapeado conforme os aliases
/// definidos, correspondendo às propriedades do JSON.
///
/// @param titulo O título do episódio, mapeado a partir da chave "Title" no JSON.
/// @param numero O número do episódio na temporada, mapeado a partir da chave "Episode".
/// @param avaliacao A avaliação do episódio no IMDb, mapeada a partir da chave "imdbRating".
/// @param dataLancamento A data de lançamento do episódio, mapeada a partir da chave "Released".
/// @see com.fasterxml.jackson.annotation.JsonAlias
/// @see com.fasterxml.jackson.annotation.JsonIgnoreProperties
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episode") Integer numero,
                            @JsonAlias("imdbRating") String avaliacao,
                            @JsonAlias("Released") String dataLancamento) {
}

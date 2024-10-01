package com.restful.screenmatch.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/// Representa os dados gerais de uma série, incluindo o título,
/// o número total de temporadas e a avaliação no IMDb.
///
/// A classe 'DadosSerie' também utiliza anotações da biblioteca Jackson para mapear os
/// dados JSON recebidos. Cada campo é mapeado conforme os aliases
/// definidos, correspondendo às propriedades do JSON.
///
/// @param titulo O título da série, mapeado a partir da chave "Title" no JSON.
/// @param totalTemporadas O número total de temporadas da série, mapeado a partir da chave "totalSeasons".
/// @param avaliacao A avaliação da série no IMDb, mapeada a partir da chave "imdbRating".
/// @see com.fasterxml.jackson.annotation.JsonAlias
/// @see com.fasterxml.jackson.annotation.JsonIgnoreProperties
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {
}

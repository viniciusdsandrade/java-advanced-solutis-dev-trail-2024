package com.restful.screenmatch.dto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/// Representa os dados de uma temporada de uma série, contendo o número
/// da temporada e uma lista de episódios.
///
/// A classe 'DadosTemporada' utiliza anotações da biblioteca Jackson para mapear os
/// dados JSON recebidos. Cada campo é mapeado conforme os aliases
/// definidos, correspondendo às propriedades do JSON.
///
/// @param numero O número da temporada, mapeado a partir da chave "Season" no JSON.
/// @param episodios Uma lista de objetos 'DadosEpisodio' que representam os episódios da temporada, mapeada a partir da chave "Episodes".
/// @see com.fasterxml.jackson.annotation.JsonAlias
/// @see com.fasterxml.jackson.annotation.JsonIgnoreProperties
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTemporada(@JsonAlias("Season") Integer numero,
                             @JsonAlias("Episodes") List<DadosEpisodio> episodios) {
}

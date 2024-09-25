package strategy.validacoes;

import strategy.Consulta;

/// Interface que define a estratégia para validar uma consulta.
///
/// Classes que implementam essa interface devem fornecer uma lógica específica
/// para verificar se uma consulta é válida de acordo com algum critério.
///
/// Essa interface é utilizada no padrão Strategy para permitir a definição de
/// diferentes critérios de validação para uma consulta de forma flexível e
/// extensível.
///
/// @see strategy.AgendamentoConsulta
public interface ConsultaValidationStrategy {

    /// Verifica se a consulta é válida conforme o critério definido pela
    /// estratégia.
    ///
    /// @param consulta A consulta a ser validada.
    /// @return `true` se a consulta for válida, `false` caso contrário.
    boolean isValid(Consulta consulta);
}
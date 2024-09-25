package strategy.validacoes;

import strategy.Consulta;

/// Estratégia de validação que verifica se a data e hora da consulta são válidas.
///
/// A data e a hora são consideradas válidas se ambas não forem nulas e não
/// estiverem vazias.
public class DataHoraValidaStrategy implements ConsultaValidationStrategy {

    /// Verifica se a data e hora da consulta são válidas.
    ///
    /// @param consulta A consulta a ser validada.
    /// @return 'true' se a data e hora forem válidas, 'false' caso contrário.
    @Override
    public boolean isValid(Consulta consulta) {
        // Lógica para validar a data e hora da consulta (exemplo)
        return consulta.getData() != null && !consulta.getData().isEmpty() &&
               consulta.getHora() != null && !consulta.getHora().isEmpty();
    }
}
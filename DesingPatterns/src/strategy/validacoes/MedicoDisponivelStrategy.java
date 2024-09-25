package strategy.validacoes;

import strategy.Consulta;

/// Estratégia de validação que verifica se o médico está disponível na data e
/// hora especificadas.
///
/// A disponibilidade do médico é considerada válida se o nome do médico, a data
/// e a hora não forem nulos e não estiverem vazios.
public class MedicoDisponivelStrategy implements ConsultaValidationStrategy {

    /// Verifica se o médico está disponível na data e hora especificadas.
    ///
    /// @param consulta A consulta a ser validada.
    /// @return `true` se o médico estiver disponível, `false` caso contrário.
    @Override
    public boolean isValid(Consulta consulta) {
        // Lógica para verificar a disponibilidade do médico (exemplo)
        return consulta.getMedico() != null && !consulta.getMedico().isEmpty() &&
               consulta.getData() != null && !consulta.getData().isEmpty() &&
               consulta.getHora() != null && !consulta.getHora().isEmpty();
    }
}
package strategy.validacoes;

import strategy.Consulta;

/// Estratégia de validação que verifica se o paciente está cadastrado.
///
/// O paciente é considerado cadastrado se o nome do paciente não for nulo e
/// não estiver vazio.
public class PacienteCadastradoStrategy implements ConsultaValidationStrategy {

    /// Verifica se o paciente está cadastrado.
    ///
    /// @param consulta A consulta a ser validada.
    /// @return 'true' se o paciente estiver cadastrado, 'false' caso contrário.
    @Override
    public boolean isValid(Consulta consulta) {
        return consulta.getPaciente() != null && !consulta.getPaciente().isEmpty();
    }
}
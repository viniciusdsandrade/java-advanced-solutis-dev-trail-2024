package exception;


/// A classe 'RegraDeNegocioException' representa uma exceção personalizada para violações de regras de negócio.
///
/// Esta exceção é utilizada quando uma regra de negócio específica é violada no sistema.
/// Por exemplo, ela pode ser usada para lançar erros relacionados a operações inválidas em contas bancárias.
public class RegraDeNegocioException extends RuntimeException {

    /// Construtor que recebe uma mensagem descritiva sobre a violação da regra de negócio.
    ///
    /// @param mensagem A mensagem que descreve o motivo da exceção.
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}

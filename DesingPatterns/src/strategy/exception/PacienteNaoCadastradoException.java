package strategy.exception;

public class PacienteNaoCadastradoException extends ConsultaValidationException {
    public PacienteNaoCadastradoException() {
        super("Paciente não cadastrado.");
    }
}
package strategy.exception;

public class MedicoNaoDisponivelException extends ConsultaValidationException {
    public MedicoNaoDisponivelException() {
        super("Médico não disponível na data/hora especificada.");
    }
}
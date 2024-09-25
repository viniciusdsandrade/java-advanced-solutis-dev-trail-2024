package strategy.exception;

public class DataHoraInvalidaException extends ConsultaValidationException {
    public DataHoraInvalidaException() {
        super("Data e/ou hora da consulta inválida.");
    }
}

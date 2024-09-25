package decorator.concreto;

import decorator.Notificador;
import decorator.NotificadorDecorator;

/// Decorador que envia uma notificação por SMS.
public class NotificadorSMS extends NotificadorDecorator {

    /// O número de telefone para o qual a mensagem SMS será enviada.
    private final String numeroTelefone;

    /// Construtor da classe.
    ///
    /// @param notificador    O notificador a ser decorado.
    /// @param numeroTelefone O número de telefone para o qual a mensagem
    ///                                             SMS será enviada.
    public NotificadorSMS(Notificador notificador, String numeroTelefone) {
        super(notificador);
        this.numeroTelefone = numeroTelefone;
    }

    /// Envia a mensagem SMS e delega o envio para o próximo notificador
    /// na cadeia.
    ///
    /// Imprime na saída padrão uma mensagem indicando o envio do SMS e,
    /// em seguida, chama o metodo 'enviar()' do notificador decorado.
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        System.out.println("Enviando SMS para " + numeroTelefone + ": " + mensagem);
        super.enviar(mensagem);
    }
}
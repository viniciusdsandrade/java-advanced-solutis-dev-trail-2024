package decorator.concreto;

import decorator.Notificador;
import decorator.NotificadorDecorator;

/// Decorador que envia uma notificação para um canal do Slack.
public class NotificadorSlack extends NotificadorDecorator {

    /// O nome do canal do Slack para o qual a mensagem será enviada.
    private final String canalSlack;

    /// Construtor da classe.
    ///
    /// @param notificador O notificador a ser decorado.
    /// @param canalSlack O nome do canal do Slack.
    public NotificadorSlack(Notificador notificador, String canalSlack) {
        super(notificador);
        this.canalSlack = canalSlack;
    }

    /// Envia a mensagem para o canal do Slack e delega o envio para o
    /// próximo notificador na cadeia.
    ///
    /// Imprime na saída padrão uma mensagem indicando o envio para o
    /// Slack e, em seguida, chama o metodo 'enviar()' do notificador
    /// decorado.
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        System.out.println("Enviando mensagem para o canal Slack " + canalSlack + ": " + mensagem);
        super.enviar(mensagem);
    }
}
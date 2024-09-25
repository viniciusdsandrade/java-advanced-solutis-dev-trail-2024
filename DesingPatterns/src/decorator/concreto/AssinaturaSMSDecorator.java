package decorator.concreto;

import decorator.Notificador;
import decorator.NotificadorDecorator;

/// Decorador que adiciona uma assinatura ao final da mensagem SMS.
public class AssinaturaSMSDecorator extends NotificadorDecorator {

    /// A assinatura a ser adicionada à mensagem.
    private final String assinatura;

    /// Construtor da classe.
    ///
    /// @param notificador O notificador a ser decorado.
    /// @param assinatura A assinatura a ser adicionada à mensagem.
    public AssinaturaSMSDecorator(Notificador notificador, String assinatura) {
        super(notificador);
        this.assinatura = assinatura;
    }

    /// Envia a mensagem SMS com a assinatura adicionada ao final.
    ///
    /// Adiciona a assinatura após a mensagem original e delega o envio
    /// para o notificador decorado.
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        super.enviar(mensagem + "\n\n" + assinatura);
    }
}
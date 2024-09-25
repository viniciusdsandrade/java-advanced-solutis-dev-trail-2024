package decorator.concreto;

import decorator.Notificador;
import decorator.NotificadorDecorator;

/// Decorador que limita o tamanho da mensagem SMS a um número máximo de
/// caracteres.
public class LimiteSMSDecorator extends NotificadorDecorator {

    /// O limite máximo de caracteres da mensagem SMS.
    private final int limiteCaracteres;

    /// Construtor da classe.
    ///
    /// @param notificador O notificador a ser decorado.
    /// @param limiteCaracteres O limite máximo de caracteres da mensagem SMS.
    public LimiteSMSDecorator(Notificador notificador, int limiteCaracteres) {
        super(notificador);
        this.limiteCaracteres = limiteCaracteres;
    }

    /// Envia a mensagem SMS, truncando-a se exceder o limite de caracteres.
    ///
    /// Verifica se o tamanho da mensagem excede o limite. Se exceder,
    /// trunca a mensagem e adiciona reticências (...) ao final. Em seguida,
    /// delega o envio para o notificador decorado.
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        if (mensagem.length() > limiteCaracteres) {
            mensagem = mensagem.substring(0, limiteCaracteres) + "...";
        }
        super.enviar(mensagem);
    }
}
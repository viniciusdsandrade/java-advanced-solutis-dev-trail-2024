package decorator;

/// Define o comportamento básico de um notificador, que é capaz de enviar
/// mensagens.
public interface Notificador {

    /// Envia uma mensagem.
    ///
    /// @param mensagem A mensagem a ser enviada.
    void enviar(String mensagem);
}
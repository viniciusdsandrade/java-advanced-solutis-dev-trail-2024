package decorator;

/// Classe abstrata que serve como base para todos os decoradores de
/// notificador.
///
/// Implementa a interface "Notificador" e delega a chamada do metodo
/// "enviar()" para o notificador decorado, permitindo que os decoradores
/// adicionem comportamentos extras antes ou depois do envio da mensagem.
public abstract class NotificadorDecorator implements Notificador {

    /// O notificador a ser decorado.
    protected Notificador notificador;

    /// Construtor da classe.
    ///
    /// @param notificador O notificador a ser decorado.
    public NotificadorDecorator(Notificador notificador) {
        this.notificador = notificador;
    }

    /// Envia a mensagem delegando a chamada para o notificador decorado.
    ///
    /// Decoradores concretos podem estender esse metodo para adicionar
    /// comportamentos extras antes ou depois do envio da mensagem.</p>
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        notificador.enviar(mensagem);
    }
}
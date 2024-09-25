package decorator;

/// Classe concreta que implementa um notificador por email.
public class NotificadorEmail implements Notificador {

    /// O endereço de email para o qual a mensagem será enviada.
    private final String email;

    /// Construtor da classe.
    ///
    /// @param email O endereço de email para o qual a mensagem será enviada.
    public NotificadorEmail(String email) {
        this.email = email;
    }

    /// Envia a mensagem por email.
    ///
    /// <Imprime na saída padrão uma mensagem indicando que o email foi
    /// enviado para o endereço especificado, juntamente com a mensagem
    /// propriamente dita.
    ///
    /// @param mensagem A mensagem a ser enviada.
    @Override
    public void enviar(String mensagem) {
        System.out.println("Enviando email para " + email + ": " + mensagem);
    }
}
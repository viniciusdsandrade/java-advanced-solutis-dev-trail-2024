package decorator;

import decorator.concreto.AssinaturaSMSDecorator;
import decorator.concreto.LimiteSMSDecorator;
import decorator.concreto.NotificadorSMS;
import decorator.concreto.NotificadorSlack;

public class TestDecorator {
    public static void main(String[] ignoredArgs) {
        Notificador notificador = new NotificadorEmail("usuario@example.com");

        notificador = new NotificadorSMS(notificador, "+5511999999999");
        notificador = new NotificadorSlack(notificador, "#canal-alertas");
        notificador = new AssinaturaSMSDecorator(notificador, "Equipe Acme");
        notificador = new LimiteSMSDecorator(notificador, 100);

        notificador.enviar("Alerta crítico!");
    }
}

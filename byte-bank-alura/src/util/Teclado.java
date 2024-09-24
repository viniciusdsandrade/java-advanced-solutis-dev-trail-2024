package util;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Teclado {

    private static final Scanner input = new Scanner(System.in).useDelimiter("\n");

    public static int lerInt() {
        while (true) {
            try {
                System.out.print("Digite um número inteiro: ");
                return input.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Digite um número inteiro válido.");
                input.next(); // Limpar o buffer do Scanner
            }
        }
    }

    public static String lerString() {
        while (true) {
            System.out.print("Digite uma string: ");
            String entrada = input.next();
            if (!entrada.isEmpty()) {
                return entrada;
            } else {
                System.err.println("Entrada inválida. A string não pode ser vazia.");
            }
        }
    }

    public static BigDecimal lerBigDecimal() {
        while (true) {
            try {
                System.out.print("Digite um valor decimal: ");
                return input.nextBigDecimal();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Digite um valor decimal válido.");
                input.next(); // Limpar o buffer do Scanner
            }
        }
    }

    public static void aguardarEnter() {
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        input.next();
    }
}
package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.util.Locale.forLanguageTag;

public class Teclado {

    private static final Scanner input = new Scanner(System.in).useDelimiter("\n");

    public static int lerInt() {
        while (true) {
            try {
                return input.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Digite um número inteiro válido.");
                input.next(); // Limpar o buffer do Scanner
            }
        }
    }

    public static String lerString() {
        while (true) {
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
                String entrada = input.next().trim();

                // Formato brasileiro
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(forLanguageTag("pt-BR"));
                DecimalFormat formatBR = new DecimalFormat("#,##0.0#", symbols);

                try {
                    Number number = formatBR.parse(entrada);
                    return new BigDecimal(number.toString());
                } catch (ParseException e) {
                    try {
                        return new BigDecimal(entrada);
                    } catch (NumberFormatException ex) {
                        throw new InputMismatchException("Formato inválido. Use vírgula para decimais ou ponto.");
                    }
                }

            } catch (InputMismatchException e) {
                System.err.println(e.getMessage());
                input.next(); // Limpa o buffer
            }
        }
    }

    public static void aguardarEnter() {
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        input.next();
    }
}
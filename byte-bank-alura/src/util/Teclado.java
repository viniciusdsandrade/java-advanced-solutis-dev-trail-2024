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

    /// Lê um número inteiro a partir da entrada do usuário.
    ///
    /// Este metodo solicita repetidamente que o usuário insira um número inteiro válido.
    /// Em caso de entrada inválida, uma mensagem de erro é exibida, e o processo continua
    /// até que um número inteiro válido seja inserido.
    ///
    /// @return O número inteiro inserido pelo usuário.
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

    /// Lê uma string a partir da entrada do usuário.
    ///
    /// Este metodo solicita repetidamente que o usuário insira uma string válida (não vazia).
    /// Em caso de entrada inválida (string vazia), uma mensagem de erro é exibida e o processo
    /// continua até que uma string válida seja inserida.
    ///
    /// @return A string inserida pelo usuário.
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

    /// Lê um valor decimal (BigDecimal) a partir da entrada do usuário.
    ///
    /// Este metodo tenta ler e formatar uma entrada numérica que pode estar no formato brasileiro
    /// (com vírgula como separador decimal) ou no formato padrão (com ponto como separador decimal).
    /// Em caso de erro de formatação, uma exceção é lançada e o processo continua até que um valor
    /// decimal válido seja inserido.
    ///
    /// @return O valor decimal inserido pelo usuário.
    public static BigDecimal lerBigDecimal() {
        while (true) {
            try {
                String entrada = input.next().trim();
                return formatarBigDecimal(entrada);
            } catch (InputMismatchException e) {
                System.err.println(e.getMessage());
                input.next(); // Limpa o buffer
            }
        }
    }

    /// Aguarda o pressionamento da tecla Enter para continuar.
    ///
    /// Este metodo exibe uma mensagem solicitando que o usuário pressione qualquer tecla seguida de ENTER
    /// para voltar ao menu principal.
    public static void aguardarEnter() {
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        input.next();
    }

    /// Formata uma string de entrada para um valor decimal (BigDecimal).
    ///
    /// Este metodo tenta converter uma string que representa um valor numérico no formato brasileiro,
    /// com vírgula como separador decimal. Se a conversão falhar, tenta converter no formato padrão
    /// (com ponto). Caso nenhum dos formatos seja válido, uma exceção é lançada com uma mensagem de erro.
    ///
    /// @param entrada A string contendo o valor numérico a ser formatado.
    /// @return O valor decimal (BigDecimal) formatado a partir da entrada.
    /// @throws InputMismatchException Se o formato da entrada for inválido.
    private static BigDecimal formatarBigDecimal(String entrada) {
        // Formato brasileiro
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(forLanguageTag("pt-BR"));
        DecimalFormat formatBR = new DecimalFormat("#,##0.0#", symbols);

        try {
            // Tenta parsear no formato brasileiro
            Number number = formatBR.parse(entrada);
            return new BigDecimal(number.toString());
        } catch (ParseException e) {
            try {
                // Se falhar, tenta parsear o formato padrão
                return new BigDecimal(entrada);
            } catch (NumberFormatException ex) {
                throw new InputMismatchException("Formato inválido. Use vírgula para decimais ou ponto.");
            }
        }
    }
}

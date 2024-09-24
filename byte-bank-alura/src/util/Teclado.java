package util;

import java.math.BigDecimal;
import java.util.Scanner;

public class Teclado {

    private static final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public static int lerInt() {
        return scanner.nextInt();
    }

    public static String lerString() {
        return scanner.next();
    }

    public static BigDecimal lerBigDecimal() {
        return scanner.nextBigDecimal();
    }

    public static void aguardarEnter() {
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        scanner.next();
    }
}
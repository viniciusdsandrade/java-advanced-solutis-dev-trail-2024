package com.restful.screenmatch.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static java.lang.System.in;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

public class InputValidator {

    private final Scanner scanner;

    public InputValidator() {
        this.scanner = new Scanner(in);
    }

    /// Metodo para validar e obter um número inteiro
    public int obterInt(String mensagem) {
        int numero;
        while (true) {
            try {
                System.out.print(mensagem);
                numero = Integer.parseInt(scanner.nextLine());
                break;  // Se o número for válido, sai do loop
            } catch (NumberFormatException e) {
                System.err.println("Erro: Entrada inválida. Por favor, insira um número inteiro.");
            }
        }
        return numero;
    }

    /// Metodo para validar e obter uma string que não seja vazia
    public String obterString(String mensagem) {
        String entrada;
        while (true) {
            System.out.print(mensagem);
            entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                break;  // Se a entrada não for vazia, sai do loop
            } else {
                System.err.println("Erro: Entrada vazia. Por favor, insira um valor.");
            }
        }
        return entrada;
    }

    /// Metodo para validar e obter uma data no formato correto
    public LocalDate obterData(String mensagem, String formato) {
        DateTimeFormatter formatter = ofPattern(formato);
        LocalDate data;
        while (true) {
            try {
                System.out.print(mensagem);
                String entrada = scanner.nextLine();
                data = parse(entrada, formatter);
                break;  // Se a data for válida, sai do loop
            } catch (DateTimeParseException e) {
                System.err.println("Erro: Formato de data inválido. Por favor, insira no formato " + formato + ".");
            }
        }
        return data;
    }
}

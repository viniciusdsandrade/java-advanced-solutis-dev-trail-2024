package com.bobocode.net.client;

import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.System.in;

/**
 * A util class that implements all the logic required for building {@link MessageBoardClient}.
 */
public class ClientUtil {
    private ClientUtil() {
    }

    /**
     * Usando o host e a porta fornecidos, cria uma instância de um novo {@link Socket} usando o construtor de dois argumentos.
     * Isso significa que o socket retornado estará já conectado ao servidor ou lançará um erro se a conexão falhar.
     *
     * @param host servidor host
     * @param port porta do servidor
     * @return uma instância de um socket conectado
     */
    @SneakyThrows
    public static Socket openSocket(String host, int port) {
        try {
            return new Socket(host, port); // Cria e conecta o socket ao servidor
        } catch (UnknownHostException e) {
            System.err.println("Host desconhecido: " + host);
            throw e; // Relança a exceção para tratamento externo
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor em " + host + ":" + port);
            throw e; // Relança a exceção para garantir que o chamador possa lidar com ela
        }
    }

    /**
     * Creates a simple {@link BufferedReader} that allows to read messages from the console.
     *
     * @return console-based {@link BufferedReader}
     */
    @SneakyThrows
    public static BufferedReader openConsoleReader() {
        InputStreamReader consoleInputStream = new InputStreamReader(in);
        return new BufferedReader(consoleInputStream);
    }

    /**
     * Prints a prompt and reads a line using provided reader.
     *
     * @return the message read by reader
     * @throws IOException se ocorrer um erro de entrada/saída
     */
    public static String readMessage(BufferedReader reader) throws IOException {
        System.out.print("Enter message (q to quit): ");
        return reader.readLine();
    }

    /**
     * Este é o metodo mais importante desta classe. Ele permite escrever uma mensagem de string para o socket dado.
     * Para escrever no socket conectado, ele utiliza seu {@link OutputStream}. Como precisamos escrever mensagens
     * de texto, é criado um {@link BufferedWriter} com base no {@link OutputStream}. Um writer permite escrever
     * mensagens {@link String} em vez de bytes. Para garantir o envio dos dados para o socket remoto, o buffer do
     * writer é esvaziado (flush) com o metodo correspondente.
     *
     * @param message uma mensagem que deve ser enviada ao socket
     * @param socket  uma instância de socket conectada ao servidor
     */
    @SneakyThrows
    public static void writeToSocket(String message, Socket socket) {
        try (OutputStream outputStream = socket.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writer.write(message);
            writer.newLine();  // Adiciona uma nova linha para garantir que a mensagem seja completa
            writer.flush();    // Força o envio da mensagem
        }
    }
}

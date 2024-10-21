package com.bobocode.net.server;


import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;

import static java.time.LocalTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Util class for a {@link MessageBoardServer}. It provides all necessary methods for network communication. Using these
 * utils, you can create a server socket, accept connection from a client, read a String message from client socket and print
 * it using specially provided format.
 */
public class ServerUtil {
    private static final DateTimeFormatter TIME_FORMATTER = ofPattern("HH:mm:ss");

    private ServerUtil() {
    }

    /**
     * A simple method that returns localhost
     *
     * @return localhost address
     */
    @SneakyThrows
    public static String getLocalHost() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Cria uma instância de um {@link ServerSocket} baseada na porta fornecida.
     *
     * @param port a porta na qual o servidor irá escutar
     * @return uma nova instância de {@link ServerSocket} ligada à porta especificada
     */
    @SneakyThrows
    public static ServerSocket createServerSocket(int port) {
        try {
            return new ServerSocket(port); // Cria e liga o ServerSocket na porta fornecida
        } catch (IOException e) {
            System.err.println("Erro ao criar ServerSocket na porta: " + port);
            throw e; // Relança a exceção para ser tratada externamente
        }
    }

    /**
     * Este metodo aceita um cliente em um dado ServerSocket e retorna uma instância de socket aceita.
     *
     * @param serverSocket um ServerSocket aberto
     * @return uma instância de um socket de cliente aceito
     */
    @SneakyThrows
    public static Socket acceptClientSocket(ServerSocket serverSocket) {
        try {
            return serverSocket.accept(); // Espera e aceita uma conexão de cliente
        } catch (IOException e) {
            System.err.println("Erro ao aceitar conexão do cliente.");
            throw e; // Relança a exceção para tratamento externo
        }
    }

    /**
     * Este é o metodo mais importante desta classe. Ele permite ler todas as mensagens enviadas pelo cliente por meio do socket.
     * Para ler de um socket, utiliza-se o {@link InputStream}. Como queremos ler mensagens de texto {@link String},
     * cria-se um {@link BufferedReader} com base no {@link InputStream} do socket. Usando o reader, ele lê uma linha e
     * retorna uma mensagem {@link String}.
     *
     * @param socket um socket aceito
     * @return mensagem recebida do cliente
     */
    @SneakyThrows
    public static String readMessageFromSocket(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.readLine(); // Lê uma linha da entrada e a retorna como String
        } catch (IOException e) {
            System.err.println("Erro ao ler a mensagem do cliente.");
            throw e; // Relança a exceção para ser tratada externamente
        }
    }

    /**
     * Simple message that allows to print a message using a nice and informative format. Apart from the text message
     * itself, it also prints the time and client address.
     *
     * @param socket  accepted socket
     * @param message a text message to print
     */
    public static void printMessage(Socket socket, String message) {
        InetAddress clientAddress = socket.getInetAddress();
        System.out.print(now().format(TIME_FORMATTER) + " ");
        System.out.printf("[%s]", clientAddress.getHostAddress());
        System.out.println(" -- " + message);
    }

}

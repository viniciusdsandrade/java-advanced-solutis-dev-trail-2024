package com.restful.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/// Classe principal da aplicação Spring Boot para o projeto Screenmatch.
///
/// A classe 'ScreenmatchApplication' é o ponto de entrada da aplicação e implementa a interface
/// 'CommandLineRunner' para executar o código inicial após a aplicação ser iniciada.
/// Ela inicia o contexto do Spring Boot e, após a inicialização, invoca o metodo 'exibMenu()'
/// da classe 'Main' para exibir o menu principal.
///
/// @see org.springframework.boot.CommandLineRunner
/// @see SpringApplication
@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    /// Metodo principal que inicia a aplicação Spring Boot.
    ///
    /// O metodo 'main' utiliza a classe 'SpringApplication' para iniciar o contexto da aplicação
    /// e rodar a aplicação Spring Boot. Todos os componentes definidos no projeto serão
    /// automaticamente carregados e configurados.
    ///
    /// @param args Argumentos de linha de comando (não utilizados diretamente neste contexto).
    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

    /// Metodo executado após a inicialização do Spring Boot.
    ///
    /// A implementação do metodo 'run' da interface 'CommandLineRunner' é chamada
    /// automaticamente após a aplicação ser iniciada.
    /// Neste caso, o metodo cria uma
    /// instância da classe 'Main' e chama o metodo 'exibMenu()' para iniciar o menu principal
    /// da aplicação.
    ///
    /// @param args Argumentos de linha de comando (não utilizados diretamente neste contexto).
    /// @throws Exception Caso ocorra algum erro durante a execução.
    @Override
    public void run(String... args) throws Exception {
        Main main = new Main();
        main.exibMenu();
    }
}

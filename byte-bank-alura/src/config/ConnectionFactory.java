package config;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

/**
 * Classe
 * responsável pela criação e gerenciamento de conexões com o banco de dados MySQL.
 * Utiliza o padrão de projeto Singleton para garantir que apenas uma instância de conexão
 * seja criada por vez.
 * <p>
 * Agora, esta classe utiliza o HikariCP para o pool de conexões, melhorando o desempenho e a
 * gestão de recursos.
 * </p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * Connection connection = new ConnectionFactory().recuperarConexao();
 * }</pre>
 */
public class ConnectionFactory {

    /**
     * Nome de usuário utilizado para acessar o banco de dados.
     */
    private static final String USER = "root";

    /**
     * Senha padrão para acessar o banco de dados.
     */
    private static final String PASSWORD = "GhostSthong567890@";

    /**
     * Endereço do host onde o banco de dados MySQL está sendo executado.
     */
    private static final String HOST = "localhost";

    /**
     * Porta utilizada para conexão com o banco de dados MySQL.
     */
    private static final String PORT = "3307";

    /**
     * Nome do banco de dados.
     */
    private static final String DATABASE = "db_byte_bank";

    /**
     * URL de conexão com o banco de dados.
     */
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;


    public Connection recuperarConexao() {
        try {
            return getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

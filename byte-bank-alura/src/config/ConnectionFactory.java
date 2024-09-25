package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe responsável pela criação e gerenciamento de conexões com o banco de dados MySQL.
 * Utiliza o HikariCP para o pool de conexões, melhorando o desempenho e a gestão de recursos.
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

    /**
     * Número máximo de conexões no pool.
     */
    private static final int MAX_POOL_SIZE = 10;

    /**
     * Retorna uma conexão com o banco de dados a partir do pool de conexões.
     *
     * @return Uma conexão com o banco de dados.
     */
    public Connection recuperarConexao() {
        try {
            return createDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cria e configura o pool de conexões HikariCP.
     *
     * @return O pool de conexões HikariCP configurado.
     */
    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(MAX_POOL_SIZE);

        return new HikariDataSource(config);
    }
}
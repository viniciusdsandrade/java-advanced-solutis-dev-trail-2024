package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

/// A classe `ConnectionFactory` é responsável pela criação e gerenciamento de conexões com o banco de dados MySQL.
///
/// Utiliza o HikariCP, um pool de conexões altamente eficiente, para melhorar o desempenho
/// e otimizar o uso de recursos ao lidar com múltiplas conexões simultâneas.
public class ConnectionFactory {

    /// Pool de conexões reutilizável.
    ///
    /// O pool de conexões é gerido pelo HikariCP, que permite a reutilização de conexões abertas,
    /// reduzindo o overhead de criar e destruir conexões repetidamente.
    private static final HikariDataSource dataSource;

    // Variáveis de configuração
    private static final int MAX_POOL_SIZE = 10;
    private static final long CONNECTION_TIMEOUT = 30000; // 30 segundos
    private static final long IDLE_TIMEOUT = 600000; // 10 minutos
    private static final long MAX_LIFETIME = 1800000; // 30 minutos

    static {
        // Carrega as variáveis do arquivo .env
        Dotenv dotenv = Dotenv.load();

        // Obtém as credenciais do .env para maior segurança
        String USER = dotenv.get("DB_USER");
        String PASSWORD = dotenv.get("DB_PASSWORD");
        String HOST = dotenv.get("DB_HOST");
        String PORT = dotenv.get("DB_PORT");
        String DATABASE = dotenv.get("DB_NAME");

        // Adicione logs para verificar se as variáveis estão sendo carregadas corretamente
        System.out.println("USER: " + USER);
        System.out.println("PASSWORD: " + PASSWORD);
        System.out.println("HOST: " + HOST);
        System.out.println("PORT: " + PORT);
        System.out.println("DATABASE: " + DATABASE);

        if (USER == null || PASSWORD == null || HOST == null || PORT == null || DATABASE == null) {
            throw new RuntimeException("Variáveis de ambiente não carregadas corretamente.");
        }

        String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(MAX_POOL_SIZE);

        // Configurações adicionais
        config.setConnectionTimeout(CONNECTION_TIMEOUT); // 30 segundos para timeout de conexão
        config.setIdleTimeout(IDLE_TIMEOUT); // 10 minutos antes de liberar conexões inativas
        config.setMaxLifetime(MAX_LIFETIME); // 30 minutos como tempo de vida máximo de uma conexão no pool

        // Inicializa o pool de conexões
        dataSource = new HikariDataSource(config);
    }

    /// Retorna uma conexão com o banco de dados a partir do pool de conexões.
    ///
    /// Este metodo é responsável por fornecer uma conexão ativa com o banco de dados MySQL.
    /// As conexões são obtidas do pool gerido pelo HikariCP, garantindo alta eficiência.
    /// Caso ocorra algum problema ao recuperar a conexão, uma exceção é lançada.
    ///
    /// @return Uma conexão reutilizável com o banco de dados.
    /// @throws RuntimeException Se ocorrer um erro ao recuperar a conexão.
    public Connection recuperarConexao() {
        try {
            return dataSource.getConnection(); // Reutiliza o pool de conexões existente
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao recuperar a conexão com o banco de dados", e);
        }
    }

    /// Metodo para fechar o pool de conexões e liberar recursos quando a aplicação for finalizada.
    ///
    /// Este metodo deve ser chamado quando a aplicação estiver sendo desligada,
    /// garantindo que todas as conexões do pool sejam fechadas corretamente e os recursos liberados.
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

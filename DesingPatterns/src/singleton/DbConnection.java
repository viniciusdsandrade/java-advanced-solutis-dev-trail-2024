package singleton;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static singleton.DatabaseFactory.getDatabaseConfig;

/// Esta classe implementa o padrão Singleton para fornecer uma única instância
/// de conexão com o banco de dados para cada configuração de banco de dados
/// especificada.
///
/// Ela utiliza o HikariCP como pool de conexões, gerenciando e otimizando o
/// acesso ao banco de dados.
public class DbConnection {

    /// Mapa que armazena as instâncias de DbConnection, utilizando a configuração
    /// do banco de dados (DatabaseConfig) como chave. Isso garante que haja apenas
    /// uma instância para cada configuração.
    private static final Map<DatabaseConfig, DbConnection> instances = new HashMap<>();

    /// DataSource do HikariCP, responsável por gerenciar o pool de conexões.
    private final HikariDataSource dataSource;

    /// Construtor privado para evitar a criação direta de instâncias da classe.
    ///
    /// Inicializa o HikariDataSource com as configurações fornecidas.
    ///
    /// @param dbConfig Objeto DatabaseConfig contendo as informações de
    /// configuração do banco de dados.
    private DbConnection(DatabaseConfig dbConfig) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.getJdbcUrl());
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setMaximumPoolSize(10); // Define o número máximo de conexões no pool
        config.setMinimumIdle(2); // Define o número mínimo de conexões inativas no pool
        config.setIdleTimeout(30000); // Define o tempo máximo (em milissegundos) que uma conexão pode ficar inativa
        config.setConnectionTimeout(30000); // Define o tempo máximo (em milissegundos) para obter uma conexão do pool

        dataSource = new HikariDataSource(config);
    }

    /// Metodo estático que retorna a instância única de DbConnection para a
    /// configuração de banco de dados especificada.
    ///
    /// O metodo é sincronizado para garantir que apenas uma thread crie a instância
    /// por vez, evitando problema de concorrência.
    ///
    /// @param dbType   Tipo do banco de dados (ex: "mysql", "postgresql").
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados.
    /// @param username Nome de usuário do banco de dados.
    /// @param password Senha do banco de dados.
    /// @return Instância única de DbConnection para a configuração especificada.
    public static synchronized DbConnection getInstance(String dbType,
                                                        String jdbcUrl,
                                                        String username,
                                                        String password) {
        DatabaseConfig dbConfig = getDatabaseConfig(dbType, jdbcUrl, username, password);
        // Verifica se já existe uma instância para a configuração do banco de dados
        return instances.computeIfAbsent(dbConfig, DbConnection::new);
    }

    /// Metodo para obter uma conexão com o banco de dados do pool.
    ///
    /// @return Conexão com o banco de dados.
    /// @throws SQLException Se ocorrer um erro ao obter a conexão.
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /// Metodo para fechar o DataSource e liberar os recursos.
    ///
    /// Deve ser chamado quando a aplicação não precisar mais acessar o banco
    /// de dados.
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
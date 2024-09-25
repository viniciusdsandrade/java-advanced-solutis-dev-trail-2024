package singleton;

/// Classe que representa as configurações específicas para um banco de dados SQL Server.
public class SQLServerConfig extends DatabaseConfig {

    /// Construtor da classe.
    ///
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados SQL Server.
    /// @param username Nome de usuário do banco de dados SQL Server.
    /// @param password Senha do banco de dados SQL Server.
    public SQLServerConfig(String jdbcUrl, String username, String password) {
        super(jdbcUrl, username, password);
    }
}
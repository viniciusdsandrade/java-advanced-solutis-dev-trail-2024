package singleton;

/// Classe que representa as configurações específicas para um banco de dados MySQL.
public class MySQLConfig extends DatabaseConfig {

    /// Construtor da classe.
    ///
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados MySQL.
    /// @param username Nome de usuário do banco de dados MySQL.
    /// @param password Senha do banco de dados MySQL.
    public MySQLConfig(String jdbcUrl, String username, String password) {
        super(jdbcUrl, username, password);
    }
}
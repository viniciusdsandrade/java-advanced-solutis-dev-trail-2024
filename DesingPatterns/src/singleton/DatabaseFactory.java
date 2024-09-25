package singleton;

/// Fábrica para criação de configurações de banco de dados.
public class DatabaseFactory {

    /// Retorna a configuração de banco de dados apropriada com base no tipo
    /// especificado.
    ///
    /// @param dbType   Tipo do banco de dados (ex: "mysql", "postgresql").
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados.
    /// @param username Nome de usuário do banco de dados.
    /// @param password Senha do banco de dados.
    /// @return Instância de `DatabaseConfig` correspondente ao tipo de banco de
    /// dados.
    /// @throws IllegalArgumentException Se o tipo de banco de dados for inválido
    /// ou não suportado.
    public static DatabaseConfig getDatabaseConfig(String dbType, String jdbcUrl, String username, String password) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> new MySQLConfig(jdbcUrl, username, password);
            case "postgresql" -> new PostgreSQLConfig(jdbcUrl, username, password);
            case "sqlserver" -> new SQLServerConfig(jdbcUrl, username, password);
            case "" -> throw new IllegalArgumentException("Tipo de banco de dados não pode ser nulo ou vazio.");
            case null -> throw new IllegalArgumentException("Tipo de banco de dados não pode ser nulo ou vazio.");
            case String s -> throw new IllegalArgumentException("Tipo de banco de dados não suportado: " + s);
        };
    }
}
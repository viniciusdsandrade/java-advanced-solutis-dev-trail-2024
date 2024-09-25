import singleton.DbConnection;

import static singleton.DbConnection.getInstance;
import java.sql.Connection;
import java.sql.SQLException;

public static void main(String[] ignoredArgs) {
    DbConnection mysqlConnection = getInstance(
            "mysql",
            "jdbc:mysql://localhost:3307/db_test_design_patterns",
            "root",
            "GhostSthong567890@"
    );

    try (Connection _ = mysqlConnection.getConnection()) {
        System.out.println("Conexão com o banco de dados MySQL estabelecida com sucesso!");
    } catch (SQLException e) {
        System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
    } finally {
        mysqlConnection.close();
    }
}
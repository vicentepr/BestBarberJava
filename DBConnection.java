import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Alterar com o seu banco de dados
    private static final String USER = "postgres"; // Alterar com o seu usuário
    private static final String PASSWORD = "12345"; // Alterar com a sua senha

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar com o banco de dados", e);
        }
    }
}
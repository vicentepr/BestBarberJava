import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres"; // Substitua pelo nome do seu banco
        String usuario = "postgres"; // Substitua pelo usuário do PostgreSQL
        String senha = "12345"; // Substitua pela senha do PostgreSQL

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {
            System.out.println("Conexão bem-sucedida!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package Controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CadastroCliente {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Capturar dados do cliente
        System.out.print("Digite o nome do cliente: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o e-mail do cliente: ");
        String email = scanner.nextLine();

        System.out.print("Digite o telefone do cliente: ");
        String telefone = scanner.nextLine();

        System.out.print("Digite a senha do cliente: ");
        String senha = scanner.nextLine();

        // Definindo a URL do banco de dados, usuário e senha
        String url = "jdbc:postgresql://localhost:5432/postgres"; // URL do banco
        String user = "postgres"; // Usuário do banco
        String password = "12345"; // A nova senha que você definiu

        try {
            // Estabelecendo a conexão com o banco de dados
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão bem-sucedida!");

            // Comando SQL para inserir os dados na tabela "clientes"
            String sql = "INSERT INTO clientes (nome, email, telefone, senha) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Inserindo os dados do cliente
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, telefone);
            preparedStatement.setString(4, senha);

            // Executando a inserção
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " linha(s) inserida(s).");

            // Fechando a conexão
            connection.close();

        } catch (SQLException e) {
            // Caso ocorra um erro, exibe a mensagem
            System.out.println("Erro na conexão: " + e.getMessage());
        }
    }
}

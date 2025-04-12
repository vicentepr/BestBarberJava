import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cliente extends Usuario {

    private String telefone;
    private String email;

    public Cliente(String nome, String telefone, String email, String senha) {
        super(nome, senha, TipoUsuario.CLIENTE);
        this.telefone = telefone;
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public static void cadastrarNovoCliente(String nome, String telefone, String email, String senha) {
        Cliente novoCliente = new Cliente(nome, telefone, email, senha);

        // Salva na lista de usuários
        novoCliente.salvar();

        // Salva no banco de dados
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO clientes (nome, telefone, email, senha) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar cliente no banco: " + e.getMessage());
        }
    }

    public static Cliente buscarClienteNoBanco(String nome) {
        String sql = "SELECT * FROM clientes WHERE nome = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String senha = rs.getString("senha");

                return new Cliente(nome, telefone, email, senha);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente no banco: " + e.getMessage());
        }

        return null;
    }
    public static Cliente buscarClientePorNomeOuEmail(String nomeOuEmail) {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT * FROM clientes WHERE nome = ? OR email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nomeOuEmail);
            stmt.setString(2, nomeOuEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String senha = rs.getString("senha");

                return new Cliente(nome, telefone, email, senha);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return null;
    }
}
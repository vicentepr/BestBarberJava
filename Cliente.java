package Model;

import jakarta.persistence.*;
import Repository.ConexaoBancoDados1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entidade que representa um cliente no sistema da barbearia.
 */
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;

    // 🔹 Construtor padrão (obrigatório para JPA)
    public Cliente() {
    }

    // 🔹 Construtor parametrizado
    public Cliente(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    // 🔹 Construtor de cópia
    public Cliente(Cliente outro) {
        this.nome = outro.nome;
        this.email = outro.email;
        this.telefone = outro.telefone;
    }

    // 🔹 Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // 🔹 Representação textual útil para depuração
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }

    // 🔹 Buscar cliente por nome ou e-mail no banco
    public static Cliente buscarClientePorNomeOuEmail(String login) {
        try (Connection conexao = ConexaoBancoDados1.conectar()) {
            String sql = "SELECT * FROM clientes WHERE nome = ? OR email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                return new Cliente(nome, email, telefone);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Cadastrar novo cliente no banco
    public static void cadastrarNovoCliente(String nome, String telefone, String email, String senha) {
        try (Connection conexao = ConexaoBancoDados1.conectar()) {
            String sql = "INSERT INTO clientes (nome, telefone, email, senha) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Cliente cadastrado com sucesso!");
            } else {
                System.out.println("Erro ao cadastrar cliente.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }
}

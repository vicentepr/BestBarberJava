import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Barbearia {
    private List<Agendamento> agendamentos = new ArrayList<>();

    public void adicionarAgendamento(Agendamento agendamento) {
        agendamentos.add(agendamento);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO agendamentos (nome, servico, preco, email, telefone, datahora) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, agendamento.getCliente().getNome());
            stmt.setString(2, agendamento.getServico().getNome());        // ✅ atualizado
            stmt.setDouble(3, agendamento.getServico().getPreco());       // ✅ atualizado
            stmt.setString(4, agendamento.getCliente().getEmail());
            stmt.setString(5, agendamento.getCliente().getTelefone());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(agendamento.getDataHora()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    public void listarAgendamentos() {
        System.out.println("===== Lista de Agendamentos =====");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT nome, servico, preco, email, telefone, datahora FROM agendamentos ORDER BY datahora";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            boolean encontrou = false;

            while (rs.next()) {
                String nome = rs.getString("nome");
                String servico = rs.getString("servico");
                double preco = rs.getDouble("preco");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                LocalDateTime dataHora = rs.getTimestamp("datahora").toLocalDateTime();

                System.out.println("Cliente: " + nome);
                System.out.println("Serviço: " + servico);
                System.out.println("Preço: R$ " + preco);
                System.out.println("Email: " + email);
                System.out.println("Telefone: " + telefone);
                System.out.println("Data e Hora: " + dataHora);
                System.out.println("----------------------------------");
                encontrou = true;
            }

            if (!encontrou) {
                System.out.println("Nenhum agendamento encontrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar agendamentos: " + e.getMessage());
        }
    }

    public void cancelarAgendamento(LocalDateTime dataHora) {
        agendamentos.removeIf(a -> a.getDataHora().equals(dataHora));

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM agendamentos WHERE datahora = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataHora));
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                System.out.println("Nenhum agendamento encontrado com essa data.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }

    public boolean verificarDisponibilidade(LocalDateTime dataHora) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM agendamentos WHERE datahora = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataHora));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar disponibilidade: " + e.getMessage());
        }
        return false;
    }
}

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Funcionario> funcionarios = new ArrayList<>();
    private static final Barbearia barbearia = new Barbearia();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        usuarios.add(new Usuario("admin", "123456", TipoUsuario.ADMIN));
        funcionarios.add(new Funcionario("João"));
        funcionarios.add(new Funcionario("Maria"));

        while (true) {
            System.out.println("===== Bem-Vindo à Best Barber =====");
            System.out.println("1 - Fazer Login");
            System.out.println("2 - Cadastrar-se");
            System.out.print("Escolha uma opção: ");
            int opcaoMenuInicial = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoMenuInicial) {
                case 1 -> realizarLogin();
                case 2 -> cadastrarNovoCliente();
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void realizarLogin() {
        System.out.println("===== Sistema de Login =====");
        System.out.print("Usuário (nome ou e-mail): ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuarioAutenticado = autenticarUsuario(login, senha);
        if (usuarioAutenticado == null) {
            System.out.println("Login falhou! Verifique suas credenciais.");
            return;
        }

        System.out.println("Bem-vindo, " + usuarioAutenticado.getNome() + "!");

        if (usuarioAutenticado.getTipo() == TipoUsuario.ADMIN) {
            menuAdmin();
        } else if (usuarioAutenticado.getTipo() == TipoUsuario.CLIENTE) {
            Cliente cliente = Cliente.buscarClientePorNomeOuEmail(login);
            if (cliente != null) {
                mostrarAgendamentosDoDia(cliente);
                menuCliente(cliente);
            } else {
                System.out.println("Erro ao buscar informações do cliente.");
            }
        }
    }

    private static Usuario autenticarUsuario(String login, String senha) {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT nome, senha FROM clientes WHERE nome = ? OR email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String senhaBanco = rs.getString("senha");
                if (senhaBanco.equals(senha)) {
                    return new Usuario(nome, senha, TipoUsuario.CLIENTE);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar cliente no banco: " + e.getMessage());
        }

        // Verifica se é admin
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equals(login) && usuario.autenticar(senha)) {
                return usuario;
            }
        }

        return null;
    }

    private static void mostrarAgendamentosDoDia(Cliente cliente) {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT servico, datahora, funcionario FROM agendamentos " +
                    "WHERE email = ? AND DATE(datahora) = CURRENT_DATE AND status = 'Agendado'";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cliente.getEmail());

            ResultSet rs = stmt.executeQuery();

            boolean encontrou = false;
            System.out.println("\n📅 Agendamentos para hoje:");
            while (rs.next()) {
                encontrou = true;
                String servico = rs.getString("servico");
                Timestamp datahora = rs.getTimestamp("datahora");
                String funcionario = rs.getString("funcionario");

                System.out.printf("✔️ %s com %s às %s\n", servico, funcionario,
                        datahora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }

            if (!encontrou) {
                System.out.println("Nenhum agendamento marcado para hoje.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar agendamentos do dia: " + e.getMessage());
        }
    }

    private static void cadastrarNovoCliente() {
        System.out.println("===== Cadastro de Novo Cliente =====");
        System.out.print("Digite seu nome: ");
        String nome = scanner.nextLine();

        System.out.print("Digite seu telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        System.out.print("Digite uma senha: ");
        String senha = scanner.nextLine();

        Cliente.cadastrarNovoCliente(nome, telefone, email, senha);
        System.out.println("Cadastro realizado com sucesso! Retornando ao menu inicial...");
    }

    private static void menuCliente(Cliente cliente) {
        int opcao;
        do {
            System.out.println("\n===== Menu Cliente =====");
            System.out.println("1 - Agendar serviço");
            System.out.println("2 - Cancelar agendamento");
            System.out.println("3 - Ver agendamentos");
            System.out.println("4 - Remarcar agendamento");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> agendarServico(cliente);
                case 2 -> cancelarAgendamento();
                case 3 -> listarAgendamentosNoBanco(cliente);
                case 4 -> remarcarAgendamento(cliente);
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }


    private static void menuAdmin() {
        while (true) {
            System.out.println("\n===== Menu Administrador =====");
            System.out.println("1 - Ver Agendamentos");
            System.out.println("2 - Cancelar Agendamento");
            System.out.println("3 - Cadastrar Funcionário");
            System.out.println("4 - Gerenciar Serviços");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> listarAgendamentosNoBanco(null);
                case 2 -> cancelarAgendamento();
                case 3 -> cadastrarFuncionario();
                case 4 -> menuGerenciarServicos();
                case 5 -> {
                    System.out.println("Saindo... Retornando ao login.");
                    return;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void menuGerenciarServicos() {
        while (true) {
            System.out.println("\n--- Gerenciar Serviços ---");
            System.out.println("1 - Adicionar Serviço");
            System.out.println("2 - Editar Serviço");
            System.out.println("3 - Voltar");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> adicionarServico();
                case 2 -> editarServico();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void adicionarServico() {
        System.out.println("\n== Adicionar Novo Serviço ==");
        System.out.print("Nome do serviço: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição do serviço: ");
        String descricao = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine();

        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "INSERT INTO servicos (nome, descricao, preco) VALUES (?, ?, ?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, descricao);
            stmt.setDouble(3, preco);
            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                System.out.println("✅ Serviço adicionado com sucesso!");
            } else {
                System.out.println("❌ Falha ao adicionar serviço.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar serviço: " + e.getMessage());
        }
    }

    private static void editarServico() {
        System.out.println("\n== Editar Serviço ==");

        List<Integer> ids = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String buscar = "SELECT id, nome, descricao, preco FROM servicos";
            PreparedStatement stmt = conexao.prepareStatement(buscar);
            ResultSet rs = stmt.executeQuery();

            int index = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                double preco = rs.getDouble("preco");

                System.out.printf("%d - %s | %s | R$ %.2f\n", index, nome, descricao, preco);
                ids.add(id);
                index++;
            }

            if (ids.isEmpty()) {
                System.out.println("Nenhum serviço cadastrado.");
                return;
            }

            System.out.print("Escolha o número do serviço para editar: ");
            int escolha = scanner.nextInt();
            scanner.nextLine();

            if (escolha < 1 || escolha > ids.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            int idSelecionado = ids.get(escolha - 1);

            System.out.print("Novo nome (deixe vazio para manter): ");
            String novoNome = scanner.nextLine();

            System.out.print("Nova descrição (deixe vazio para manter): ");
            String novaDescricao = scanner.nextLine();

            System.out.print("Novo preço (-1 para manter): ");
            double novoPreco = scanner.nextDouble();
            scanner.nextLine();

            String update = "UPDATE servicos SET nome = COALESCE(NULLIF(?, ''), nome), " +
                    "descricao = COALESCE(NULLIF(?, ''), descricao), " +
                    "preco = CASE WHEN ? >= 0 THEN ? ELSE preco END WHERE id = ?";
            PreparedStatement updateStmt = conexao.prepareStatement(update);
            updateStmt.setString(1, novoNome);
            updateStmt.setString(2, novaDescricao);
            updateStmt.setDouble(3, novoPreco);
            updateStmt.setDouble(4, novoPreco);
            updateStmt.setInt(5, idSelecionado);

            if (updateStmt.executeUpdate() > 0) {
                System.out.println("✅ Serviço atualizado com sucesso!");
            } else {
                System.out.println("❌ Falha ao atualizar serviço.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao editar serviço: " + e.getMessage());
        }
    }
    private static void cadastrarFuncionario() {
        System.out.print("Digite o nome do novo funcionário: ");
        String nomeFuncionario = scanner.nextLine();

        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "INSERT INTO funcionarios (nome) VALUES (?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nomeFuncionario);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Funcionário cadastrado com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    private static List<Funcionario> listarFuncionariosDoBanco() {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT id, nome FROM funcionarios";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                lista.add(new Funcionario(id, nome));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar funcionários: " + e.getMessage());
        }
        return lista;
    }

    private static void agendarServico(Cliente cliente) {
        System.out.println("Escolha o serviço que deseja agendar:");
        for (int i = 0; i < TipoServico.values().length; i++) {
            TipoServico tipoServico = TipoServico.values()[i];
            System.out.println((i + 1) + " - " + tipoServico.getDescricao());
        }

        System.out.print("Digite o número do serviço desejado: ");
        int opcaoServico = scanner.nextInt();
        scanner.nextLine();

        if (opcaoServico < 1 || opcaoServico > TipoServico.values().length) {
            System.out.println("Opção inválida. Tente novamente.");
            return;
        }

        TipoServico tipoServicoEscolhido = TipoServico.values()[opcaoServico - 1];

        List<Funcionario> listaFuncionarios = listarFuncionariosDoBanco();
        if (listaFuncionarios.isEmpty()) {
            System.out.println("Nenhum funcionário cadastrado.");
            return;
        }

        System.out.println("Escolha um funcionário:");
        for (Funcionario f : listaFuncionarios) {
            System.out.println(f);
        }

        System.out.print("Digite o ID do funcionário desejado: ");
        int idEscolhido = scanner.nextInt();
        scanner.nextLine();

        Funcionario funcionarioEscolhido = listaFuncionarios.stream()
                .filter(f -> f.getId() == idEscolhido)
                .findFirst()
                .orElse(null);

        if (funcionarioEscolhido == null) {
            System.out.println("Funcionário inválido.");
            return;
        }

        System.out.print("Digite a data do agendamento (formato: dd-MM-yyyy): ");
        String data = scanner.nextLine();
        System.out.print("Digite a hora do agendamento (formato: HH:mm): ");
        String hora = scanner.nextLine();

        try {
            String dataHoraString = data + " " + hora;
            LocalDateTime dataHoraAgendamento = LocalDateTime.parse(dataHoraString, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            if (barbearia.verificarDisponibilidade(dataHoraAgendamento)) {
                System.out.println("Horário indisponível. Tente outro horário.");
                return;
            }

            try (Connection conexao = ConexaoBancoDados.conectar()) {
                String sql = "INSERT INTO agendamentos (nome, servico, preco, email, telefone, datahora, status, funcionario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conexao.prepareStatement(sql);
                stmt.setString(1, cliente.getNome());
                stmt.setString(2, tipoServicoEscolhido.getNome());
                stmt.setDouble(3, tipoServicoEscolhido.getPreco());
                stmt.setString(4, cliente.getEmail());
                stmt.setString(5, cliente.getTelefone());
                stmt.setTimestamp(6, Timestamp.valueOf(dataHoraAgendamento));
                stmt.setString(7, "Agendado");
                stmt.setString(8, funcionarioEscolhido.getNome());

                stmt.executeUpdate();
                System.out.println("Agendamento realizado com sucesso para " + tipoServicoEscolhido.getNome() + " com " + funcionarioEscolhido.getNome() + "!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data ou hora inválido. Tente novamente.");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    private static void cancelarAgendamento() {
        System.out.println("===== Cancelar Agendamento (Admin) =====");
        listarAgendamentosNoBanco(null); // Mostra todos os agendamentos

        System.out.print("Digite o ID do agendamento que deseja cancelar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "DELETE FROM agendamentos WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Agendamento cancelado com sucesso!");
            } else {
                System.out.println("❌ Agendamento não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }

    private static void listarAgendamentosNoBanco(Cliente cliente) {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql;
            PreparedStatement stmt;

            if (cliente != null) {
                sql = "SELECT id, servico, datahora, funcionario FROM agendamentos " +
                        "WHERE email = ? AND status = 'Agendado' ORDER BY datahora";
                stmt = conexao.prepareStatement(sql);
                stmt.setString(1, cliente.getEmail());
            } else {
                // ADMIN visualiza todos os agendamentos
                sql = "SELECT id, nome, servico, datahora, funcionario FROM agendamentos " +
                        "WHERE status = 'Agendado' ORDER BY datahora";
                stmt = conexao.prepareStatement(sql);
            }

            ResultSet rs = stmt.executeQuery();
            boolean encontrou = false;
            System.out.println("\n📋 Agendamentos:");

            while (rs.next()) {
                encontrou = true;

                int id = rs.getInt("id");
                String servico = rs.getString("servico");
                Timestamp datahora = rs.getTimestamp("datahora");
                String funcionario = rs.getString("funcionario");

                if (cliente != null) {
                    System.out.printf("ID: %d | %s com %s às %s\n", id, servico, funcionario,
                            datahora.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                } else {
                    String nomeCliente = rs.getString("nome"); // ✅ correto
                    System.out.printf("ID: %d | %s | %s com %s às %s\n", id, nomeCliente, servico, funcionario,
                            datahora.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }

            if (!encontrou) {
                System.out.println("Nenhum agendamento encontrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar agendamentos: " + e.getMessage());
        }
    }

    private static void verificarAgendamentoDoDia(Cliente cliente) {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT servico, datahora, funcionario FROM agendamentos " +
                    "WHERE email = ? AND status = 'Agendado' AND DATE(datahora) = CURRENT_DATE";

            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cliente.getEmail());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String servico = rs.getString("servico");
                Timestamp datahora = rs.getTimestamp("datahora");
                String funcionario = rs.getString("funcionario");

                System.out.println("\n📅 Você tem um agendamento para hoje:");
                System.out.printf("Serviço: %s | Funcionário: %s | Horário: %s\n",
                        servico, funcionario,
                        datahora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar agendamentos do dia: " + e.getMessage());
        }
    }

private static void remarcarAgendamento(Cliente cliente) {
    try (Connection conexao = ConexaoBancoDados.conectar()) {
        String buscarAgendamentos = "SELECT id, servico, datahora, funcionario FROM agendamentos " +
                "WHERE email = ? AND status = 'Agendado'";
        PreparedStatement stmt = conexao.prepareStatement(buscarAgendamentos);
        stmt.setString(1, cliente.getEmail());
        ResultSet rs = stmt.executeQuery();

        List<Integer> ids = new ArrayList<>();
        int count = 1;

        System.out.println("\n===== Seus Agendamentos =====");
        while (rs.next()) {
            ids.add(rs.getInt("id"));
            System.out.printf("%d - %s com %s em %s\n", count++,
                    rs.getString("servico"),
                    rs.getString("funcionario"),
                    rs.getTimestamp("datahora").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        if (ids.isEmpty()) {
            System.out.println("Você não possui agendamentos.");
            return;
        }

        System.out.print("Escolha o número do agendamento para remarcar: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        if (opcao < 1 || opcao > ids.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        int agendamentoId = ids.get(opcao - 1);
        System.out.print("Nova data e hora (dd/MM/yyyy HH:mm): ");
        String novaDataHoraStr = scanner.nextLine();

        LocalDateTime novaDataHora;
        try {
            novaDataHora = LocalDateTime.parse(novaDataHoraStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Formato inválido.");
            return;
        }

        // Verifica conflito de horário com mesmo funcionário
        String funcionario = buscarFuncionario(conexao, agendamentoId);
        if (funcionario == null || !horarioDisponivel(conexao, funcionario, novaDataHora, agendamentoId)) {
            System.out.println("Esse horário não está disponível para o funcionário.");
            return;
        }

        String atualizar = "UPDATE agendamentos SET datahora = ? WHERE id = ?";
        PreparedStatement updateStmt = conexao.prepareStatement(atualizar);
        updateStmt.setTimestamp(1, Timestamp.valueOf(novaDataHora));
        updateStmt.setInt(2, agendamentoId);

        if (updateStmt.executeUpdate() > 0) {
            System.out.println("Agendamento remarcado com sucesso!");
        } else {
            System.out.println("Erro ao remarcar.");
        }

    } catch (SQLException e) {
        System.out.println("Erro: " + e.getMessage());
    }
}
    private static String buscarFuncionario(Connection conexao, int agendamentoId) throws SQLException {
        String sql = "SELECT funcionario FROM agendamentos WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, agendamentoId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getString("funcionario") : null;
    }

    private static boolean horarioDisponivel(Connection conexao, String funcionario, LocalDateTime dataHora, int ignorarId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamentos WHERE funcionario = ? AND datahora = ? AND status = 'Agendado' AND id <> ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, funcionario);
        stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
        stmt.setInt(3, ignorarId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) == 0;
      }
    }
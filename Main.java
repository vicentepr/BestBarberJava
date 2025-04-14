import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

// =======================================================
// Main Class
// =======================================================
public class Main {
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Funcionario> funcionarios = new ArrayList<>();
    private static final Barbearia barbearia = new Barbearia();
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        usuarios.add(new Usuario("admin", "123456", TipoUsuario.ADMIN));
        funcionarios.add(new Funcionario("Joo"));
        funcionarios.add(new Funcionario("Maria"));
        while (true) {
            System.out.println("===== Bem-Vindo à Best Barber =====");
            System.out.println("1 - Fazer Login");
            System.out.println("2 - Cadastrar-se");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                int opcaoMenuInicial = scanner.nextInt();
                scanner.nextLine(); // Limpa buffer
                switch (opcaoMenuInicial) {
                    case 1 -> realizarLogin();
                    case 2 -> cadastrarNovoCliente();
                    default -> System.out.println("❌ Opção inválida. Tente novamente.");
                }
            } else {
                System.out.println("❌ Entrada inválida. Digite apenas números.");
                scanner.nextLine(); // limpar entrada inválida
            }
        }
    }
    // =======================================================
    // Autenticao/Login
    // =======================================================
    private static void realizarLogin() {
        System.out.println("===== Sistema de Login =====");
        System.out.print("Usurio (nome ou e-mail): ");
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
                System.out.println("Erro ao buscar informaes do cliente.");
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
        // Verifica se admin
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
            System.out.println("\nAgendamentos para hoje:");
            while (rs.next()) {
                encontrou = true;
                String servico = rs.getString("servico");
                Timestamp datahora = rs.getTimestamp("datahora");
                String funcionario = rs.getString("funcionario");
                System.out.printf("- %s com %s s %s\n", servico, funcionario,
                        datahora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            if (!encontrou) {
                System.out.println("Nenhum agendamento marcado para hoje.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar agendamentos do dia: " + e.getMessage());

        }
    }
    // =======================================================
    // Cadastro de Novo Cliente
    // =======================================================
    private static void cadastrarNovoCliente() {
        System.out.println("===== Cadastro de Novo Cliente =====");
        System.out.print("Digite seu nome: ");
        String nome = scanner.nextLine();
        String telefone;
        while (true) {
            System.out.print("Digite seu telefone: ");
            telefone = scanner.nextLine();
            if (telefone.matches("\\d{11}")) {
                break;
            } else {
                System.out.println(" Telefone invlido! Deve conter exatamente 11 dgitos numricos.");
            }
        }
        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();
        System.out.print("Digite uma senha: ");
        String senha = scanner.nextLine();
        Cliente.cadastrarNovoCliente(nome, telefone, email, senha);
        System.out.println("Cadastro realizado com sucesso! Retornando ao menu inicial...");
    }
    // Menu do Cliente
    // =======================================================
    private static void menuCliente(Cliente cliente) {
        int opcao;
        do {
            System.out.println("\n===== Menu Cliente =====");
            System.out.println("1 - Agendar servio");
            System.out.println("2 - Cancelar agendamento");
            System.out.println("3 - Ver agendamentos");
            System.out.println("4 - Remarcar agendamento");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma Opo: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1 -> agendarServico(cliente);
                case 2 -> cancelarAgendamento(cliente);
                case 3 -> listarAgendamentosNoBanco(cliente);
                case 4 -> remarcarAgendamento(cliente);
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opo invlida!");
            }
        } while (opcao != 0);
    }
    // =======================================================
    // Menu do Administrador
    // =======================================================
    private static void menuAdmin() {
        while (true) {
            System.out.println("\n===== Menu Administrador =====");
            System.out.println("1 - Ver Agendamentos");
            System.out.println("2 - Cancelar Agendamento");
            System.out.println("3 - Cadastrar Funcionrio");
            System.out.println("4 - Gerenciar Servios");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma Opo: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1 -> listarTodosAgendamentosNoBanco();
                case 2 -> cancelarAgendamento();
                case 3 -> cadastrarFuncionario();
                case 4 -> menuGerenciarServicos();
                case 5 -> {
                    System.out.println("Saindo... Retornando ao login.");
                    return;
                }
                default -> System.out.println("Opo invlida. Tente novamente.");
            }
        }
    }
    private static void menuGerenciarServicos() {
        while (true) {
            System.out.println("\n--- Gerenciar Servios ---");
            System.out.println("1 - Adicionar Servio");
            System.out.println("2 - Editar Servio");
            System.out.println("3 - Voltar");
            System.out.print("Escolha uma Opo: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1 -> adicionarServico();
                case 2 -> editarServico();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Opo invlida.");
            }
        }
    }
    // =======================================================
    // Gerenciamento de Servios
    // =======================================================
    private static void adicionarServico() {
        System.out.println("\n== Adicionar Novo Servio ==");
        System.out.print("Nome do servio: ");
        String nome = scanner.nextLine();
        System.out.print("Descrio do servio: ");
        String descricao = scanner.nextLine();
        System.out.print("Preo: ");
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
                System.out.println(" Servio adicionado com sucesso!");
            } else {
                System.out.println(" Falha ao adicionar servio.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar servio: " + e.getMessage());
        }
    }
    private static void editarServico() {
        System.out.println("\n== Editar Servio ==");
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
                System.out.println("Nenhum servio cadastrado.");
                return;
            }
            System.out.print("Escolha o nmero do servio para editar: ");
            int escolha = scanner.nextInt();
            scanner.nextLine();
            if (escolha < 1 || escolha > ids.size()) {
                System.out.println("Opo invlida.");
                return;
            }
            int idSelecionado = ids.get(escolha - 1);
            System.out.print("Novo nome (deixe vazio para manter): ");
            String novoNome = scanner.nextLine();
            System.out.print("Nova descrio (deixe vazio para manter): ");
            String novaDescricao = scanner.nextLine();
            System.out.print("Novo preo (Digite o preo atual para manter ou outro preo para alterar): ");
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
                System.out.println(" Servio atualizado com sucesso!");
            } else {
                System.out.println(" Falha ao atualizar servio.");
            }
        } catch (SQLException e) {System.out.println("Erro ao editar servio: " + e.getMessage());
        }
    }
    // =======================================================
    // Funcionrios e Agendamento
    // =======================================================
    private static void cadastrarFuncionario() {
        System.out.print("Digite o nome do novo funcionrio: ");
        String nomeFuncionario = scanner.nextLine();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "INSERT INTO funcionarios (nome) VALUES (?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nomeFuncionario);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Funcionrio cadastrado com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar funcionrio: " + e.getMessage());
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
            System.out.println("Erro ao buscar funcionrios: " + e.getMessage());
        }
        return lista;
    }
    // Listar agendamentos do cliente
    private static void listarTodosAgendamentosNoBanco() {
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT id, nome, servico, funcionario, datahora, status FROM agendamentos ORDER BY datahora";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n== Todos os Agendamentos ==");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String servico = rs.getString("servico");
                String funcionario = rs.getString("funcionario");
                LocalDateTime dataHora = rs.getTimestamp("datahora").toLocalDateTime();
                String status = rs.getString("status");
                System.out.printf("ID: %d | Cliente: %s | Servio: %s | Funcionrio: %s | %s | Status: %s\n",
                id, nome, servico, funcionario,
                        dataHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                        status);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar agendamentos: " + e.getMessage());
        }
    }
    // Listar todos os agendamentos (admin)
    private static boolean listarAgendamentosNoBanco(Cliente cliente) {
        boolean encontrou = false;
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT id, servico, datahora, funcionario FROM agendamentos " +
                    "WHERE email = ? AND status = 'Agendado' ORDER BY datahora";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cliente.getEmail());
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nSeus Agendamentos:");
            while (rs.next()) {
                encontrou = true;
                int id = rs.getInt("id");
                String servico = rs.getString("servico");
                String funcionario = rs.getString("funcionario");
                LocalDateTime dataHora = rs.getTimestamp("datahora").toLocalDateTime();
                System.out.printf("ID: %d | %s com %s em %s\n",
                        id, servico, funcionario,
                        dataHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            }
            if (!encontrou) {
                System.out.println(" Voce no possui nenhum agendamento.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar agendamentos: " + e.getMessage());
        }
        return encontrou;
    }
    // =======================================================
    // Agendamento de Servios
    // =======================================================
    private static void agendarServico(Cliente cliente) {
        // Solicita a data do agendamento
        System.out.print("Digite a data do agendamento (dd-MM-yyyy): ");
        String dataString = scanner.nextLine();
        LocalDate dataAgendamento;

        try {
            dataAgendamento = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido.");
            return;
        }

        // Impede agendamentos em datas passadas
        if (dataAgendamento.isBefore(LocalDate.now())) {
            System.out.println("❌ Não é possível agendar para uma data que já passou.");
            return;
        }

        // Solicita o serviço
        List<Map<String, Object>> servicos = new ArrayList<>();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT id, nome, descricao, preco FROM servicos";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int index = 1;
            System.out.println("Escolha o serviço que deseja agendar:");
            while (rs.next()) {
                Map<String, Object> servico = new HashMap<>();
                servico.put("id", rs.getInt("id"));
                servico.put("nome", rs.getString("nome"));
                servico.put("descricao", rs.getString("descricao"));
                servico.put("preco", rs.getDouble("preco"));
                System.out.printf("%d - %s | %s | R$ %.2f\n", index,
                        servico.get("nome"),
                        servico.get("descricao"),
                        servico.get("preco"));
                servicos.add(servico);
                index++;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar serviços: " + e.getMessage());
            return;
        }

        if (servicos.isEmpty()) {
            System.out.println("Nenhum serviço disponível.");
            return;
        }

        // Solicita o número do serviço
        System.out.print("Digite o número do serviço desejado: ");
        int opcaoServico = scanner.nextInt();
        scanner.nextLine();
        if (opcaoServico < 1 || opcaoServico > servicos.size()) {
            System.out.println("Opção inválida. Tente novamente.");
            return;
        }

        Map<String, Object> servicoEscolhido = servicos.get(opcaoServico - 1);

        // Solicita o funcionário
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
                .findFirst().orElse(null);

        if (funcionarioEscolhido == null) {
            System.out.println("Funcionário inválido.");
            return;
        }

        // Listar os horários disponíveis para o funcionário na data escolhida
        List<LocalDateTime> horariosDisponiveis = listarHorariosDisponiveis(funcionarioEscolhido.getNome(), dataAgendamento);

        if (horariosDisponiveis.isEmpty()) {
            System.out.println("Não há horários disponíveis para o funcionário nesta data.");
            return;
        }

        // Exibe os horários disponíveis (apenas horário, sem data)
        System.out.println("Horários disponíveis:");
        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < horariosDisponiveis.size(); i++) {
            LocalDateTime horario = horariosDisponiveis.get(i);
            System.out.printf("%d - %s\n", i + 1, horario.toLocalTime().format(formatadorHora));
        }

        // Solicita o horário
        System.out.print("Escolha o horário desejado: ");
        int opcaoHorario = scanner.nextInt();
        scanner.nextLine();

        if (opcaoHorario < 1 || opcaoHorario > horariosDisponiveis.size()) {
            System.out.println("Horário inválido.");
            return;
        }

        LocalDateTime horarioEscolhido = horariosDisponiveis.get(opcaoHorario - 1);

        // Impede o agendamento se a hora escolhida já passou
        if (horarioEscolhido.isBefore(LocalDateTime.now())) {
            System.out.println("❌ Não é possível agendar para um horário que já passou.");
            return;
        }

        // Insere o agendamento no banco de dados
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "INSERT INTO agendamentos (nome, servico, preco, email, telefone, datahora, status, funcionario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, (String) servicoEscolhido.get("nome"));
            stmt.setDouble(3, (Double) servicoEscolhido.get("preco"));
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getTelefone());
            stmt.setTimestamp(6, Timestamp.valueOf(horarioEscolhido));
            stmt.setString(7, "Agendado");
            stmt.setString(8, funcionarioEscolhido.getNome());
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("✅ Agendamento realizado com sucesso!");
            } else {
                System.out.println("❌ Falha ao realizar o agendamento.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    private static List<LocalDateTime> obterHorariosDisponiveis(Funcionario funcionario) {
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
        LocalDateTime fimDia = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30));

        // Gerar todos os horários possíveis de 30 em 30 minutos
        while (!inicioDia.isAfter(fimDia)) {
            horariosDisponiveis.add(inicioDia);
            inicioDia = inicioDia.plusMinutes(30);
        }

        // Verificar se algum horário já está ocupado
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sqlVerifica = "SELECT datahora FROM agendamentos WHERE funcionario = ? AND status = 'Agendado'";
            PreparedStatement stmtVerifica = conexao.prepareStatement(sqlVerifica);
            stmtVerifica.setString(1, funcionario.getNome());
            ResultSet rsVerifica = stmtVerifica.executeQuery();

            // Remover horários ocupados da lista de disponíveis
            while (rsVerifica.next()) {
                Timestamp ocupado = rsVerifica.getTimestamp("datahora");
                LocalDateTime horarioOcupado = ocupado.toLocalDateTime();
                horariosDisponiveis.removeIf(h -> h.isEqual(horarioOcupado));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar disponibilidade: " + e.getMessage());
        }

        return horariosDisponiveis;
    }
    // =======================================================
    // Cancelar e Remarcar Agendamentos
    // =======================================================
    private static void cancelarAgendamento(int idUsuarioLogado, Cliente Cliente) {
        System.out.println("===== Cancelar Agendamento =====");
        // lista os agendamentos do usurio logado
        listarAgendamentosNoBanco(Cliente); // com letra minscula
        System.out.print("Digite o ID do agendamento que deseja cancelar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "UPDATE agendamentos SET status = 'Cancelado' WHERE id = ? AND id_usuario = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuarioLogado);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println(" Agendamento cancelado com sucesso!");
            } else {
                System.out.println(" Agendamento no encontrado ou voc no tem permisso para cancelar.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }
    // Cancelar agendamento como ADMIN (pode remover qualquer agendamento)
    private static void cancelarAgendamento() {
        System.out.println("===== Cancelar Agendamento (Admin) =====");
        listarTodosAgendamentosNoBanco();// Lista todos os agendamentos
        System.out.print("Digite o ID do agendamento que deseja cancelar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "UPDATE agendamentos SET status = 'Cancelado' WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println(" Agendamento cancelado com sucesso!");
            } else {
                System.out.println(" Agendamento no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }
    private static void remarcarAgendamento(Cliente cliente) {
        List<Map<String, Object>> agendamentos = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "SELECT id, datahora, servico, funcionario FROM agendamentos " +
                    "WHERE email = ? AND status = 'Agendado'";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cliente.getEmail());
            ResultSet rs = stmt.executeQuery();

            System.out.println("Seus agendamentos:");
            while (rs.next()) {
                Map<String, Object> agendamento = new HashMap<>();
                agendamento.put("id", rs.getInt("id"));
                agendamento.put("datahora", rs.getTimestamp("datahora").toLocalDateTime());
                agendamento.put("servico", rs.getString("servico"));
                agendamento.put("funcionario", rs.getString("funcionario"));

                System.out.printf("ID: %d | Data: %s | Serviço: %s | Funcionário: %s\n",
                        agendamento.get("id"),
                        ((LocalDateTime) agendamento.get("datahora")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        agendamento.get("servico"),
                        agendamento.get("funcionario"));

                agendamentos.add(agendamento);
            }

            if (agendamentos.isEmpty()) {
                System.out.println("Você não possui agendamentos para remarcar.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar agendamentos: " + e.getMessage());
            return;
        }

        // Solicita o ID do agendamento a ser remarcado
        System.out.print("Digite o ID do agendamento que deseja remarcar: ");
        int idAgendamento = scanner.nextInt();
        scanner.nextLine();

        Map<String, Object> agendamentoSelecionado = agendamentos.stream()
                .filter(a -> (int) a.get("id") == idAgendamento)
                .findFirst().orElse(null);

        if (agendamentoSelecionado == null) {
            System.out.println("ID inválido.");
            return;
        }

        String funcionario = (String) agendamentoSelecionado.get("funcionario");

        // Solicita a nova data
        System.out.print("Digite a nova data para o agendamento (dd-MM-yyyy): ");
        String dataString = scanner.nextLine();
        LocalDate novaData;

        try {
            novaData = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido.");
            return;
        }

        // Lista os horários disponíveis
        List<LocalDateTime> horariosDisponiveis = listarHorariosDisponiveis(funcionario, novaData);

        if (horariosDisponiveis.isEmpty()) {
            System.out.println("Não há horários disponíveis para esta data.");
            return;
        }

        // Exibe os horários disponíveis (apenas hora)
        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("Horários disponíveis:");
        for (int i = 0; i < horariosDisponiveis.size(); i++) {
            LocalDateTime horario = horariosDisponiveis.get(i);
            System.out.printf("%d - %s\n", i + 1, horario.toLocalTime().format(formatadorHora));
        }

        System.out.print("Escolha o horário desejado: ");
        int opcaoHorario = scanner.nextInt();
        scanner.nextLine();

        if (opcaoHorario < 1 || opcaoHorario > horariosDisponiveis.size()) {
            System.out.println("Horário inválido.");
            return;
        }

        LocalDateTime novoHorario = horariosDisponiveis.get(opcaoHorario - 1);

        // Atualiza o agendamento no banco
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sqlUpdate = "UPDATE agendamentos SET datahora = ? WHERE id = ?";
            PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdate);
            stmtUpdate.setTimestamp(1, Timestamp.valueOf(novoHorario));
            stmtUpdate.setInt(2, idAgendamento);

            int linhas = stmtUpdate.executeUpdate();
            if (linhas > 0) {
                System.out.println("✅ Agendamento remarcado com sucesso!");
            } else {
                System.out.println("❌ Falha ao remarcar agendamento.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remarcar agendamento: " + e.getMessage());
        }
    }

    static List<LocalDateTime> listarHorariosDisponiveis(String nomeFuncionario, LocalDate data) {
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();
        LocalTime horaInicial = LocalTime.of(8, 0);  // 08:00
        LocalTime horaFinal = LocalTime.of(17, 30);  // 17:30
        Duration intervalo = Duration.ofMinutes(30);  // Intervalo de 30 minutos

        // Cria todos os horários possíveis no dia
        for (LocalTime hora = horaInicial; !hora.isAfter(horaFinal); hora = hora.plus(intervalo)) {
            LocalDateTime dataHora = LocalDateTime.of(data, hora);

            // Verifica se o horário já está ocupado para o funcionário
            try (Connection conexao = ConexaoBancoDados.conectar()) {
                String sqlVerificaHorario = "SELECT COUNT(*) FROM agendamentos WHERE funcionario = ? AND datahora = ? AND status = 'Agendado'";
                PreparedStatement stmtVerificaHorario = conexao.prepareStatement(sqlVerificaHorario);
                stmtVerificaHorario.setString(1, nomeFuncionario);
                stmtVerificaHorario.setTimestamp(2, Timestamp.valueOf(dataHora));
                ResultSet rsVerificaHorario = stmtVerificaHorario.executeQuery();
                rsVerificaHorario.next();

                if (rsVerificaHorario.getInt(1) == 0) {
                    horariosDisponiveis.add(dataHora);
                }
            } catch (SQLException e) {
                System.out.println("Erro ao verificar horário: " + e.getMessage());
            }
        }

        return horariosDisponiveis;
    }
    // =======================================================
    // Suporte e Utilidades
    // =======================================================
    private static String buscarFuncionario(Connection conexao, int agendamentoId) throws
            SQLException {
        String sql = "SELECT funcionario FROM agendamentos WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, agendamentoId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getString("funcionario") : null;
    }
    private static boolean horarioDisponivel(Connection conexao, String funcionario, LocalDateTime
            dataHora, int ignorarId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamentos WHERE funcionario = ? AND datahora = ? AND status = 'Agendado' AND id <> ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, funcionario);
        stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
        stmt.setInt(3, ignorarId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) == 0;
    }
    private static void cancelarAgendamento(Cliente cliente) {
        System.out.println("===== Cancelar Agendamento =====");
        if (!listarAgendamentosNoBanco(cliente)) return;
        System.out.print("Digite o ID do agendamento que deseja cancelar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conexao = ConexaoBancoDados.conectar()) {
            String sql = "UPDATE agendamentos SET status = 'Cancelado' WHERE id = ? AND email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, cliente.getEmail());
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println(" Agendamento cancelado com sucesso!");
            } else {
                System.out.println(" Agendamento no encontrado ou no pertence a voc.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }
}
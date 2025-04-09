import java.time.LocalDateTime;

public class Agendamento {
    private Cliente cliente;
    private Servico servico;
    private LocalDateTime dataHora;
    private Funcionario funcionario;
    private String pagamento;

    public Agendamento(Cliente cliente, Servico servico, LocalDateTime dataHora, Funcionario funcionario, String pagamento) {
        this.cliente = cliente;
        this.servico = servico;
        this.dataHora = dataHora;
        this.funcionario = funcionario;
        this.pagamento = pagamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Servico getServico() {
        return servico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public String getFormaPagamento() { // Renomeado de getPagamento para getFormaPagamento
        return pagamento;
    }

    // Método para exibir detalhes do agendamento
    public void exibirAgendamento() {
        System.out.println("Agendamento de " + cliente.getNome());
        System.out.println("Serviço: " + servico.getTipoServico().getNome());
        System.out.println("Data e Hora: " + dataHora);
        System.out.println("Funcionário: " + funcionario.getNome());
        System.out.println("Forma de pagamento: " + pagamento);
    }
}

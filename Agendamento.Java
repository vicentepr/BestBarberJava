package Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Servico servico;

    @ManyToOne
    private Funcionario funcionario;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private FormaPagamento pagamento;

    public Agendamento() {
    }

    public Agendamento(Cliente cliente, Servico servico, LocalDateTime dataHora, Funcionario funcionario, FormaPagamento pagamento) {
        this.cliente = cliente;
        this.servico = servico;
        this.dataHora = dataHora;
        this.funcionario = funcionario;
        this.pagamento = pagamento;
    }

    public Agendamento(Agendamento outro) {
        this.cliente = outro.cliente;
        this.servico = outro.servico;
        this.dataHora = outro.dataHora;
        this.funcionario = outro.funcionario;
        this.pagamento = outro.pagamento;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public FormaPagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(FormaPagamento pagamento) {
        this.pagamento = pagamento;
    }

    public void exibirAgendamento() {
        System.out.println("Agendamento de " + cliente.getNome());
        System.out.println("Serviço: " + servico.getNome());
        System.out.println("Data e Hora: " + dataHora);
        System.out.println("Funcionário: " + funcionario.getNome());
        System.out.println("Forma de pagamento: " + pagamento);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", servico=" + servico +
                ", dataHora=" + dataHora +
                ", funcionario=" + funcionario +
                ", pagamento=" + pagamento +
                '}';
    }
}

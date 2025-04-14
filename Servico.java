public class Servico implements Ihm {
    private int id;
    private String nome;
    private String descricao;
    private double preco;

    public Servico(int id, String nome, String descricao, double preco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    @Override
    public void realizarServico() {
        System.out.println("Realizando o serviço de " + nome + "...");
    }

    @Override
    public String toString() {
        return nome + " | " + descricao + " | R$ " + String.format("%.2f", preco);
    }
}

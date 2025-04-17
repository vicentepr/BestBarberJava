package Model;

import jakarta.persistence.*;

/**
 * Entidade que representa um serviço oferecido pela barbearia.
 */
@Entity
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private double preco;

    // 🔹 Construtor padrão
    public Servico() {
    }

    // 🔹 Construtor parametrizado
    public Servico(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // 🔹 Construtor de cópia
    public Servico(Servico outro) {
        this.nome = outro.nome;
        this.preco = outro.preco;
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                '}';
    }
}

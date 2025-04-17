package Model;

import jakarta.persistence.*;


@Entity
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cargo;

    // 🔹 Construtor padrão (obrigatório para JPA)
    public Funcionario() {
    }

    // 🔹 Construtor com ID e nome (usado ao buscar do banco)
    public Funcionario(Long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.cargo = "Barbeiro"; // valor padrão (ou null)
    }

    // 🔹 Construtor só com nome
    public Funcionario(String nome) {
        this.nome = nome;
        this.cargo = "Barbeiro";
    }

    // 🔹 Construtor completo
    public Funcionario(String nome, String cargo) {
        this.nome = nome;
        this.cargo = cargo;
    }

    // 🔹 Construtor de cópia
    public Funcionario(Funcionario outro) {
        this.nome = outro.nome;
        this.cargo = outro.cargo;
    }

    // Get e Set
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id; // útil se usar em DTOs ou testes
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cargo='" + cargo + '\'' +
                '}';
    }
}

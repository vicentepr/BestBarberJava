public enum TipoServico {
    CORTE("Corte", 50.0),
    BARBA("Barba", 30.0),
    SOBRANCELHA_NAVALHA("Sobrancelha - Navalha", 30.0),
    SOBRANCELHA_PINCA("Sobrancelha - Pinça", 35.0),
    LUZES("Luzes", 120.0),
    ALISAMENTO("Alisamento", 100.0),
    PACOTE_COMPLETO("Pacote Completo", 110.0);  // Preço do pacote completo

    private String nome;
    private double preco;

    TipoServico(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public String getDescricao() {
        if (this == PACOTE_COMPLETO) {
            return "Pacote Completo: Corte, Barba, Sobrancelha (Navalha ou Pinça) - R$ " + preco;
        }
        return nome + " (R$ " + preco + ")";
    }
}
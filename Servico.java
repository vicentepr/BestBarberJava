public class Servico implements Ihm {
    private TipoServico tipoServico;

    public Servico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    @Override
    public void realizarServico() {
        System.out.println("Realizando o serviço de " + tipoServico.getNome() + "...");
    }
}
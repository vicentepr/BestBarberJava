import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class Usuario {
    private String nome;
    private String senha;
    private TipoUsuario tipo;

    // Lista estática para armazenar os usuários
    private static List<Usuario> usuarios = new ArrayList<>();

    public Usuario(String nome, String senha, TipoUsuario tipo) {
        this.nome = nome;

        // Garantindo que a senha não seja nula ou vazia
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode estar vazia.");
        }

        // Validação de senha para ter pelo menos 6 caracteres
        if (senha.trim().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        this.senha = senha.trim(); // Remove espaços extras
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public boolean autenticar(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    // Método para salvar o usuário na lista
    public void salvar() {
        usuarios.add(this);
        System.out.println("Usuário " + this.nome + " salvo com sucesso!");
    }

    // Método para buscar um usuário na lista pelo nome (usando Optional)
    public static Optional<Usuario> buscarPorNome(String nome) {
        return usuarios.stream()
                .filter(usuario -> usuario.getNome().equals(nome))
                .findFirst();
    }

    // Método para listar todos os usuários
    public static List<Usuario> listarTodos() {
        return usuarios;
    }

    // Método para excluir um usuário da lista
    public static boolean excluirPorNome(String nome) {
        Optional<Usuario> usuario = buscarPorNome(nome);
        if (usuario.isPresent()) {
            usuarios.remove(usuario.get());
            System.out.println("Usuário " + nome + " excluído com sucesso!");
            return true;
        } else {
            System.out.println("Usuário não encontrado!");
            return false;
        }
    }
}
package com.bestbarber.view;

import com.bestbarber.model.Cliente;
import com.bestbarber.service.ClienteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaRecuperarSenha extends JFrame {
    
    private int gradientOffset = 0;
    private JTextField emailField;
    private JPasswordField novaSenhaField;
    private JPasswordField repetirSenhaField;
    private JButton enviarEmailButton;
    private JButton redefinirButton;

    private ClienteService clienteService = new ClienteService();
    private Cliente clienteVerificado;

    public TelaRecuperarSenha() {
        setTitle("Recuperar Senha");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new GradientPanel());
        setLayout(null);

        JLabel titulo = new JLabel("Recuperar Senha");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setBounds(120, 20, 200, 25);
        add(titulo);

        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Informe seu Email"));
        emailField.setBounds(80, 60, 240, 45);
        add(emailField);

        enviarEmailButton = new JButton("Verificar Email");
        enviarEmailButton.setBounds(130, 115, 140, 30);
        enviarEmailButton.addActionListener(e -> verificarEmail());
        add(enviarEmailButton);

        novaSenhaField = new JPasswordField();
        novaSenhaField.setBorder(BorderFactory.createTitledBorder("Nova Senha"));
        novaSenhaField.setBounds(80, 160, 240, 45);
        novaSenhaField.setVisible(false);
        add(novaSenhaField);

        repetirSenhaField = new JPasswordField();
        repetirSenhaField.setBorder(BorderFactory.createTitledBorder("Repetir Nova Senha"));
        repetirSenhaField.setBounds(80, 215, 240, 45);
        repetirSenhaField.setVisible(false);
        add(repetirSenhaField);

        redefinirButton = new JButton("Redefinir");
        redefinirButton.setBounds(140, 270, 120, 30);
        redefinirButton.setVisible(false);
        redefinirButton.addActionListener(e -> redefinirSenha());
        add(redefinirButton);
        setVisible(true);
    }

private void verificarEmail() {
    String email = emailField.getText();
    System.out.println("Verificando email: " + email);
    clienteVerificado = clienteService.buscarPorEmail(email);
    System.out.println("Resultado da busca: " + (clienteVerificado != null));
    
    if (clienteVerificado != null) {
        JOptionPane.showMessageDialog(this, "Email encontrado. Digite a nova senha.");
        novaSenhaField.setVisible(true);
        repetirSenhaField.setVisible(true);
        redefinirButton.setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this, "Email não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
    private void redefinirSenha() {
        String novaSenha = new String(novaSenhaField.getPassword());
        String repetirSenha = new String(repetirSenhaField.getPassword());

        if (!novaSenha.equals(repetirSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Nova senha: " + novaSenha);
        System.out.println("Repetir senha: " + repetirSenha);
        if (novaSenha.equals(repetirSenha)) {
            clienteVerificado.setSenha(novaSenha);
            clienteService.salvar(clienteVerificado);
            JOptionPane.showMessageDialog(this, "Senha atualizada com sucesso!");
            dispose();
            new TelaLoginAnimada();
        } else {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return;
        
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color color1 = new Color(58, 123, 213);
            Color color2 = new Color(0, 210, 255);
            GradientPaint gp = new GradientPaint(gradientOffset, 0, color1, gradientOffset + width, height, color2, true);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaRecuperarSenha::new);
    }
}
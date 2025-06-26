package com.bestbarber.view;

import com.bestbarber.model.Cliente;
import com.bestbarber.service.ClienteService;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;

public class TelaCadastro extends JFrame {
    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JFormattedTextField telefoneField;
    private JTextField emailField;
    private JPasswordField senhaField;

    private int gradientOffset = 0;
    private ClienteService clienteService = new ClienteService();

    public TelaCadastro() {
        setTitle("Cadastro - BestBarber");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new GradientPanel());
        setLayout(null);

        JLabel titulo = new JLabel("Criar Conta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(170, 20, 200, 30);
        add(titulo);

        nomeField = new JTextField();
        nomeField.setBorder(BorderFactory.createTitledBorder("Nome Completo"));
        nomeField.setBounds(130, 60, 240, 45);
        add(nomeField);

        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfMask);
            cpfField.setBorder(BorderFactory.createTitledBorder("CPF"));
            cpfField.setBounds(130, 110, 240, 45);
            add(cpfField);

            MaskFormatter telMask = new MaskFormatter("(##) #####-####");
            telMask.setPlaceholderCharacter('_');
            telefoneField = new JFormattedTextField(telMask);
            telefoneField.setBorder(BorderFactory.createTitledBorder("Telefone"));
            telefoneField.setBounds(130, 160, 240, 45);
            add(telefoneField);
        } catch (Exception e) {
            e.printStackTrace();
        }

        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        emailField.setBounds(130, 210, 240, 45);
        add(emailField);

        senhaField = new JPasswordField();
        senhaField.setBorder(BorderFactory.createTitledBorder("Senha"));
        senhaField.setBounds(130, 260, 240, 45);
        add(senhaField);

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.setBounds(190, 320, 120, 35);
        cadastrarButton.addActionListener(e -> registrarUsuario());
        add(cadastrarButton);

        Timer timer = new Timer(50, e -> {
            gradientOffset = (gradientOffset + 1) % getWidth();
            repaint();
        });
        timer.start();

        setVisible(true);
    }

    private void registrarUsuario() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        String telefone = telefoneField.getText().replaceAll("[^0-9]", "");
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cpf.length() < 9) {
            JOptionPane.showMessageDialog(this, "CPF deve ter pelo menos 9 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
            cpfField.setText("");
            cpfField.requestFocus();
            return;
        }

        if (telefone.length() != 11) {
            JOptionPane.showMessageDialog(this, "Telefone deve conter exatamente 11 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
            telefoneField.setText("");
            telefoneField.requestFocus();
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setSenha(senha);

        clienteService.salvar(cliente);
        JOptionPane.showMessageDialog(this, "Cadastro salvo com sucesso!");
        dispose();
        new TelaLoginAnimada();
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
        SwingUtilities.invokeLater(TelaCadastro::new);
    }
}

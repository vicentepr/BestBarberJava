package com.bestbarber.view;

import javax.swing.*;
import java.awt.*;

public class TelaMenuPrincipal extends JFrame {

    public TelaMenuPrincipal() {
        setTitle("Menu Principal");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JButton agendarBtn = new JButton("Agendar Serviço");
        agendarBtn.addActionListener(e -> new TelaAgendarServico());

        JButton alterarBtn = new JButton("Alterar Agendamento");
        alterarBtn.addActionListener(e -> new TelaAlterarAgendamento());

        JButton cancelarBtn = new JButton("Cancelar Agendamento");
        cancelarBtn.addActionListener(e -> new TelaCancelarAgendamento());

        JButton contatoBtn = new JButton("Contato");
        contatoBtn.addActionListener(e -> new TelaContato());

        JButton localizacaoBtn = new JButton("Localização");
        localizacaoBtn.addActionListener(e -> new TelaLocalizacao());

        JButton sairBtn = new JButton("Sair");
        sairBtn.addActionListener(e -> System.exit(0));

        add(agendarBtn);
        add(alterarBtn);
        add(cancelarBtn);
        add(contatoBtn);
        add(localizacaoBtn);
        add(sairBtn);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaMenuPrincipal::new);
    }
}

package com.bestbarber.view;

import com.bestbarber.model.Agendamento;
import com.bestbarber.model.Cliente;
import com.bestbarber.service.AgendamentoService;
import com.bestbarber.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelaCancelarAgendamento extends JFrame {
    private JComboBox<String> agendamentosBox;
    private List<Agendamento> agendamentosCliente = new ArrayList<>();
    private AgendamentoService agendamentoService = new AgendamentoService();

    public TelaCancelarAgendamento() {
        setTitle("Cancelar Agendamento");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel titulo = new JLabel("Cancelar Agendamento");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBounds(100, 20, 250, 30);
        add(titulo);

        agendamentosBox = new JComboBox<>();
        agendamentosBox.setBounds(80, 70, 240, 45);
        agendamentosBox.setBorder(BorderFactory.createTitledBorder("Selecione o Agendamento"));
        add(agendamentosBox);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setBounds(140, 140, 120, 30);
        cancelarButton.addActionListener(e -> cancelar());
        add(cancelarButton);

        carregarAgendamentos();
        setVisible(true);
    }

    private void carregarAgendamentos() {
        Cliente cliente = Sessao.getClienteLogado();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Sessão expirada ou cliente não logado.", "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        String emailCliente = cliente.getEmail();
        agendamentosCliente = agendamentoService.buscarTodos().stream()
                .filter(a -> a.getEmailCliente() != null && emailCliente.equalsIgnoreCase(a.getEmailCliente()))
                .collect(Collectors.toList());

        agendamentosBox.removeAllItems();
        for (Agendamento a : agendamentosCliente) {
            agendamentosBox.addItem("ID: " + a.getId() + " - " + a.getServico() + " em " + a.getData() + " às " + a.getHora());
        }
    }

    private void cancelar() {
        if (agendamentosCliente == null || agendamentosCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum agendamento disponível para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int index = agendamentosBox.getSelectedIndex();
        if (index >= 0 && index < agendamentosCliente.size()) {
            Agendamento agendamento = agendamentosCliente.get(index);
            if (agendamento == null) {
                JOptionPane.showMessageDialog(this, "Erro interno: agendamento inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Long id = agendamento.getId(); // Corrigido: agora é Long
            boolean sucesso = agendamentoService.cancelar(id);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Agendamento cancelado com sucesso.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar agendamento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

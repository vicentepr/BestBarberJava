package com.bestbarber.view;

import com.bestbarber.model.Agendamento;
import com.bestbarber.model.Cliente;
import com.bestbarber.service.AgendamentoService;
import com.bestbarber.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TelaAgendarServico extends JFrame {
    private JComboBox<String> servicoBox;
    private JTextField dataField;
    private JComboBox<String> horaBox;
    private AgendamentoService agendamentoService = new AgendamentoService();

    public TelaAgendarServico() {
        setTitle("Agendar Serviço");
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel titulo = new JLabel("Agendar Serviço");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBounds(120, 10, 250, 30);
        add(titulo);

        servicoBox = new JComboBox<>(new String[] {
            "Corte de Cabelo", "Barba", "Corte + Barba", "Sobrancelha"
        });
        servicoBox.setBounds(80, 60, 240, 45);
        servicoBox.setBorder(BorderFactory.createTitledBorder("Serviço"));
        add(servicoBox);

        dataField = new JTextField();
        dataField.setBorder(BorderFactory.createTitledBorder("Data (dd/mm/aaaa)"));
        dataField.setBounds(80, 110, 240, 45);
        dataField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                atualizarHorariosDisponiveis(dataField.getText());
            }
        });
        add(dataField);

        horaBox = new JComboBox<>();
        horaBox.setBounds(80, 160, 240, 45);
        horaBox.setBorder(BorderFactory.createTitledBorder("Horário"));
        add(horaBox);

        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBounds(140, 220, 120, 30);
        confirmarButton.addActionListener(e -> agendar());
        add(confirmarButton);

        setVisible(true);
    }

    private void atualizarHorariosDisponiveis(String data) {
        List<String> todosHorarios = Arrays.asList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30"
        );

        List<Agendamento> agendados = agendamentoService.buscarPorData(data);
        List<String> ocupados = new ArrayList<>();
        for (Agendamento a : agendados) {
            ocupados.add(a.getHora());
        }

        horaBox.removeAllItems();
        for (String h : todosHorarios) {
            if (!ocupados.contains(h)) {
                horaBox.addItem(h);
            }
        }
    }

    private void agendar() {
        Cliente cliente = Sessao.getClienteLogado();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Sessão expirada. Faça login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        String servico = (String) servicoBox.getSelectedItem();
        String data = dataField.getText();
        String hora = (String) horaBox.getSelectedItem();

        Agendamento novo = new Agendamento();
        novo.setServico(servico);
        novo.setData(data);
        novo.setHora(hora);
        novo.setEmailCliente(cliente.getEmail());
        novo.setNomeCliente(cliente.getNome());

        boolean sucesso = agendamentoService.salvar(novo);
        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Serviço agendado com sucesso!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao agendar serviço.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package menu;

import javax.swing.*;

import pds.util.RelogioSimulado;

import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Uma janela que apresenta o relógio simulado e permite manipular esse mesmo
 * relógio.
 * Não será necessário alterar esta classe.
 */
public class RelogioDialog extends JDialog {
    private JLabel labelRelogio;
    private Timer timer;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
    private RelogioSimulado relogio;

    public RelogioDialog(Frame owner, Point pos) {
        super(owner, "Relógio Simulado", false);
        relogio = RelogioSimulado.getRelogioSimulado();
        setupAspeto();
        setSize(420, 100);
        setLocation(pos);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        iniciarTimer();
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
        super.dispose();
    }

    private void setupAspeto() {
        setLayout(new BorderLayout(10, 10));

        // Label do relógio
        labelRelogio = new JLabel(relogio.tempoAtual().format(formatter), SwingConstants.CENTER);
        labelRelogio.setFont(new Font("Monospaced", Font.BOLD, 24));
        add(labelRelogio, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 0));

        JButton maisUmaHoraBt = new JButton("+1H");
        maisUmaHoraBt.addActionListener(e -> relogio.setTempoAtual(relogio.tempoAtual().plusHours(1)));

        JButton maisDuasHorasBt = new JButton("+2H");
        maisDuasHorasBt.addActionListener(e -> relogio.setTempoAtual(relogio.tempoAtual().plusHours(2)));

        JButton maisCincoHorasBt = new JButton("+5H");
        maisCincoHorasBt.addActionListener(e -> relogio.setTempoAtual(relogio.tempoAtual().plusHours(5)));

        JButton maisDezHorasBt = new JButton("+10H");
        maisDezHorasBt.addActionListener(e -> relogio.setTempoAtual(relogio.tempoAtual().plusHours(10)));

        JButton mais1DiaBt = new JButton("+1D");
        mais1DiaBt.addActionListener(e -> relogio.setTempoAtual(relogio.tempoAtual().plusDays(1)));

        Integer[] velocidades = { 1, 2, 5, 10, 20, 30, 60, 3600 };
        String[] labels = { "1x", "2x", "5x", "10x", "20x", "30x", "1 min", "1 h" };
        JComboBox<String> comboVelocidade = new JComboBox<>(labels);
        comboVelocidade.setSelectedIndex(0);
        comboVelocidade.addActionListener(a -> {
            int index = comboVelocidade.getSelectedIndex();
            RelogioSimulado.getRelogioSimulado().setTicksPorSegundo(velocidades[index]);
        });

        painelBotoes.add(comboVelocidade);
        painelBotoes.add(maisUmaHoraBt);
        painelBotoes.add(maisDuasHorasBt);
        painelBotoes.add(maisCincoHorasBt);
        painelBotoes.add(maisDezHorasBt);
        painelBotoes.add(mais1DiaBt);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void iniciarTimer() {
        timer = new Timer(1000, e -> {
            labelRelogio.setText(relogio.tempoAtual().format(formatter));
        });
        timer.start();
    }
}
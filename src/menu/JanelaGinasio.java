package menu;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import pds.dominio.Membro;
import pds.dominio.RegistoUtilizacao;
import pds.dominio.ResultadoAcesso;
import pds.dominio.TipoServico;
import pds.equipamentos.Equipamento;
import pds.ginasio.Ginasio;
import pds.util.RelogioSimulado;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Janela que simula um dos ginásios da ESTGym
 */
public class JanelaGinasio extends JFrame {

    // constantes com as dimensões dos elementos da interface
    private static final Dimension TAMANHO_BOTAO = new Dimension(55, 55);
    private static final Dimension TAMANHO_ICON = new Dimension(50, 50);
    private static final int LARGURA_LISTA_MEMBROS = 100;
    private static final int LARGURA_TABELA_SERVICOS = 400;

    // constantes para a atualização da janela
    private static final int PERIODO_ATUALIZACAO_MS = 1000;

    // constante para definir o formato a usar para a data e hora
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Elementos gráficos
    private final DefaultListModel<Integer> modeloMembros = new DefaultListModel<>();
    private final JList<Integer> listaMembros = new JList<>(modeloMembros);
    private final DefaultTableModel modeloServicos = new DefaultTableModel(new Object[] { "Serviço", "Contrato" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel modeloUsos = new DefaultTableModel(
            new Object[] { "Serviço", "Equipamento", "Data e hora" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // elementos para controlar os botões dos equipamentos
    private final PainelBotoes painelBotoes = new PainelBotoes();
    private final Map<Integer, JButton> botoes = new LinkedHashMap<>();
    private final Ginasio ginasio;
    private final Timer timerAtualizacao;

    public JanelaGinasio() {
        this(new Ginasio("ESTGym"));
    }

    public JanelaGinasio(Ginasio ginasio) {
        super("ESTGym");
        this.ginasio = Objects.requireNonNull(ginasio);
        setupInterface();
        timerAtualizacao = new Timer(PERIODO_ATUALIZACAO_MS, e -> atualizarBotoes());
        timerAtualizacao.start();
    }

    @Override
    public void dispose() {
        timerAtualizacao.stop();
        super.dispose();
    }

    /** Carrega a lista de membros. */
    private void carregarMembros() {
        for (Membro membro : ginasio.getMembros()) {
            modeloMembros.addElement(membro.getId());
        }
    }

    /**
     * Adiciona equipamento ao simulador do ginásio.
     * 
     * @param id           identificador do equipamento
     * @param iconesPronto array com os icones para livre ([0]) e ocupado ([1])
     */
    public void addEquipamento(int id, Icon icones[]) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addEquipamento(id, icones));
            return;
        }
        Equipamento equipamento = ginasio.getEquipamento(id);
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não registado no ginásio: " + id);
        }
        if (icones == null || icones.length < 2 || icones[0] == null || icones[1] == null) {
            throw new IllegalArgumentException("São necessários ícones de equipamento livre e ocupado.");
        }
        if (botoes.containsKey(id)) {
            return; // já existe
        }
        String nome = equipamento.getNome();
        Icon iconPronto = icones[0];
        Icon iconEmUso = icones[1];

        JButton botao = new JButton();
        botao.setPreferredSize(TAMANHO_BOTAO);
        botao.setMinimumSize(TAMANHO_BOTAO);
        botao.setMaximumSize(TAMANHO_BOTAO);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setFocusPainted(false);

        // Todos os icons ficam com o mesmo tamanho
        botao.setIcon(redimensionarIcon(iconPronto));
        botao.setDisabledIcon(redimensionarIcon(iconEmUso));

        // Hint com o nome do equipamento
        botao.setToolTipText(nome);

        botao.setEnabled(equipamento.isDisponivel(RelogioSimulado.getTempoAtual()));
        botao.addActionListener(e -> usarEquipamento(id, botao));

        botoes.put(id, botao);
        painelBotoes.add(botao);
        painelBotoes.revalidate();
        painelBotoes.repaint();
    }

    /**
     * Chamado quando se pressiona num botão de equipamento
     * 
     * @param equipamentoId o id do equipamento
     * @param source        o botão que foi pressionado
     */
    protected void usarEquipamento(int equipamentoId, JButton source) {
        Membro membro = pedirMembro(source);
        if (membro == null) {
            return;
        }
        ResultadoAcesso resultado = ginasio.usarEquipamento(equipamentoId, membro.getId());
        atualizarBotoes();
        Integer membroSelecionado = listaMembros.getSelectedValue();
        if (membroSelecionado != null && membroSelecionado == membro.getId()) {
            atualizarTabelas(membro.getId());
        }
        JOptionPane.showMessageDialog(source, resultado.getMensagem(),
                resultado.isAutorizado() ? "Acesso autorizado" : "Uso não autorizado",
                resultado.isAutorizado() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pede o número identificador do membro (simula a pssagem do cartão de
     * identificação)
     * 
     * @param source botão que foi premido (para alinhar a mensagem de erro)
     * @return o memebro identificado, ou null se não existir
     */
    private Membro pedirMembro(JButton source) {
        String code = JOptionPane.showInputDialog(source, "Id do membro?");
        if (code == null) {
            return null;
        }
        final int id;
        try {
            id = Integer.parseInt(code.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(source, "O identificador do membro tem de ser numérico.",
                    "Id inválido", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        Membro membro = ginasio.getMembro(id);
        if (membro == null) {
            JOptionPane.showMessageDialog(source, "Id desconhecido", "Membro não encontrado",
                    JOptionPane.ERROR_MESSAGE);
        }
        return membro;
    }

    /**
     * Classe interna responsável por apresentar o nome do membro na lista de
     * membros
     */
    private class MembroRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            int id = (int) value;
            Membro membro = ginasio.getMembro(id);
            String nome = membro == null ? "Membro desconhecido" : membro.getNome();
            return super.getListCellRendererComponent(list, nome, index, isSelected, cellHasFocus);
        }

    }

    /**
     * Atualiza as tabelas que apresentam os serviços/contratos e os registos de uso
     * do membro selecionado.
     * Chamado quando se seleciona um membro na lista de membros.
     * 
     * @param membroId id do membro selecionado
     */
    private void atualizarTabelas(int membroId) {
        modeloServicos.setRowCount(0);
        modeloUsos.setRowCount(0);

        Membro membro = ginasio.getMembro(membroId);
        if (membro == null) {
            return;
        }
        for (Map.Entry<TipoServico, String> servico : ginasio.getDescricaoServicosContratados(membroId).entrySet()) {
            modeloServicos.addRow(new String[] { servico.getKey().getNome(), servico.getValue() });
        }

        for (RegistoUtilizacao registo : membro.getRegistos()) {
            modeloUsos.addRow(new Object[] {
                    registo.getTipoServico().getNome(),
                    registo.getNomeEquipamento(),
                    registo.getDataHora().format(FORMATO_DATA_HORA)
            });
        }
    }

    /**
     * Chamado a cada segundo: reflete o estado atual de cada equipamento
     */
    private void atualizarBotoes() {
        LocalDateTime agora = RelogioSimulado.getTempoAtual();
        for (Map.Entry<Integer, JButton> e : botoes.entrySet()) {
            int equipamentoId = e.getKey();
            JButton botao = e.getValue();
            Equipamento equipamento = ginasio.getEquipamento(equipamentoId);
            if (equipamento == null) {
                botao.setEnabled(false);
                botao.setToolTipText("Equipamento removido");
                continue;
            }
            botao.setEnabled(equipamento.isDisponivel(agora));
            botao.setToolTipText(equipamento.getNome());
        }
    }

    /**
     * inicializa todos os elementos visuais da janela
     */
    private void setupInterface() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(criarZonaMembros(), BorderLayout.WEST);
        add(criarZonaBotoes(), BorderLayout.CENTER);

        carregarMembros();

        setMinimumSize(new Dimension(900, 700));

        setLocationRelativeTo(null);
    }

    /**
     * Cria a zona onde aparecem os dados dos membros, localizada no lado esquerdo
     * da janela
     * 
     * @return a zona já criada e configurada
     */
    private JComponent criarZonaMembros() {
        // Lista de membros: largura fixa, altura flexível
        listaMembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMembros.setCellRenderer(new MembroRenderer());
        listaMembros.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaMembros.getSelectedValue() != null) {
                atualizarTabelas(listaMembros.getSelectedValue());
            }
        });
        JScrollPane scrollLista = new JScrollPane(listaMembros);
        fixarLargura(scrollLista, LARGURA_LISTA_MEMBROS);

        // Tabela com os serviços e respetivos contratos
        JTable tabelaServicos = new JTable(modeloServicos);
        tabelaServicos.setFillsViewportHeight(true);
        JScrollPane scrollTabela = new JScrollPane(tabelaServicos);
        fixarLargura(scrollTabela, LARGURA_TABELA_SERVICOS);

        // Tabela com os registos de uso
        JTable tabelaUsos = new JTable(modeloUsos);
        tabelaUsos.setFillsViewportHeight(true);
        JScrollPane scrollUsos = new JScrollPane(tabelaUsos);
        fixarLargura(scrollUsos, LARGURA_TABELA_SERVICOS);

        // Coluna com as duas tabelas
        JPanel colunaTabelas = new JPanel();
        colunaTabelas.setLayout(new BoxLayout(colunaTabelas, BoxLayout.Y_AXIS));
        colunaTabelas.add(scrollTabela);
        colunaTabelas.add(Box.createVerticalStrut(4));
        colunaTabelas.add(scrollUsos);

        // Painel onde colocar todos os elementos
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.X_AXIS));
        painel.add(scrollLista);
        painel.add(Box.createHorizontalStrut(4));
        painel.add(colunaTabelas);
        painel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return painel;
    }

    /**
     * Permite fixar a largura de um componente
     * 
     * @param c       o componente a fixar a largura
     * @param largura a largura máxima
     */
    private static void fixarLargura(JComponent c, int largura) {
        c.setPreferredSize(new Dimension(largura, 0));
        c.setMinimumSize(new Dimension(largura, 0));
        c.setMaximumSize(new Dimension(largura, Integer.MAX_VALUE));
    }

    /**
     * Cria a zona onde colcoar os botões com os equipamentos
     * 
     * @return a zona já criada e configurada
     */
    private JComponent criarZonaBotoes() {
        JScrollPane scroll = new JScrollPane(painelBotoes,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(new TitledBorder("Equipamentos"));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /** Desenha o icon num BufferedImage com o tamanho padrão. */
    private static Icon redimensionarIcon(Icon icon) {
        BufferedImage origem = new BufferedImage(
                Math.max(1, icon.getIconWidth()), Math.max(1, icon.getIconHeight()),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = origem.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        BufferedImage destino = new BufferedImage(TAMANHO_ICON.width, TAMANHO_ICON.height,
                BufferedImage.TYPE_INT_ARGB);
        g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(origem, 0, 0, TAMANHO_ICON.width, TAMANHO_ICON.height, null);
        g.dispose();
        return new ImageIcon(destino);
    }

    /**
     * Painel dos botões: local onde os botões são adicionados
     */
    private static class PainelBotoes extends JPanel implements Scrollable {
        PainelBotoes() {
            super(new QuebraLinhaLayout(8, 8));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(16, visibleRect.height - 16);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /**
     * FlowLayout (alinhado à esquerda) que calcula a altura preferida com base na
     * largura disponível.
     */
    private static class QuebraLinhaLayout extends FlowLayout {
        QuebraLinhaLayout(int hgap, int vgap) {
            super(FlowLayout.LEFT, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return calcular(target);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return calcular(target);
        }

        private Dimension calcular(Container target) {
            synchronized (target.getTreeLock()) {
                int largura = target.getParent() != null ? target.getParent().getWidth() : 0;
                Insets in = target.getInsets();
                int maxLinha = (largura > 0 ? largura : Integer.MAX_VALUE) - in.left - in.right - 2 * getHgap();

                int linhaW = 0, linhaH = 0, totalW = 0, totalH = 0;
                for (Component c : target.getComponents()) {
                    if (!c.isVisible()) {
                        continue;
                    }
                    Dimension d = c.getPreferredSize();
                    if (linhaW > 0 && linhaW + getHgap() + d.width > maxLinha) {
                        totalW = Math.max(totalW, linhaW);
                        totalH += linhaH + getVgap();
                        linhaW = 0;
                        linhaH = 0;
                    }
                    linhaW += (linhaW > 0 ? getHgap() : 0) + d.width;
                    linhaH = Math.max(linhaH, d.height);
                }
                totalW = Math.max(totalW, linhaW);
                totalH += linhaH;

                return new Dimension(totalW + in.left + in.right + 2 * getHgap(),
                        totalH + in.top + in.bottom + 2 * getVgap());
            }
        }
    }
}

package janela;

import dialogo.DialogoUtil;
import util.KeyBindingManager;
import util.KeyBindingManager.Atalho;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Painel de abas com suporte a fechamento, ícones e indicador de alterações.
 * 
 * <h3>Características:</h3>
 * <ul>
 *   <li>Botão X para fechar cada aba</li>
 *   <li>Indicador visual de alterações não salvas (•)</li>
 *   <li>Menu de contexto (Fechar, Fechar Outras, Fechar Todas)</li>
 *   <li>Atalhos: Ctrl+W (fechar), Ctrl+Tab (próxima), Ctrl+Shift+Tab (anterior)</li>
 *   <li>Suporte a múltiplas instâncias do mesmo tipo de painel</li>
 *   <li>Confirmação ao fechar abas com alterações pendentes</li>
 * </ul>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * PainelAbas tabs = new PainelAbas();
 * tabs.adicionarAba("Clientes", new ClientePanel());
 * tabs.adicionarAba("Produtos", icone, new ProdutoPanel());
 * 
 * // Marcar aba como modificada
 * tabs.marcarModificado(component, true);
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class PainelAbas extends JTabbedPane {
    
    private final Map<Component, TabInfo> tabInfoMap = new HashMap<>();
    private final List<OpcaoAba> opcoesAba = new ArrayList<>();
    private Consumer<Component> onTabClosed;
    private Consumer<Component> onTabSelected;
    
    /**
     * Representa uma opção de aba que pode ser aberta via busca.
     */
    public static class OpcaoAba {
        private final String nome;
        private final String icone;
        private final Supplier<Component> factory;
        private final boolean unica;
        
        public OpcaoAba(String nome, String icone, Supplier<Component> factory, boolean unica) {
            this.nome = nome;
            this.icone = icone;
            this.factory = factory;
            this.unica = unica;
        }
        
        public OpcaoAba(String nome, Supplier<Component> factory) {
            this(nome, null, factory, true);
        }
        
        public String getNome() { return nome; }
        public String getIcone() { return icone; }
        public Component criarComponente() { return factory.get(); }
        public boolean isUnica() { return unica; }
        
        @Override
        public String toString() {
            return icone != null ? icone + " " + nome : nome;
        }
    }
    
    /**
     * Informações sobre uma aba.
     */
    private static class TabInfo {
        String titulo;
        Icon icone;
        boolean modificado;
        boolean fechavel;
        
        TabInfo(String titulo, Icon icone, boolean fechavel) {
            this.titulo = titulo;
            this.icone = icone;
            this.modificado = false;
            this.fechavel = fechavel;
        }
    }
    
    /**
     * Cria um PainelAbas com posição padrão (topo).
     */
    public PainelAbas() {
        super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        configurarAtalhos();
        configurarListeners();
    }
    
    /**
     * Cria um PainelAbas com posição customizada.
     * @param tabPlacement TOP, BOTTOM, LEFT ou RIGHT
     */
    public PainelAbas(int tabPlacement) {
        super(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
        configurarAtalhos();
        configurarListeners();
    }
    
    private void configurarAtalhos() {
        KeyBindingManager.registrar(this, Atalho.FECHAR, this::fecharAbaAtual);
        KeyBindingManager.registrar(this, Atalho.PROXIMO, this::proximaAba);
        KeyBindingManager.registrar(this, Atalho.ANTERIOR, this::abaAnterior);
    }
    
    private void configurarListeners() {
        addChangeListener(e -> {
            if (onTabSelected != null && getSelectedComponent() != null) {
                onTabSelected.accept(getSelectedComponent());
            }
        });
    }
    
    // ==================== MÉTODOS PÚBLICOS ====================
    
    /**
     * Adiciona uma nova aba fechável.
     * @param titulo Título da aba
     * @param componente Componente a ser exibido
     */
    public void adicionarAba(String titulo, Component componente) {
        adicionarAba(titulo, null, componente, true);
    }
    
    /**
     * Adiciona uma nova aba com ícone.
     * @param titulo Título da aba
     * @param icone Ícone da aba (pode ser null)
     * @param componente Componente a ser exibido
     */
    public void adicionarAba(String titulo, Icon icone, Component componente) {
        adicionarAba(titulo, icone, componente, true);
    }
    
    /**
     * Adiciona uma nova aba com controle de fechamento.
     * @param titulo Título da aba
     * @param icone Ícone da aba (pode ser null)
     * @param componente Componente a ser exibido
     * @param fechavel Se a aba pode ser fechada
     */
    public void adicionarAba(String titulo, Icon icone, Component componente, boolean fechavel) {
        TabInfo info = new TabInfo(titulo, icone, fechavel);
        tabInfoMap.put(componente, info);
        
        addTab(titulo, icone, componente);
        int index = indexOfComponent(componente);
        
        setTabComponentAt(index, criarTabComponent(info, componente));
        setSelectedIndex(index);
    }
    
    /**
     * Adiciona uma aba fixa (não fechável).
     * @param titulo Título da aba
     * @param icone Ícone da aba
     * @param componente Componente a ser exibido
     */
    public void adicionarAbaFixa(String titulo, Icon icone, Component componente) {
        adicionarAba(titulo, icone, componente, false);
    }
    
    /**
     * Marca uma aba como modificada (exibe indicador •).
     * @param componente Componente da aba
     * @param modificado Se há alterações não salvas
     */
    public void marcarModificado(Component componente, boolean modificado) {
        TabInfo info = tabInfoMap.get(componente);
        if (info != null && info.modificado != modificado) {
            info.modificado = modificado;
            int index = indexOfComponent(componente);
            if (index >= 0) {
                atualizarTabComponent(index, info, componente);
            }
        }
    }
    
    /**
     * Marca a aba atual como modificada.
     * @param modificado Se há alterações não salvas
     */
    public void marcarModificadoAtual(boolean modificado) {
        Component comp = getSelectedComponent();
        if (comp != null) {
            marcarModificado(comp, modificado);
        }
    }
    
    /**
     * Verifica se uma aba tem modificações não salvas.
     * @param componente Componente da aba
     * @return true se há modificações pendentes
     */
    public boolean isModificado(Component componente) {
        TabInfo info = tabInfoMap.get(componente);
        return info != null && info.modificado;
    }
    
    /**
     * Verifica se alguma aba tem modificações não salvas.
     * @return true se qualquer aba tem modificações
     */
    public boolean existeModificacoes() {
        return tabInfoMap.values().stream().anyMatch(info -> info.modificado);
    }
    
    /**
     * Fecha a aba atual.
     * @return true se a aba foi fechada
     */
    public boolean fecharAbaAtual() {
        int index = getSelectedIndex();
        if (index >= 0) {
            return fecharAba(index);
        }
        return false;
    }
    
    /**
     * Fecha uma aba específica.
     * @param index Índice da aba
     * @return true se a aba foi fechada
     */
    public boolean fecharAba(int index) {
        if (index < 0 || index >= getTabCount()) {
            return false;
        }
        
        Component componente = getComponentAt(index);
        TabInfo info = tabInfoMap.get(componente);
        
        if (info != null && !info.fechavel) {
            return false;
        }
        
        if (info != null && info.modificado) {
            if (!DialogoUtil.confirmar(this, 
                "A aba \"" + info.titulo + "\" tem alterações não salvas.\nDeseja fechar mesmo assim?",
                "Alterações não salvas")) {
                return false;
            }
        }
        
        tabInfoMap.remove(componente);
        removeTabAt(index);
        
        if (onTabClosed != null) {
            onTabClosed.accept(componente);
        }
        
        return true;
    }
    
    /**
     * Fecha todas as abas exceto a atual.
     */
    public void fecharOutras() {
        int indexAtual = getSelectedIndex();
        
        for (int i = getTabCount() - 1; i >= 0; i--) {
            if (i != indexAtual) {
                Component comp = getComponentAt(i);
                TabInfo info = tabInfoMap.get(comp);
                if (info != null && info.fechavel) {
                    fecharAba(i);
                }
            }
        }
    }
    
    /**
     * Fecha todas as abas fecháveis.
     * @return true se todas foram fechadas
     */
    public boolean fecharTodas() {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component comp = getComponentAt(i);
            TabInfo info = tabInfoMap.get(comp);
            if (info != null && info.fechavel) {
                if (!fecharAba(i)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Navega para a próxima aba.
     */
    public void proximaAba() {
        int count = getTabCount();
        if (count > 1) {
            int next = (getSelectedIndex() + 1) % count;
            setSelectedIndex(next);
        }
    }
    
    /**
     * Navega para a aba anterior.
     */
    public void abaAnterior() {
        int count = getTabCount();
        if (count > 1) {
            int prev = (getSelectedIndex() - 1 + count) % count;
            setSelectedIndex(prev);
        }
    }
    
    // ==================== OPÇÕES DE ABAS ====================
    
    /**
     * Registra uma opção de aba que pode ser aberta via busca.
     * @param nome Nome da aba
     * @param factory Função que cria o componente da aba
     */
    public void registrarOpcao(String nome, Supplier<Component> factory) {
        opcoesAba.add(new OpcaoAba(nome, factory));
    }
    
    /**
     * Registra uma opção de aba com ícone.
     * @param nome Nome da aba
     * @param icone Ícone (emoji ou texto)
     * @param factory Função que cria o componente
     */
    public void registrarOpcao(String nome, String icone, Supplier<Component> factory) {
        opcoesAba.add(new OpcaoAba(nome, icone, factory, true));
    }
    
    /**
     * Retorna as opções de abas registradas.
     */
    public List<OpcaoAba> getOpcoesAba() {
        return new ArrayList<>(opcoesAba);
    }
    
    /**
     * Abre uma aba pelo nome da opção registrada.
     * @param nomeOpcao Nome da opção
     * @return true se a aba foi aberta ou focada
     */
    public boolean abrirOpcao(String nomeOpcao) {
        for (OpcaoAba opcao : opcoesAba) {
            if (opcao.getNome().equalsIgnoreCase(nomeOpcao)) {
                return abrirOpcao(opcao);
            }
        }
        return false;
    }
    
    /**
     * Abre uma aba pela opção.
     * @param opcao Opção de aba
     * @return true se a aba foi aberta ou focada
     */
    public boolean abrirOpcao(OpcaoAba opcao) {
        if (opcao.isUnica()) {
            for (int i = 0; i < getTabCount(); i++) {
                String titulo = getTitleAt(i).replace("• ", "");
                if (titulo.equals(opcao.getNome())) {
                    setSelectedIndex(i);
                    return true;
                }
            }
        }
        
        Component componente = opcao.criarComponente();
        if (componente != null) {
            adicionarAba(opcao.getNome(), null, componente);
            return true;
        }
        return false;
    }
    
    /**
     * Busca opções pelo texto.
     * @param texto Texto de busca
     * @return Lista de opções que correspondem
     */
    public List<OpcaoAba> buscarOpcoes(String texto) {
        List<OpcaoAba> resultado = new ArrayList<>();
        String busca = texto.toLowerCase();
        
        for (OpcaoAba opcao : opcoesAba) {
            if (opcao.getNome().toLowerCase().contains(busca)) {
                resultado.add(opcao);
            }
        }
        return resultado;
    }
    
    /**
     * Seleciona uma aba pelo componente.
     * @param componente Componente a selecionar
     */
    public void selecionarAba(Component componente) {
        int index = indexOfComponent(componente);
        if (index >= 0) {
            setSelectedIndex(index);
        }
    }
    
    /**
     * Atualiza o título de uma aba.
     * @param componente Componente da aba
     * @param novoTitulo Novo título
     */
    public void atualizarTitulo(Component componente, String novoTitulo) {
        TabInfo info = tabInfoMap.get(componente);
        if (info != null) {
            info.titulo = novoTitulo;
            int index = indexOfComponent(componente);
            if (index >= 0) {
                setTitleAt(index, novoTitulo);
                atualizarTabComponent(index, info, componente);
            }
        }
    }
    
    /**
     * Define callback para quando uma aba é fechada.
     * @param callback Consumer que recebe o componente fechado
     */
    public void setOnTabClosed(Consumer<Component> callback) {
        this.onTabClosed = callback;
    }
    
    /**
     * Define callback para quando uma aba é selecionada.
     * @param callback Consumer que recebe o componente selecionado
     */
    public void setOnTabSelected(Consumer<Component> callback) {
        this.onTabSelected = callback;
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    private JPanel criarTabComponent(TabInfo info, Component componente) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);
        
        if (info.icone != null) {
            JLabel iconLabel = new JLabel(info.icone);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
            tabPanel.add(iconLabel);
        }
        
        JLabel titleLabel = new JLabel(getTituloExibicao(info));
        tabPanel.add(titleLabel);
        
        if (info.fechavel) {
            JButton closeButton = criarBotaoFechar(componente);
            tabPanel.add(closeButton);
        }
        
        tabPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = indexOfComponent(componente);
                if (index >= 0) {
                    setSelectedIndex(index);
                }
                
                if (e.isPopupTrigger()) {
                    mostrarMenuContexto(e, componente);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenuContexto(e, componente);
                }
            }
        });
        
        return tabPanel;
    }
    
    private String getTituloExibicao(TabInfo info) {
        return info.modificado ? "• " + info.titulo : info.titulo;
    }
    
    private void atualizarTabComponent(int index, TabInfo info, Component componente) {
        setTabComponentAt(index, criarTabComponent(info, componente));
    }
    
    private JButton criarBotaoFechar(Component componente) {
        JButton button = new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(16, 16);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2.setColor(UIManager.getColor("TabbedPane.closeHoverForeground") != null 
                        ? UIManager.getColor("TabbedPane.closeHoverForeground") 
                        : new Color(200, 50, 50));
                } else {
                    g2.setColor(UIManager.getColor("TabbedPane.closeForeground") != null
                        ? UIManager.getColor("TabbedPane.closeForeground")
                        : Color.GRAY);
                }
                
                int padding = 4;
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(padding, padding, getWidth() - padding, getHeight() - padding);
                g2.drawLine(getWidth() - padding, padding, padding, getHeight() - padding);
                g2.dispose();
            }
        };
        
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(2, 6, 0, 0));
        button.setToolTipText("Fechar aba (Ctrl+W)");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            int index = indexOfComponent(componente);
            if (index >= 0) {
                fecharAba(index);
            }
        });
        
        return button;
    }
    
    private void mostrarMenuContexto(MouseEvent e, Component componente) {
        JPopupMenu menu = new JPopupMenu();
        TabInfo info = tabInfoMap.get(componente);
        
        JMenuItem fecharItem = new JMenuItem("Fechar");
        fecharItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        fecharItem.addActionListener(ev -> {
            int index = indexOfComponent(componente);
            if (index >= 0) fecharAba(index);
        });
        fecharItem.setEnabled(info != null && info.fechavel);
        menu.add(fecharItem);
        
        JMenuItem fecharOutrasItem = new JMenuItem("Fechar Outras");
        fecharOutrasItem.addActionListener(ev -> fecharOutras());
        fecharOutrasItem.setEnabled(getTabCount() > 1);
        menu.add(fecharOutrasItem);
        
        JMenuItem fecharTodasItem = new JMenuItem("Fechar Todas");
        fecharTodasItem.addActionListener(ev -> fecharTodas());
        menu.add(fecharTodasItem);
        
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

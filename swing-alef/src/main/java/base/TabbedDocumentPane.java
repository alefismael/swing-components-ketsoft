package base;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import ui.DialogUtil;
import util.KeyBindingManager;
import util.KeyBindingManager.Atalho;

/**
 * Painel de abas com suporte a fechamento, ícones e indicador de alterações.
 * 
 * Características:
 * - Botão X para fechar cada aba
 * - Indicador visual de alterações não salvas (•)
 * - Menu de contexto (Fechar, Fechar Outras, Fechar Todas)
 * - Atalhos: Ctrl+W (fechar), Ctrl+Tab (próxima), Ctrl+Shift+Tab (anterior)
 * - Suporte a múltiplas instâncias do mesmo tipo de painel
 * - Confirmação ao fechar abas com alterações pendentes
 * 
 * Exemplo de uso:
 * <pre>
 * TabbedDocumentPane tabs = new TabbedDocumentPane();
 * tabs.adicionarAba("Clientes", new ClientePanel());
 * tabs.adicionarAba("Produtos", icone, new ProdutoPanel());
 * 
 * // Marcar aba como modificada
 * tabs.marcarModificado("Clientes", true);
 * </pre>
 * 
 * @author alefi
 */
public class TabbedDocumentPane extends JTabbedPane {
    
    private final Map<Component, TabInfo> tabInfoMap = new HashMap<>();
    private final List<TabOption> tabOptions = new ArrayList<>();
    private final List<MenuOpcao> menuOpcoes = new ArrayList<>();
    private Consumer<Component> onTabClosed;
    private Consumer<Component> onTabSelected;
    
    /**
     * Representa uma opção de menu hierárquico com submenus.
     * Pode conter submenus ou abrir diretamente um painel.
     */
    public static class MenuOpcao {
        private final String nome;
        private final String icone;
        private final List<MenuOpcao> subOpcoes;
        private final Supplier<Component> factory;
        private final boolean unica;
        private String caminhoCompleto; // Ex: "Cadastro > Básicos > Pessoa"
        
        /**
         * Cria uma opção de menu que é um grupo (tem subopções).
         */
        public MenuOpcao(String nome, String icone, List<MenuOpcao> subOpcoes) {
            this.nome = nome;
            this.icone = icone;
            this.subOpcoes = subOpcoes != null ? subOpcoes : new ArrayList<>();
            this.factory = null;
            this.unica = true;
            this.caminhoCompleto = nome;
        }
        
        /**
         * Cria uma opção de menu que abre um painel.
         */
        public MenuOpcao(String nome, String icone, Supplier<Component> factory) {
            this.nome = nome;
            this.icone = icone;
            this.subOpcoes = new ArrayList<>();
            this.factory = factory;
            this.unica = true;
            this.caminhoCompleto = nome;
        }
        
        /**
         * Cria uma opção de menu que abre um painel (permite múltiplas instâncias).
         */
        public MenuOpcao(String nome, String icone, Supplier<Component> factory, boolean unica) {
            this.nome = nome;
            this.icone = icone;
            this.subOpcoes = new ArrayList<>();
            this.factory = factory;
            this.unica = unica;
            this.caminhoCompleto = nome;
        }
        
        public String getNome() { return nome; }
        public String getIcone() { return icone; }
        public List<MenuOpcao> getSubOpcoes() { return subOpcoes; }
        public boolean isGrupo() { return factory == null && !subOpcoes.isEmpty(); }
        public boolean isPainel() { return factory != null; }
        public boolean isUnica() { return unica; }
        public String getCaminhoCompleto() { return caminhoCompleto; }
        
        void setCaminhoCompleto(String caminho) { this.caminhoCompleto = caminho; }
        
        public Component criarComponente() {
            return factory != null ? factory.get() : null;
        }
        
        public void adicionarSubOpcao(MenuOpcao opcao) {
            subOpcoes.add(opcao);
            opcao.setCaminhoCompleto(this.caminhoCompleto + " > " + opcao.getNome());
            // Propagar caminho para sub-subopções
            propagarCaminho(opcao);
        }
        
        private void propagarCaminho(MenuOpcao opcao) {
            for (MenuOpcao sub : opcao.getSubOpcoes()) {
                sub.setCaminhoCompleto(opcao.getCaminhoCompleto() + " > " + sub.getNome());
                propagarCaminho(sub);
            }
        }
        
        @Override
        public String toString() {
            return icone != null ? icone + " " + nome : nome;
        }
        
        /**
         * Retorna representação para busca (mostra caminho completo).
         */
        public String toStringBusca() {
            String texto = icone != null ? icone + " " + nome : nome;
            if (!caminhoCompleto.equals(nome)) {
                texto += "  (" + caminhoCompleto.substring(0, caminhoCompleto.lastIndexOf(" > ")) + ")";
            }
            return texto;
        }
    }
    
    /**
     * Representa uma opção de aba que pode ser aberta via busca.
     */
    public static class TabOption {
        private final String nome;
        private final String icone;
        private final Supplier<Component> factory;
        private final boolean unica; // Se só pode ter uma instância
        
        public TabOption(String nome, String icone, Supplier<Component> factory, boolean unica) {
            this.nome = nome;
            this.icone = icone;
            this.factory = factory;
            this.unica = unica;
        }
        
        public TabOption(String nome, Supplier<Component> factory) {
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
    
    public TabbedDocumentPane() {
        super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        configurarAtalhos();
        configurarListeners();
    }
    
    /**
     * Cria um TabbedDocumentPane com posição de abas customizada.
     * @param tabPlacement TOP, BOTTOM, LEFT ou RIGHT
     */
    public TabbedDocumentPane(int tabPlacement) {
        super(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
        configurarAtalhos();
        configurarListeners();
    }
    
    private void configurarAtalhos() {
        // Usa KeyBindingManager para atalhos padronizados
        KeyBindingManager.registrar(this, Atalho.FECHAR, this::fecharAbaAtual);
        KeyBindingManager.registrar(this, Atalho.PROXIMO, this::proximaAba);
        KeyBindingManager.registrar(this, Atalho.ANTERIOR, this::abaAnterior);
        KeyBindingManager.registrar(this, Atalho.BUSCAR, this::focarBusca);
    }
    
    /**
     * Foca no campo de busca (se existir toolbar com busca).
     */
    private void focarBusca() {
        // Busca o campo de texto na toolbar
        Container parent = getParent();
        if (parent != null) {
            for (Component comp : parent.getComponents()) {
                if (comp instanceof JToolBar toolbar) {
                    JTextField tf = buscarCampoBusca(toolbar);
                    if (tf != null) {
                        tf.requestFocusInWindow();
                        tf.selectAll();
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Busca recursivamente um JTextField em um container.
     */
    private JTextField buscarCampoBusca(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField tf) {
                return tf;
            }
            if (comp instanceof Container c) {
                JTextField found = buscarCampoBusca(c);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
        // Armazena informações da aba
        TabInfo info = new TabInfo(titulo, icone, fechavel);
        tabInfoMap.put(componente, info);
        
        // Adiciona a aba
        addTab(titulo, icone, componente);
        int index = indexOfComponent(componente);
        
        // Configura o componente da aba (título + botão fechar)
        setTabComponentAt(index, criarTabComponent(info, componente));
        
        // Seleciona a nova aba
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
        
        // Verifica se a aba é fechável
        if (info != null && !info.fechavel) {
            return false;
        }
        
        // Verifica modificações
        if (info != null && info.modificado) {
            if (!DialogUtil.confirmar(this, 
                "A aba \"" + info.titulo + "\" tem alterações não salvas.\nDeseja fechar mesmo assim?",
                "Alterações não salvas")) {
                return false;
            }
        }
        
        // Remove a aba
        tabInfoMap.remove(componente);
        removeTabAt(index);
        
        // Callback
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
        Component componenteAtual = getSelectedComponent();
        
        // Fecha abas da direita para esquerda (para manter índices válidos)
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
    
    // ==================== OPÇÕES DE ABAS E BUSCA ====================
    
    /**
     * Registra uma opção de aba que pode ser aberta via busca.
     * @param nome Nome da aba (para exibição e busca)
     * @param factory Função que cria o componente da aba
     */
    public void registrarOpcao(String nome, Supplier<Component> factory) {
        tabOptions.add(new TabOption(nome, factory));
    }
    
    /**
     * Registra uma opção de aba com ícone.
     * @param nome Nome da aba
     * @param icone Ícone (emoji ou texto)
     * @param factory Função que cria o componente
     */
    public void registrarOpcao(String nome, String icone, Supplier<Component> factory) {
        tabOptions.add(new TabOption(nome, icone, factory, true));
    }
    
    /**
     * Registra uma opção de aba com controle de instância única.
     * @param nome Nome da aba
     * @param icone Ícone
     * @param factory Função que cria o componente
     * @param unica Se só permite uma instância da aba
     */
    public void registrarOpcao(String nome, String icone, Supplier<Component> factory, boolean unica) {
        tabOptions.add(new TabOption(nome, icone, factory, unica));
    }
    
    // ==================== MENU HIERÁRQUICO ====================
    
    /**
     * Registra um menu hierárquico com submenus.
     * @param menu Menu principal com subopções
     */
    public void registrarMenu(MenuOpcao menu) {
        menuOpcoes.add(menu);
        // Registra também os painéis para busca
        registrarPaineisParaBusca(menu, menu.getNome());
    }
    
    private void registrarPaineisParaBusca(MenuOpcao menu, String caminhoAtual) {
        menu.setCaminhoCompleto(caminhoAtual);
        
        if (menu.isPainel()) {
            // Registra como TabOption para aparecer na busca
            // Usa o caminho completo no ícone para mostrar a hierarquia
            String iconeComCaminho = menu.getIcone();
            if (!caminhoAtual.equals(menu.getNome())) {
                String caminhoParent = caminhoAtual.substring(0, caminhoAtual.lastIndexOf(" > "));
                iconeComCaminho = (menu.getIcone() != null ? menu.getIcone() : "") + 
                    " [" + caminhoParent + "]";
            }
            tabOptions.add(new TabOption(menu.getNome(), iconeComCaminho, menu::criarComponente, menu.isUnica()));
        }
        
        for (MenuOpcao sub : menu.getSubOpcoes()) {
            String novoCaminho = caminhoAtual + " > " + sub.getNome();
            registrarPaineisParaBusca(sub, novoCaminho);
        }
    }
    
    /**
     * Retorna os menus hierárquicos registrados.
     */
    public List<MenuOpcao> getMenuOpcoes() {
        return new ArrayList<>(menuOpcoes);
    }
    
    /**
     * Cria um botão dropdown para um menu hierárquico.
     * @param menu Menu com subopções
     * @return JButton com popup menu
     */
    public JButton criarBotaoMenu(MenuOpcao menu) {
        JButton btn = new JButton(menu.toString());
        btn.setFocusable(false);
        
        if (menu.isPainel()) {
            // Se é um painel direto, abre ao clicar
            btn.addActionListener(e -> abrirMenuOpcao(menu));
        } else {
            // Adiciona indicador discreto de dropdown
            btn.setText(menu.toString() + "  ⏷");
            
            // Se tem submenus, recria popup a cada clique para respeitar tema atual
            btn.addActionListener(e -> {
                JPopupMenu popup = criarPopupMenu(menu.getSubOpcoes());
                popup.show(btn, 0, btn.getHeight());
            });
        }
        
        return btn;
    }
    
    /**
     * Cria um popup menu recursivo para as subopções.
     */
    private JPopupMenu criarPopupMenu(List<MenuOpcao> opcoes) {
        JPopupMenu popup = new JPopupMenu();
        
        for (MenuOpcao opcao : opcoes) {
            if (opcao.isGrupo()) {
                // Submenu
                JMenu submenu = new JMenu(opcao.toString());
                popularSubmenu(submenu, opcao.getSubOpcoes());
                popup.add(submenu);
            } else {
                // Item que abre painel
                JMenuItem item = new JMenuItem(opcao.toString());
                item.addActionListener(e -> abrirMenuOpcao(opcao));
                popup.add(item);
            }
        }
        
        return popup;
    }
    
    /**
     * Popula um submenu recursivamente.
     */
    private void popularSubmenu(JMenu menu, List<MenuOpcao> opcoes) {
        for (MenuOpcao opcao : opcoes) {
            if (opcao.isGrupo()) {
                JMenu submenu = new JMenu(opcao.toString());
                popularSubmenu(submenu, opcao.getSubOpcoes());
                menu.add(submenu);
            } else {
                JMenuItem item = new JMenuItem(opcao.toString());
                item.addActionListener(e -> abrirMenuOpcao(opcao));
                menu.add(item);
            }
        }
    }
    
    /**
     * Abre um painel a partir de uma MenuOpcao.
     */
    public boolean abrirMenuOpcao(MenuOpcao opcao) {
        if (!opcao.isPainel()) {
            return false;
        }
        
        // Se é única, verifica se já existe
        if (opcao.isUnica()) {
            for (int i = 0; i < getTabCount(); i++) {
                String titulo = getTitleAt(i).replace("• ", "");
                if (titulo.equals(opcao.getNome())) {
                    setSelectedIndex(i);
                    return true;
                }
            }
        }
        
        // Cria nova aba
        Component componente = opcao.criarComponente();
        if (componente != null) {
            adicionarAba(opcao.getNome(), null, componente);
            return true;
        }
        return false;
    }
    
    /**
     * Busca painéis em todos os menus hierárquicos pelo nome.
     * @param nome Nome do painel
     * @return MenuOpcao encontrada ou null
     */
    public MenuOpcao buscarMenuOpcao(String nome) {
        for (MenuOpcao menu : menuOpcoes) {
            MenuOpcao encontrado = buscarEmMenu(menu, nome);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }
    
    private MenuOpcao buscarEmMenu(MenuOpcao menu, String nome) {
        if (menu.getNome().equalsIgnoreCase(nome) && menu.isPainel()) {
            return menu;
        }
        for (MenuOpcao sub : menu.getSubOpcoes()) {
            MenuOpcao encontrado = buscarEmMenu(sub, nome);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }
    
    /**
     * Retorna todos os painéis disponíveis (para busca).
     */
    public List<MenuOpcao> getTodosPaineis() {
        List<MenuOpcao> paineis = new ArrayList<>();
        for (MenuOpcao menu : menuOpcoes) {
            coletarPaineis(menu, paineis);
        }
        return paineis;
    }
    
    private void coletarPaineis(MenuOpcao menu, List<MenuOpcao> lista) {
        if (menu.isPainel()) {
            lista.add(menu);
        }
        for (MenuOpcao sub : menu.getSubOpcoes()) {
            coletarPaineis(sub, lista);
        }
    }
    
    /**
     * Busca painéis pelo texto (nome ou caminho).
     * @param texto Texto de busca
     * @return Lista de painéis que correspondem
     */
    public List<MenuOpcao> buscarPaineis(String texto) {
        List<MenuOpcao> resultado = new ArrayList<>();
        String busca = texto.toLowerCase();
        
        for (MenuOpcao painel : getTodosPaineis()) {
            if (painel.getNome().toLowerCase().contains(busca) ||
                painel.getCaminhoCompleto().toLowerCase().contains(busca)) {
                resultado.add(painel);
            }
        }
        return resultado;
    }
    
    // ==================== FIM MENU HIERÁRQUICO ====================
    
    /**
     * Retorna as opções de abas registradas.
     */
    public List<TabOption> getTabOptions() {
        return new ArrayList<>(tabOptions);
    }
    
    /**
     * Abre uma aba pelo nome da opção registrada.
     * @param nomeOpcao Nome da opção
     * @return true se a aba foi aberta ou focada
     */
    public boolean abrirOpcao(String nomeOpcao) {
        // Primeiro tenta nos menus hierárquicos
        MenuOpcao menuOpcao = buscarMenuOpcao(nomeOpcao);
        if (menuOpcao != null) {
            return abrirMenuOpcao(menuOpcao);
        }
        
        // Depois nas opções simples
        for (TabOption opcao : tabOptions) {
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
    public boolean abrirOpcao(TabOption opcao) {
        // Se é única, verifica se já existe
        if (opcao.isUnica()) {
            for (int i = 0; i < getTabCount(); i++) {
                String titulo = getTitleAt(i).replace("• ", "");
                if (titulo.equals(opcao.getNome())) {
                    setSelectedIndex(i);
                    return true;
                }
            }
        }
        
        // Cria nova aba
        Component componente = opcao.criarComponente();
        if (componente != null) {
            adicionarAba(opcao.getNome(), null, componente);
            return true;
        }
        return false;
    }
    
    /**
     * Busca opções pelo prefixo do nome.
     * @param prefixo Texto de busca
     * @return Lista de opções que correspondem
     */
    public List<TabOption> buscarOpcoes(String prefixo) {
        List<TabOption> resultado = new ArrayList<>();
        String busca = prefixo.toLowerCase();
        
        for (TabOption opcao : tabOptions) {
            if (opcao.getNome().toLowerCase().contains(busca)) {
                resultado.add(opcao);
            }
        }
        return resultado;
    }
    
    /**
     * Cria um painel de busca de abas para usar na toolbar.
     * O painel contém um campo de texto com dropdown de sugestões.
     * @return JPanel com campo de busca
     */
    public JPanel criarPainelBusca() {
        return criarPainelBusca("Buscar aba...");
    }
    
    /**
     * Cria um painel de busca de abas com placeholder customizado.
     * @param placeholder Texto placeholder
     * @return JPanel com campo de busca
     */
    public JPanel criarPainelBusca(String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setOpaque(false);
        
        JLabel label = new JLabel("🔍");
        panel.add(label, BorderLayout.WEST);
        
        JTextField searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", placeholder);
        searchField.setFocusable(true);
        
        // Container para popup (permite recriar quando necessário)
        final JPopupMenu[] popupHolder = new JPopupMenu[1];
        final JList<TabOption>[] listaHolder = new JList[1];
        
        // Função para criar/recriar o popup
        Runnable criarPopup = () -> {
            if (popupHolder[0] != null) {
                popupHolder[0].setVisible(false);
            }
            popupHolder[0] = new JPopupMenu();
            listaHolder[0] = new JList<>();
            listaHolder[0].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listaHolder[0].setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof TabOption) {
                        TabOption opt = (TabOption) value;
                        setText(opt.toString());
                    }
                    return this;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(listaHolder[0]);
            scrollPane.setPreferredSize(new Dimension(200, 150));
            popupHolder[0].add(scrollPane);
            popupHolder[0].setFocusable(false);
            
            // Clique na lista
            listaHolder[0].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        TabOption opcao = listaHolder[0].getSelectedValue();
                        if (opcao != null) {
                            abrirOpcao(opcao);
                            searchField.setText("");
                            popupHolder[0].setVisible(false);
                            TabbedDocumentPane.this.requestFocusInWindow();
                        }
                    }
                }
            });
        };
        
        // Criar popup inicial
        criarPopup.run();
        
        // Atualizar sugestões ao digitar
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { atualizarSugestoes(); }
            @Override
            public void removeUpdate(DocumentEvent e) { atualizarSugestoes(); }
            @Override
            public void changedUpdate(DocumentEvent e) { atualizarSugestoes(); }
            
            private void atualizarSugestoes() {
                String texto = searchField.getText().trim();
                
                // Recria popup se necessário (garante cores do tema)
                if (popupHolder[0] == null || !popupHolder[0].isDisplayable()) {
                    criarPopup.run();
                }
                
                JList<TabOption> listaSugestoes = listaHolder[0];
                JPopupMenu popup = popupHolder[0];
                
                if (texto.isEmpty()) {
                    // Mostra todas as opções
                    listaSugestoes.setListData(tabOptions.toArray(new TabOption[0]));
                } else {
                    // Filtra opções
                    List<TabOption> filtradas = buscarOpcoes(texto);
                    listaSugestoes.setListData(filtradas.toArray(new TabOption[0]));
                }
                
                if (listaSugestoes.getModel().getSize() > 0) {
                    listaSugestoes.setSelectedIndex(0);
                    if (!popup.isVisible()) {
                        popup.show(searchField, 0, searchField.getHeight());
                    }
                } else {
                    popup.setVisible(false);
                }
            }
        });
        
        // Ao perder foco, esconde o popup
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Pequeno delay para permitir clique na lista
                Timer timer = new Timer(200, ev -> {
                    if (listaHolder[0] != null && !listaHolder[0].hasFocus()) {
                        if (popupHolder[0] != null) {
                            popupHolder[0].setVisible(false);
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        // Navegação com teclado
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                JList<TabOption> listaSugestoes = listaHolder[0];
                JPopupMenu popup = popupHolder[0];
                
                if (listaSugestoes == null || popup == null) return;
                
                int size = listaSugestoes.getModel().getSize();
                int selected = listaSugestoes.getSelectedIndex();
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (size > 0) {
                            listaSugestoes.setSelectedIndex(Math.min(selected + 1, size - 1));
                            listaSugestoes.ensureIndexIsVisible(listaSugestoes.getSelectedIndex());
                        }
                        e.consume();
                        break;
                        
                    case KeyEvent.VK_UP:
                        if (size > 0) {
                            listaSugestoes.setSelectedIndex(Math.max(selected - 1, 0));
                            listaSugestoes.ensureIndexIsVisible(listaSugestoes.getSelectedIndex());
                        }
                        e.consume();
                        break;
                        
                    case KeyEvent.VK_ENTER:
                        TabOption opcaoSelecionada = listaSugestoes.getSelectedValue();
                        if (opcaoSelecionada != null) {
                            abrirOpcao(opcaoSelecionada);
                            searchField.setText("");
                            popup.setVisible(false);
                            TabbedDocumentPane.this.requestFocusInWindow();
                        }
                        e.consume();
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        popup.setVisible(false);
                        searchField.setText("");
                        TabbedDocumentPane.this.requestFocusInWindow();
                        e.consume();
                        break;
                }
            }
        });
        
        panel.add(searchField, BorderLayout.CENTER);
        
        // Define tamanho máximo para evitar expansão em toolbars
        panel.setMaximumSize(panel.getPreferredSize());
        
        return panel;
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
        
        // Ícone (se houver)
        if (info.icone != null) {
            JLabel iconLabel = new JLabel(info.icone);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
            tabPanel.add(iconLabel);
        }
        
        // Label do título
        JLabel titleLabel = new JLabel(getTituloExibicao(info));
        tabPanel.add(titleLabel);
        
        // Botão de fechar (se fechável)
        if (info.fechavel) {
            JButton closeButton = criarBotaoFechar(componente);
            tabPanel.add(closeButton);
        }
        
        // Menu de contexto
        tabPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Seleciona a aba ao clicar
                int index = indexOfComponent(componente);
                if (index >= 0) {
                    setSelectedIndex(index);
                }
                
                // Menu de contexto
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
                
                // Cor do X
                if (getModel().isRollover()) {
                    g2.setColor(UIManager.getColor("TabbedPane.closeHoverForeground") != null 
                        ? UIManager.getColor("TabbedPane.closeHoverForeground") 
                        : new Color(200, 50, 50));
                } else {
                    g2.setColor(UIManager.getColor("TabbedPane.closeForeground") != null
                        ? UIManager.getColor("TabbedPane.closeForeground")
                        : Color.GRAY);
                }
                
                // Desenha o X
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
        
        // Fechar
        JMenuItem fecharItem = new JMenuItem("Fechar");
        fecharItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        fecharItem.addActionListener(ev -> {
            int index = indexOfComponent(componente);
            if (index >= 0) fecharAba(index);
        });
        fecharItem.setEnabled(info != null && info.fechavel);
        menu.add(fecharItem);
        
        // Fechar Outras
        JMenuItem fecharOutrasItem = new JMenuItem("Fechar Outras");
        fecharOutrasItem.addActionListener(ev -> fecharOutras());
        fecharOutrasItem.setEnabled(getTabCount() > 1);
        menu.add(fecharOutrasItem);
        
        // Fechar Todas
        JMenuItem fecharTodasItem = new JMenuItem("Fechar Todas");
        fecharTodasItem.addActionListener(ev -> fecharTodas());
        menu.add(fecharTodasItem);
        
        menu.addSeparator();
        
        // Fechar Abas à Direita
        int indexAtual = indexOfComponent(componente);
        JMenuItem fecharDireitaItem = new JMenuItem("Fechar Abas à Direita");
        fecharDireitaItem.addActionListener(ev -> {
            for (int i = getTabCount() - 1; i > indexAtual; i--) {
                fecharAba(i);
            }
        });
        fecharDireitaItem.setEnabled(indexAtual < getTabCount() - 1);
        menu.add(fecharDireitaItem);
        
        // Fechar Abas à Esquerda
        JMenuItem fecharEsquerdaItem = new JMenuItem("Fechar Abas à Esquerda");
        fecharEsquerdaItem.addActionListener(ev -> {
            for (int i = indexAtual - 1; i >= 0; i--) {
                fecharAba(i);
            }
        });
        fecharEsquerdaItem.setEnabled(indexAtual > 0);
        menu.add(fecharEsquerdaItem);
        
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

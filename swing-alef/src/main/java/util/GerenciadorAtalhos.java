package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Gerenciador centralizado de atalhos de teclado.
 * 
 * <p>Permite registrar atalhos globais e por componente de forma simples.</p>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Atalhos globais no frame
 * GerenciadorAtalhos.registrarGlobal(frame, Atalho.SALVAR, () -> salvar());
 * GerenciadorAtalhos.registrarGlobal(frame, Atalho.NOVO, () -> novo());
 * 
 * // Atalho específico em componente
 * GerenciadorAtalhos.registrar(botao, "ctrl S", () -> salvar());
 * 
 * // ESC para fechar dialog
 * GerenciadorAtalhos.registrarEsc(dialog, () -> dialog.dispose());
 * }</pre>
 * 
 * @author swing-alef
 */
public class GerenciadorAtalhos {
    
    /**
     * Atalhos padrão predefinidos.
     */
    public enum Atalho {
        SALVAR("ctrl S", "Salvar", KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
        NOVO("ctrl N", "Novo", KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
        ABRIR("ctrl O", "Abrir", KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
        FECHAR("ctrl W", "Fechar", KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK),
        SAIR("alt F4", "Sair", KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK),
        CANCELAR("ESCAPE", "Cancelar", KeyEvent.VK_ESCAPE, 0),
        CONFIRMAR("ENTER", "Confirmar", KeyEvent.VK_ENTER, 0),
        EXCLUIR("DELETE", "Excluir", KeyEvent.VK_DELETE, 0),
        EDITAR("ctrl E", "Editar", KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
        BUSCAR("ctrl F", "Buscar", KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
        IMPRIMIR("ctrl P", "Imprimir", KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
        ATUALIZAR("F5", "Atualizar", KeyEvent.VK_F5, 0),
        AJUDA("F1", "Ajuda", KeyEvent.VK_F1, 0),
        TELA_CHEIA("F11", "Tela Cheia", KeyEvent.VK_F11, 0),
        PROXIMO("ctrl TAB", "Próxima Aba", KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK),
        ANTERIOR("ctrl shift TAB", "Aba Anterior", KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
        DESFAZER("ctrl Z", "Desfazer", KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
        REFAZER("ctrl Y", "Refazer", KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK),
        COPIAR("ctrl C", "Copiar", KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
        COLAR("ctrl V", "Colar", KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
        RECORTAR("ctrl X", "Recortar", KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
        SELECIONAR_TUDO("ctrl A", "Selecionar Tudo", KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK);
        
        private final String keystroke;
        private final String descricao;
        private final int keyCode;
        private final int modifiers;
        
        Atalho(String keystroke, String descricao, int keyCode, int modifiers) {
            this.keystroke = keystroke;
            this.descricao = descricao;
            this.keyCode = keyCode;
            this.modifiers = modifiers;
        }
        
        public String getKeystroke() {
            return keystroke;
        }
        
        public String getDescricao() {
            return descricao;
        }
        
        public KeyStroke toKeyStroke() {
            return KeyStroke.getKeyStroke(keyCode, modifiers);
        }
        
        /**
         * Retorna texto formatado para exibir em tooltip.
         * Ex: "Ctrl+S"
         */
        public String getTextoTooltip() {
            StringBuilder sb = new StringBuilder();
            if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) sb.append("Ctrl+");
            if ((modifiers & KeyEvent.ALT_DOWN_MASK) != 0) sb.append("Alt+");
            if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) sb.append("Shift+");
            sb.append(KeyEvent.getKeyText(keyCode));
            return sb.toString();
        }
    }
    
    // Cache de ações registradas por componente
    private static final Map<JComponent, Map<String, Action>> componentActions = new HashMap<>();
    
    /**
     * Registra um atalho em um componente usando um Atalho predefinido.
     * 
     * @param componente Componente onde registrar o atalho
     * @param atalho Atalho predefinido
     * @param acao Ação a executar
     */
    public static void registrar(JComponent componente, Atalho atalho, Runnable acao) {
        registrar(componente, atalho.getKeystroke(), atalho.name(), acao);
    }
    
    /**
     * Registra um atalho customizado em um componente.
     * 
     * @param componente Componente onde registrar
     * @param keystroke String do atalho (ex: "ctrl S", "F5", "alt ENTER")
     * @param acao Ação a executar
     */
    public static void registrar(JComponent componente, String keystroke, Runnable acao) {
        registrar(componente, keystroke, keystroke.replace(" ", "_"), acao);
    }
    
    /**
     * Registra um atalho com nome específico.
     */
    public static void registrar(JComponent componente, String keystroke, String nomeAcao, Runnable acao) {
        KeyStroke ks = KeyStroke.getKeyStroke(keystroke);
        if (ks == null) {
            System.err.println("GerenciadorAtalhos: Keystroke inválido: " + keystroke);
            return;
        }
        
        Action action = new AbstractAction(nomeAcao) {
            @Override
            public void actionPerformed(ActionEvent e) {
                acao.run();
            }
        };
        
        InputMap inputMap = componente.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = componente.getActionMap();
        
        inputMap.put(ks, nomeAcao);
        actionMap.put(nomeAcao, action);
        
        // Armazena no cache
        componentActions.computeIfAbsent(componente, k -> new HashMap<>()).put(nomeAcao, action);
    }
    
    /**
     * Registra atalhos globais em um JFrame ou JDialog.
     * Os atalhos funcionam mesmo quando o foco está em subcomponentes.
     * 
     * @param janela Frame ou Dialog
     * @param atalho Atalho predefinido
     * @param acao Ação a executar
     */
    public static void registrarGlobal(RootPaneContainer janela, Atalho atalho, Runnable acao) {
        JRootPane rootPane = janela.getRootPane();
        registrar(rootPane, atalho, acao);
    }
    
    /**
     * Registra atalho customizado global.
     */
    public static void registrarGlobal(RootPaneContainer janela, String keystroke, Runnable acao) {
        JRootPane rootPane = janela.getRootPane();
        registrar(rootPane, keystroke, acao);
    }
    
    /**
     * Registra ESC para fechar um dialog.
     * 
     * @param dialog Dialog a ser fechado com ESC
     */
    public static void registrarEsc(JDialog dialog) {
        registrarEsc(dialog, dialog::dispose);
    }
    
    /**
     * Registra ESC com ação customizada.
     */
    public static void registrarEsc(RootPaneContainer janela, Runnable acao) {
        registrarGlobal(janela, Atalho.CANCELAR, acao);
    }
    
    /**
     * Registra ENTER para confirmar em um dialog.
     * 
     * @param dialog Dialog
     * @param acao Ação de confirmação
     */
    public static void registrarEnter(RootPaneContainer dialog, Runnable acao) {
        registrarGlobal(dialog, Atalho.CONFIRMAR, acao);
    }
    
    /**
     * Remove um atalho registrado.
     */
    public static void remover(JComponent componente, Atalho atalho) {
        remover(componente, atalho.name());
    }
    
    /**
     * Remove um atalho por nome.
     */
    public static void remover(JComponent componente, String nomeAcao) {
        ActionMap actionMap = componente.getActionMap();
        actionMap.remove(nomeAcao);
        
        Map<String, Action> actions = componentActions.get(componente);
        if (actions != null) {
            actions.remove(nomeAcao);
        }
    }
    
    /**
     * Remove todos os atalhos de um componente.
     */
    public static void removerTodos(JComponent componente) {
        Map<String, Action> actions = componentActions.remove(componente);
        if (actions != null) {
            ActionMap actionMap = componente.getActionMap();
            for (String nome : actions.keySet()) {
                actionMap.remove(nome);
            }
        }
    }
    
    /**
     * Configura um botão com atalho e tooltip.
     * Atualiza o tooltip para incluir o atalho.
     * 
     * @param botao Botão a configurar
     * @param atalho Atalho a associar
     * @param acao Ação do botão
     */
    public static void configurarBotao(JButton botao, Atalho atalho, Runnable acao) {
        // Configura a ação do botão
        botao.addActionListener(e -> acao.run());
        
        // Registra o atalho
        Container parent = botao.getTopLevelAncestor();
        if (parent instanceof RootPaneContainer) {
            registrarGlobal((RootPaneContainer) parent, atalho, acao);
        }
        
        // Atualiza tooltip
        String tooltipAtual = botao.getToolTipText();
        String textoAtalho = " (" + atalho.getTextoTooltip() + ")";
        
        if (tooltipAtual == null || tooltipAtual.isEmpty()) {
            botao.setToolTipText(atalho.getDescricao() + textoAtalho);
        } else if (!tooltipAtual.contains(textoAtalho)) {
            botao.setToolTipText(tooltipAtual + textoAtalho);
        }
    }
    
    /**
     * Configura atalhos padrão de CRUD em um painel.
     * 
     * @param container Container (frame ou dialog)
     * @param onNovo Ação para Novo (Ctrl+N)
     * @param onSalvar Ação para Salvar (Ctrl+S)
     * @param onExcluir Ação para Excluir (Delete)
     * @param onBuscar Ação para Buscar (Ctrl+F)
     */
    public static void configurarCrud(RootPaneContainer container, 
                                       Runnable onNovo,
                                       Runnable onSalvar,
                                       Runnable onExcluir,
                                       Runnable onBuscar) {
        if (onNovo != null) registrarGlobal(container, Atalho.NOVO, onNovo);
        if (onSalvar != null) registrarGlobal(container, Atalho.SALVAR, onSalvar);
        if (onExcluir != null) registrarGlobal(container, Atalho.EXCLUIR, onExcluir);
        if (onBuscar != null) registrarGlobal(container, Atalho.BUSCAR, onBuscar);
    }
    
    /**
     * Configura atalhos padrão de navegação de abas.
     * 
     * @param tabbedPane JTabbedPane a controlar
     */
    public static void configurarNavegacaoAbas(JTabbedPane tabbedPane) {
        Container parent = tabbedPane.getTopLevelAncestor();
        if (!(parent instanceof RootPaneContainer)) return;
        
        RootPaneContainer container = (RootPaneContainer) parent;
        
        // Ctrl+Tab = próxima aba
        registrarGlobal(container, Atalho.PROXIMO, () -> {
            int index = tabbedPane.getSelectedIndex();
            int count = tabbedPane.getTabCount();
            if (count > 0) {
                tabbedPane.setSelectedIndex((index + 1) % count);
            }
        });
        
        // Ctrl+Shift+Tab = aba anterior
        registrarGlobal(container, Atalho.ANTERIOR, () -> {
            int index = tabbedPane.getSelectedIndex();
            int count = tabbedPane.getTabCount();
            if (count > 0) {
                tabbedPane.setSelectedIndex((index - 1 + count) % count);
            }
        });
        
        // Ctrl+W = fechar aba atual
        registrarGlobal(container, Atalho.FECHAR, () -> {
            int index = tabbedPane.getSelectedIndex();
            if (index > 0) { // Não fecha a primeira aba (geralmente é a principal)
                tabbedPane.removeTabAt(index);
            }
        });
    }
    
    /**
     * Utilitário para obter texto de tooltip com atalho.
     * 
     * @param descricao Descrição base
     * @param atalho Atalho associado
     * @return Texto formatado "Descrição (Ctrl+X)"
     */
    public static String tooltipComAtalho(String descricao, Atalho atalho) {
        return descricao + " (" + atalho.getTextoTooltip() + ")";
    }
    
    /**
     * Lista todos os atalhos predefinidos.
     */
    public static void listarAtalhos(Consumer<String> output) {
        output.accept("=== ATALHOS DISPONÍVEIS ===");
        for (Atalho a : Atalho.values()) {
            output.accept(String.format("%-15s %s - %s", 
                a.getTextoTooltip(), a.name(), a.getDescricao()));
        }
    }
}

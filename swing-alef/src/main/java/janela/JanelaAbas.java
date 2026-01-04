package janela;

import dialogo.DialogoUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Frame principal com suporte a abas de documentos.
 * 
 * <h3>Características:</h3>
 * <ul>
 *   <li>Abas fecháveis com indicador de modificações</li>
 *   <li>Barra de navegação/menu opcional</li>
 *   <li>Suporte a F11 para tela cheia</li>
 *   <li>Atalhos de teclado para navegação entre abas</li>
 * </ul>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * JanelaAbas frame = new JanelaAbas("Minha Aplicação");
 * frame.adicionarAba("Clientes", new ClientePanel());
 * frame.adicionarAba("Produtos", new ProdutoPanel());
 * frame.setVisible(true);
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class JanelaAbas extends JFrame {
    
    private final PainelAbas painelAbas;
    private final JPanel painelCabecalho;
    private final JPanel barraStatus;
    private final JLabel labelStatus;
    private boolean telaCheia = false;
    
    /**
     * Cria uma nova janela com abas.
     * 
     * @param titulo título da janela
     */
    public JanelaAbas(String titulo) {
        super(titulo);
        
        // Inicializa componentes
        painelAbas = new PainelAbas();
        painelCabecalho = new JPanel(new BorderLayout());
        barraStatus = new JPanel(new BorderLayout());
        labelStatus = new JLabel(" ");
        
        configurarLayout();
        configurarBarraStatus();
        configurarAtalhos();
        configurarEventos();
        configurarJanela();
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Header (pode conter menu, toolbar, etc)
        painelCabecalho.setOpaque(false);
        add(painelCabecalho, BorderLayout.NORTH);
        
        // Conteúdo principal (abas)
        add(painelAbas, BorderLayout.CENTER);
        
        // Barra de status
        add(barraStatus, BorderLayout.SOUTH);
    }
    
    private void configurarBarraStatus() {
        barraStatus.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        barraStatus.setPreferredSize(new Dimension(0, 28));
        barraStatus.add(labelStatus, BorderLayout.WEST);
    }
    
    private void configurarAtalhos() {
        // F11 - Tela cheia
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        getRootPane().getActionMap().put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarTelaCheia();
            }
        });
        
        // Ctrl+N - Nova aba (pode ser sobrescrito)
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novaAba");
        getRootPane().getActionMap().put("novaAba", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNovaAba();
            }
        });
    }
    
    private void configurarEventos() {
        // Atualiza status quando aba é selecionada
        painelAbas.setOnTabSelected(comp -> {
            atualizarStatus();
        });
        
        // Confirmação ao fechar janela
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharAplicacao();
            }
        });
    }
    
    private void configurarJanela() {
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    // ==================== MÉTODOS PÚBLICOS ====================
    
    /**
     * Adiciona uma nova aba.
     * @param titulo Título da aba
     * @param componente Componente a exibir
     */
    public void adicionarAba(String titulo, Component componente) {
        painelAbas.adicionarAba(titulo, componente);
        atualizarStatus();
    }
    
    /**
     * Adiciona uma nova aba com ícone.
     * @param titulo Título da aba
     * @param icone Ícone da aba
     * @param componente Componente a exibir
     */
    public void adicionarAba(String titulo, Icon icone, Component componente) {
        painelAbas.adicionarAba(titulo, icone, componente);
        atualizarStatus();
    }
    
    /**
     * Adiciona uma aba fixa (não fechável).
     * @param titulo Título da aba
     * @param icone Ícone da aba
     * @param componente Componente a exibir
     */
    public void adicionarAbaFixa(String titulo, Icon icone, Component componente) {
        painelAbas.adicionarAbaFixa(titulo, icone, componente);
        atualizarStatus();
    }
    
    /**
     * Retorna o painel de abas para manipulação direta.
     * @return PainelAbas
     */
    public PainelAbas getPainelAbas() {
        return painelAbas;
    }
    
    /**
     * Retorna o painel de cabeçalho para adicionar menus/toolbars.
     * @return JPanel do header
     */
    public JPanel getPainelCabecalho() {
        return painelCabecalho;
    }
    
    /**
     * Define o texto da barra de status.
     * @param texto Texto a exibir
     */
    public void setStatus(String texto) {
        labelStatus.setText(texto != null ? texto : " ");
    }
    
    /**
     * Mostra ou oculta a barra de status.
     * @param visivel true para mostrar
     */
    public void setBarraStatusVisivel(boolean visivel) {
        barraStatus.setVisible(visivel);
    }
    
    /**
     * Alterna entre modo tela cheia e janela normal.
     */
    public void alternarTelaCheia() {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        
        if (!device.isFullScreenSupported()) {
            return;
        }
        
        if (telaCheia) {
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setVisible(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            telaCheia = false;
        } else {
            dispose();
            setUndecorated(true);
            device.setFullScreenWindow(this);
            setVisible(true);
            telaCheia = true;
        }
    }
    
    /**
     * Fecha a aplicação, verificando modificações pendentes.
     */
    public void fecharAplicacao() {
        if (painelAbas.existeModificacoes()) {
            if (!DialogoUtil.confirmar(this, 
                "Existem abas com alterações não salvas.\nDeseja sair mesmo assim?",
                "Confirmação")) {
                return;
            }
        }
        dispose();
        System.exit(0);
    }
    
    // ==================== MÉTODOS PROTEGIDOS ====================
    
    /**
     * Chamado quando Ctrl+N é pressionado.
     * Sobrescreva para criar nova aba do tipo desejado.
     */
    protected void onNovaAba() {
        // Implementação padrão vazia
        // Sobrescreva para adicionar comportamento
    }
    
    /**
     * Atualiza a barra de status com informações das abas.
     */
    protected void atualizarStatus() {
        int total = painelAbas.getTabCount();
        int index = painelAbas.getSelectedIndex();
        
        if (total == 0) {
            setStatus("Nenhuma aba aberta");
        } else {
            String titulo = painelAbas.getTitleAt(index);
            setStatus("Aba " + (index + 1) + " de " + total + " - " + titulo);
        }
    }
}

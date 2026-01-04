package dialogo;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**
 * Diálogo de configurações com categorias.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * DialogoConfiguracoes config = new DialogoConfiguracoes(frame);
 * config.adicionarCategoria("Geral", painelGeral);
 * config.adicionarCategoria("Aparência", painelAparencia);
 * config.adicionarCategoria("Avançado", painelAvancado);
 * config.setAoSalvar(() -> salvarConfiguracoes());
 * config.setVisible(true);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class DialogoConfiguracoes extends JDialog {
    
    private JList<String> listaCategorias;
    private DefaultListModel<String> modeloLista;
    private JPanel painelConteudo;
    private CardLayout cardLayout;
    
    private Map<String, JPanel> categorias = new LinkedHashMap<>();
    private Runnable aoSalvar;
    private Runnable aoCancelar;
    
    public DialogoConfiguracoes(Frame parent) {
        super(parent, "Configurações", true);
        inicializar();
    }
    
    private void inicializar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Lista de categorias
        modeloLista = new DefaultListModel<>();
        listaCategorias = new JList<>(modeloLista);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCategorias.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        listaCategorias.setFixedCellHeight(35);
        
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String categoria = listaCategorias.getSelectedValue();
                if (categoria != null) {
                    cardLayout.show(painelConteudo, categoria);
                }
            }
        });
        
        JScrollPane scrollLista = new JScrollPane(listaCategorias);
        scrollLista.setPreferredSize(new Dimension(180, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Categorias"));
        painelPrincipal.add(scrollLista, BorderLayout.WEST);
        
        // Conteúdo
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBorder(BorderFactory.createTitledBorder("Opções"));
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        
        JButton btnAplicar = new JButton("Aplicar");
        btnAplicar.addActionListener(e -> {
            if (aoSalvar != null) aoSalvar.run();
        });
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAplicar);
        painelBotoes.add(btnCancelar);
        
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        setContentPane(painelPrincipal);
    }
    
    /**
     * Adiciona uma categoria de configuração.
     */
    public void adicionarCategoria(String nome, JPanel painel) {
        categorias.put(nome, painel);
        modeloLista.addElement(nome);
        painelConteudo.add(painel, nome);
        
        if (categorias.size() == 1) {
            listaCategorias.setSelectedIndex(0);
        }
    }
    
    /**
     * Remove uma categoria.
     */
    public void removerCategoria(String nome) {
        JPanel painel = categorias.remove(nome);
        if (painel != null) {
            modeloLista.removeElement(nome);
            painelConteudo.remove(painel);
        }
    }
    
    /**
     * Retorna o painel de uma categoria.
     */
    public JPanel getCategoria(String nome) {
        return categorias.get(nome);
    }
    
    /**
     * Seleciona uma categoria.
     */
    public void selecionarCategoria(String nome) {
        listaCategorias.setSelectedValue(nome, true);
    }
    
    /**
     * Define ação ao salvar.
     */
    public void setAoSalvar(Runnable acao) {
        this.aoSalvar = acao;
    }
    
    /**
     * Define ação ao cancelar.
     */
    public void setAoCancelar(Runnable acao) {
        this.aoCancelar = acao;
    }
    
    private void salvar() {
        if (aoSalvar != null) {
            aoSalvar.run();
        }
        dispose();
    }
    
    private void cancelar() {
        if (aoCancelar != null) {
            aoCancelar.run();
        }
        dispose();
    }
    
    /**
     * Cria um painel de opções com layout de formulário.
     */
    public static JPanel criarPainelOpcoes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return painel;
    }
    
    /**
     * Adiciona uma opção ao painel.
     */
    public static void adicionarOpcao(JPanel painel, String rotulo, JComponent componente) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int linha = painel.getComponentCount() / 2;
        
        gbc.gridx = 0;
        gbc.gridy = linha;
        painel.add(new JLabel(rotulo), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, gbc);
    }
}

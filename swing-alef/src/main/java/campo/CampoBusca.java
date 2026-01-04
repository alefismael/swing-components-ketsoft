package campo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Campo de busca com dropdown de sugestões.
 * Componente reutilizável para busca e seleção de itens.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Busca em tempo real</li>
 *   <li>Dropdown de sugestões</li>
 *   <li>Navegação por teclado</li>
 *   <li>Funções de filtro customizáveis</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * // Com objetos simples
 * CampoBusca<String> busca = new CampoBusca<>(frutas);
 * busca.setPlaceholder("Buscar fruta...");
 * busca.aoSelecionar(fruta -> System.out.println("Selecionou: " + fruta));
 * 
 * // Com objetos customizados
 * CampoBusca<Cliente> buscaCliente = new CampoBusca<>(clientes);
 * buscaCliente.setFuncaoExibicao(Cliente::getNome);
 * buscaCliente.setFuncaoFiltro((c, texto) -> c.getNome().toLowerCase().contains(texto));
 * }</pre>
 * 
 * @param <T> Tipo dos itens da lista
 * @author alefi
 */
public class CampoBusca<T> extends JPanel {
    
    private final JTextField textField;
    private final JPopupMenu popup;
    private final JList<T> listaSugestoes;
    private final DefaultListModel<T> listModel;
    
    private List<T> todosItens;
    private Function<T, String> funcaoExibicao;
    private FuncaoFiltro<T> funcaoFiltro;
    private Consumer<T> callbackSelecao;
    private boolean mostrarTodosAoFocar = true;
    private int maxLinhasVisiveis = 8;
    
    /**
     * Interface funcional para filtro customizado.
     * @param <T> Tipo do item
     */
    @FunctionalInterface
    public interface FuncaoFiltro<T> {
        /**
         * Verifica se o item corresponde ao texto de busca.
         * @param item item a verificar
         * @param textoBusca texto digitado
         * @return true se corresponde
         */
        boolean corresponde(T item, String textoBusca);
    }
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoBusca() {
        this(new ArrayList<>());
    }
    
    /**
     * Construtor com lista de itens.
     * @param itens Lista de itens para busca
     */
    public CampoBusca(List<T> itens) {
        this.todosItens = new ArrayList<>(itens);
        this.funcaoExibicao = Object::toString;
        this.funcaoFiltro = (item, texto) -> 
            funcaoExibicao.apply(item).toLowerCase().contains(texto.toLowerCase());
        
        setLayout(new BorderLayout(4, 0));
        setOpaque(false);
        
        // Ícone de busca
        JLabel icone = new JLabel("🔍");
        icone.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        add(icone, BorderLayout.WEST);
        
        // Campo de texto
        textField = new JTextField(20);
        textField.putClientProperty("JTextField.placeholderText", "Buscar...");
        add(textField, BorderLayout.CENTER);
        
        // Lista de sugestões
        listModel = new DefaultListModel<>();
        listaSugestoes = new JList<>(listModel);
        listaSugestoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugestoes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    @SuppressWarnings("unchecked")
                    T item = (T) value;
                    setText(funcaoExibicao.apply(item));
                }
                return this;
            }
        });
        
        // Popup
        popup = new JPopupMenu();
        popup.setFocusable(false);
        JScrollPane scrollPane = new JScrollPane(listaSugestoes);
        scrollPane.setBorder(null);
        popup.add(scrollPane);
        
        // Listeners
        configurarListeners();
    }
    
    private void configurarListeners() {
        // Atualizar sugestões ao digitar
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarSugestoes(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarSugestoes(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarSugestoes(); }
        });
        
        // Mostrar todas ao ganhar foco
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (mostrarTodosAoFocar && textField.getText().isEmpty()) {
                    mostrarTodosItens();
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // Delay para permitir clique na lista
                Timer timer = new Timer(200, ev -> {
                    if (!listaSugestoes.hasFocus()) {
                        popup.setVisible(false);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        // Navegação por teclado
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int size = listModel.getSize();
                int selected = listaSugestoes.getSelectedIndex();
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (size > 0) {
                            if (!popup.isVisible()) {
                                mostrarTodosItens();
                            }
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
                        selecionarItemAtual();
                        e.consume();
                        break;
                        
                    case KeyEvent.VK_ESCAPE:
                        popup.setVisible(false);
                        e.consume();
                        break;
                }
            }
        });
        
        // Seleção por clique
        listaSugestoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selecionarItemAtual();
                }
            }
        });
    }
    
    private void atualizarSugestoes() {
        String texto = textField.getText().trim();
        listModel.clear();
        
        if (texto.isEmpty()) {
            if (mostrarTodosAoFocar) {
                mostrarTodosItens();
            } else {
                popup.setVisible(false);
            }
            return;
        }
        
        // Filtrar itens
        for (T item : todosItens) {
            if (funcaoFiltro.corresponde(item, texto)) {
                listModel.addElement(item);
            }
        }
        
        mostrarPopup();
    }
    
    private void mostrarTodosItens() {
        listModel.clear();
        for (T item : todosItens) {
            listModel.addElement(item);
        }
        mostrarPopup();
    }
    
    private void mostrarPopup() {
        if (listModel.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        
        // Selecionar primeiro item
        if (listaSugestoes.getSelectedIndex() < 0) {
            listaSugestoes.setSelectedIndex(0);
        }
        
        // Calcular tamanho do popup
        int linhasVisiveis = Math.min(listModel.getSize(), maxLinhasVisiveis);
        int alturaLinha = listaSugestoes.getFixedCellHeight();
        if (alturaLinha <= 0) {
            alturaLinha = 25;
        }
        
        int alturaPopup = linhasVisiveis * alturaLinha + 10;
        int larguraPopup = Math.max(textField.getWidth(), 200);
        
        popup.setPopupSize(larguraPopup, alturaPopup);
        
        if (!popup.isVisible()) {
            popup.show(textField, 0, textField.getHeight());
        }
    }
    
    private void selecionarItemAtual() {
        T selecionado = listaSugestoes.getSelectedValue();
        
        // Se nenhum item estiver selecionado mas há itens na lista, seleciona o primeiro
        if (selecionado == null && listModel.getSize() > 0) {
            selecionado = listModel.getElementAt(0);
        }
        
        if (selecionado != null) {
            textField.setText(funcaoExibicao.apply(selecionado));
            popup.setVisible(false);
            
            if (callbackSelecao != null) {
                callbackSelecao.accept(selecionado);
            }
        }
    }
    
    // ==================== API Pública ====================
    
    /**
     * Define os itens para busca.
     * @param itens Lista de itens
     */
    public void setItens(List<T> itens) {
        this.todosItens = new ArrayList<>(itens);
        listModel.clear();
    }
    
    /**
     * Adiciona um item à lista.
     * @param item Item a adicionar
     */
    public void adicionarItem(T item) {
        this.todosItens.add(item);
    }
    
    /**
     * Remove um item da lista.
     * @param item Item a remover
     */
    public void removerItem(T item) {
        this.todosItens.remove(item);
    }
    
    /**
     * Limpa todos os itens.
     */
    public void limparItens() {
        this.todosItens.clear();
        listModel.clear();
    }
    
    /**
     * Retorna a lista de todos os itens.
     * @return Lista de itens
     */
    public List<T> getItens() {
        return new ArrayList<>(todosItens);
    }
    
    /**
     * Define o texto placeholder.
     * @param placeholder Texto de placeholder
     */
    public void setPlaceholder(String placeholder) {
        textField.putClientProperty("JTextField.placeholderText", placeholder);
    }
    
    /**
     * Define a função para exibir itens como texto.
     * @param funcao Função que converte T em String
     */
    public void setFuncaoExibicao(Function<T, String> funcao) {
        this.funcaoExibicao = funcao;
    }
    
    /**
     * Define a função de filtro customizada.
     * @param funcao Função que determina se item corresponde ao texto
     */
    public void setFuncaoFiltro(FuncaoFiltro<T> funcao) {
        this.funcaoFiltro = funcao;
    }
    
    /**
     * Define o callback de seleção.
     * @param callback Função chamada quando um item é selecionado
     */
    public void aoSelecionar(Consumer<T> callback) {
        this.callbackSelecao = callback;
    }
    
    /**
     * Define se mostra todos os itens ao ganhar foco.
     * @param mostrar true para mostrar todos ao focar
     */
    public void setMostrarTodosAoFocar(boolean mostrar) {
        this.mostrarTodosAoFocar = mostrar;
    }
    
    /**
     * Define o número máximo de linhas visíveis no dropdown.
     * @param linhas Número de linhas (padrão: 8)
     */
    public void setMaxLinhasVisiveis(int linhas) {
        this.maxLinhasVisiveis = linhas;
    }
    
    /**
     * Obtém o texto digitado.
     * @return Texto atual do campo
     */
    public String getTexto() {
        return textField.getText();
    }
    
    /**
     * Define o texto do campo.
     * @param texto Texto a definir
     */
    public void setTexto(String texto) {
        textField.setText(texto);
    }
    
    /**
     * Limpa o texto do campo.
     */
    public void limparTexto() {
        textField.setText("");
    }
    
    /**
     * Obtém o item selecionado atualmente na lista.
     * @return Item selecionado ou null
     */
    public T getItemSelecionado() {
        return listaSugestoes.getSelectedValue();
    }
    
    /**
     * Obtém o campo de texto interno.
     * @return JTextField interno
     */
    public JTextField getCampoTexto() {
        return textField;
    }
    
    /**
     * Obtém a lista de sugestões.
     * @return JList de sugestões
     */
    public JList<T> getListaSugestoes() {
        return listaSugestoes;
    }
    
    /**
     * Define o número de colunas do campo de texto.
     * @param colunas Número de colunas
     */
    public void setColunas(int colunas) {
        textField.setColumns(colunas);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
    
    @Override
    public boolean requestFocusInWindow() {
        return textField.requestFocusInWindow();
    }
}

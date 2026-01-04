package dialogo;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * Diálogo de busca genérico com filtro e seleção.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * DialogoBusca<Cliente> busca = new DialogoBusca<>(frame, "Buscar Cliente");
 * busca.setColunas("ID", "Nome", "CPF");
 * busca.setConversor(c -> new Object[]{c.getId(), c.getNome(), c.getCpf()});
 * busca.setFonteDados(termo -> clienteService.buscar(termo));
 * busca.setAoSelecionar(cliente -> preencherFormulario(cliente));
 * busca.setVisible(true);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class DialogoBusca<T> extends JDialog {
    
    private JTextField campoFiltro;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSelecionar;
    
    private String[] colunas = {"Item"};
    private Function<T, Object[]> conversor;
    private Function<String, List<T>> fonteDados;
    private Consumer<T> aoSelecionar;
    
    private List<T> itens = new ArrayList<>();
    private T itemSelecionado;
    
    private Timer timerBusca;
    
    public DialogoBusca(Frame parent, String titulo) {
        super(parent, titulo, true);
        inicializar();
    }
    
    private void inicializar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(getParent());
        
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campo de filtro
        JPanel painelFiltro = new JPanel(new BorderLayout(5, 0));
        painelFiltro.add(new JLabel("🔍"), BorderLayout.WEST);
        
        campoFiltro = new JTextField();
        campoFiltro.putClientProperty("JTextField.placeholderText", "Digite para buscar...");
        campoFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { agendarBusca(); }
            @Override
            public void removeUpdate(DocumentEvent e) { agendarBusca(); }
            @Override
            public void changedUpdate(DocumentEvent e) { agendarBusca(); }
        });
        painelFiltro.add(campoFiltro, BorderLayout.CENTER);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        painelFiltro.add(btnBuscar, BorderLayout.EAST);
        
        painel.add(painelFiltro, BorderLayout.NORTH);
        
        // Tabela
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabela.setColumnIdentifiers(colunas);
        
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setReorderingAllowed(false);
        
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selecionar();
                }
            }
        });
        
        tabela.getSelectionModel().addListSelectionListener(e -> {
            btnSelecionar.setEnabled(tabela.getSelectedRow() >= 0);
        });
        
        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnSelecionar = new JButton("Selecionar");
        btnSelecionar.setEnabled(false);
        btnSelecionar.addActionListener(e -> selecionar());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnSelecionar);
        painelBotoes.add(btnCancelar);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        setContentPane(painel);
        
        // Enter para selecionar
        tabela.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selecionar");
        tabela.getActionMap().put("selecionar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionar();
            }
        });
        
        // Timer para busca com delay
        timerBusca = new Timer(300, e -> buscar());
        timerBusca.setRepeats(false);
    }
    
    private void agendarBusca() {
        timerBusca.restart();
    }
    
    private void buscar() {
        if (fonteDados == null) return;
        
        String termo = campoFiltro.getText().trim();
        
        SwingWorker<List<T>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<T> doInBackground() {
                return fonteDados.apply(termo);
            }
            
            @Override
            protected void done() {
                try {
                    atualizarTabela(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DialogoBusca.this,
                        "Erro na busca: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarTabela(List<T> novosItens) {
        this.itens = novosItens;
        modeloTabela.setRowCount(0);
        
        for (T item : itens) {
            Object[] linha;
            if (conversor != null) {
                linha = conversor.apply(item);
            } else {
                linha = new Object[]{item.toString()};
            }
            modeloTabela.addRow(linha);
        }
    }
    
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0 && linha < itens.size()) {
            itemSelecionado = itens.get(linha);
            
            if (aoSelecionar != null) {
                aoSelecionar.accept(itemSelecionado);
            }
            
            dispose();
        }
    }
    
    /**
     * Define as colunas da tabela.
     */
    public void setColunas(String... colunas) {
        this.colunas = colunas;
        modeloTabela.setColumnIdentifiers(colunas);
    }
    
    /**
     * Define o conversor de item para linha da tabela.
     */
    public void setConversor(Function<T, Object[]> conversor) {
        this.conversor = conversor;
    }
    
    /**
     * Define a fonte de dados (função de busca).
     */
    public void setFonteDados(Function<String, List<T>> fonte) {
        this.fonteDados = fonte;
    }
    
    /**
     * Define ação ao selecionar.
     */
    public void setAoSelecionar(Consumer<T> acao) {
        this.aoSelecionar = acao;
    }
    
    /**
     * Retorna o item selecionado.
     */
    public T getItemSelecionado() {
        return itemSelecionado;
    }
    
    /**
     * Define itens diretamente (sem busca).
     */
    public void setItens(List<T> itens) {
        atualizarTabela(itens);
    }
    
    /**
     * Executa busca inicial ao abrir.
     */
    public void buscarAoAbrir() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                buscar();
            }
        });
    }
}

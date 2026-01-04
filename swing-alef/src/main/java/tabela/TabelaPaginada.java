package tabela;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

/**
 * Tabela com paginação automática.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * TabelaPaginada<Cliente> tabela = new TabelaPaginada<>();
 * tabela.setColunas("ID", "Nome", "Email");
 * tabela.setConversor(c -> new Object[]{c.getId(), c.getNome(), c.getEmail()});
 * tabela.setFonteDados((pagina, tamanhoPagina) -> clienteService.listar(pagina, tamanhoPagina));
 * tabela.setContadorTotal(() -> clienteService.contar());
 * tabela.carregar();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class TabelaPaginada<T> extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JPanel painelPaginacao;
    
    private JButton btnPrimeira, btnAnterior, btnProxima, btnUltima;
    private JLabel lblPagina;
    private JComboBox<Integer> comboTamanhoPagina;
    
    private int paginaAtual = 0;
    private int tamanhoPagina = 20;
    private int totalRegistros = 0;
    
    private String[] colunas = {};
    private Function<T, Object[]> conversor;
    private BiFunction<Integer, Integer, List<T>> fonteDados;
    private Supplier<Integer> contadorTotal;
    
    private List<T> itensAtuais = new ArrayList<>();
    private Consumer<T> aoSelecionar;
    private Consumer<T> aoDuploClique;
    
    public TabelaPaginada() {
        setLayout(new BorderLayout(0, 5));
        
        // Tabela
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setReorderingAllowed(false);
        
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && aoSelecionar != null) {
                T item = getItemSelecionado();
                if (item != null) {
                    aoSelecionar.accept(item);
                }
            }
        });
        
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && aoDuploClique != null) {
                    T item = getItemSelecionado();
                    if (item != null) {
                        aoDuploClique.accept(item);
                    }
                }
            }
        });
        
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        // Paginação
        painelPaginacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnPrimeira = new JButton("⏮");
        btnPrimeira.setToolTipText("Primeira página");
        btnPrimeira.addActionListener(e -> irParaPagina(0));
        
        btnAnterior = new JButton("◀");
        btnAnterior.setToolTipText("Página anterior");
        btnAnterior.addActionListener(e -> paginaAnterior());
        
        lblPagina = new JLabel("Página 1 de 1");
        
        btnProxima = new JButton("▶");
        btnProxima.setToolTipText("Próxima página");
        btnProxima.addActionListener(e -> proximaPagina());
        
        btnUltima = new JButton("⏭");
        btnUltima.setToolTipText("Última página");
        btnUltima.addActionListener(e -> irParaPagina(getTotalPaginas() - 1));
        
        comboTamanhoPagina = new JComboBox<>(new Integer[]{10, 20, 50, 100});
        comboTamanhoPagina.setSelectedItem(tamanhoPagina);
        comboTamanhoPagina.addActionListener(e -> {
            tamanhoPagina = (Integer) comboTamanhoPagina.getSelectedItem();
            paginaAtual = 0;
            carregar();
        });
        
        painelPaginacao.add(btnPrimeira);
        painelPaginacao.add(btnAnterior);
        painelPaginacao.add(lblPagina);
        painelPaginacao.add(btnProxima);
        painelPaginacao.add(btnUltima);
        painelPaginacao.add(Box.createHorizontalStrut(20));
        painelPaginacao.add(new JLabel("Registros por página:"));
        painelPaginacao.add(comboTamanhoPagina);
        
        add(painelPaginacao, BorderLayout.SOUTH);
    }
    
    /**
     * Carrega os dados da página atual.
     */
    public void carregar() {
        if (fonteDados == null) return;
        
        // Atualizar total
        if (contadorTotal != null) {
            totalRegistros = contadorTotal.get();
        }
        
        // Carregar dados
        itensAtuais = fonteDados.apply(paginaAtual, tamanhoPagina);
        
        // Atualizar tabela
        modeloTabela.setRowCount(0);
        for (T item : itensAtuais) {
            Object[] linha = conversor != null ? conversor.apply(item) : new Object[]{item.toString()};
            modeloTabela.addRow(linha);
        }
        
        atualizarControles();
    }
    
    private void atualizarControles() {
        int totalPaginas = getTotalPaginas();
        
        lblPagina.setText(String.format("Página %d de %d (%d registros)", 
            paginaAtual + 1, Math.max(1, totalPaginas), totalRegistros));
        
        btnPrimeira.setEnabled(paginaAtual > 0);
        btnAnterior.setEnabled(paginaAtual > 0);
        btnProxima.setEnabled(paginaAtual < totalPaginas - 1);
        btnUltima.setEnabled(paginaAtual < totalPaginas - 1);
    }
    
    /**
     * Vai para a próxima página.
     */
    public void proximaPagina() {
        if (paginaAtual < getTotalPaginas() - 1) {
            paginaAtual++;
            carregar();
        }
    }
    
    /**
     * Vai para a página anterior.
     */
    public void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregar();
        }
    }
    
    /**
     * Vai para uma página específica.
     */
    public void irParaPagina(int pagina) {
        int totalPaginas = getTotalPaginas();
        paginaAtual = Math.max(0, Math.min(pagina, totalPaginas - 1));
        carregar();
    }
    
    /**
     * Retorna o total de páginas.
     */
    public int getTotalPaginas() {
        return Math.max(1, (int) Math.ceil((double) totalRegistros / tamanhoPagina));
    }
    
    /**
     * Define as colunas da tabela.
     */
    public void setColunas(String... colunas) {
        this.colunas = colunas;
        modeloTabela.setColumnIdentifiers(colunas);
    }
    
    /**
     * Define o conversor de item para linha.
     */
    public void setConversor(Function<T, Object[]> conversor) {
        this.conversor = conversor;
    }
    
    /**
     * Define a fonte de dados paginada.
     */
    public void setFonteDados(BiFunction<Integer, Integer, List<T>> fonte) {
        this.fonteDados = fonte;
    }
    
    /**
     * Define o contador de total de registros.
     */
    public void setContadorTotal(Supplier<Integer> contador) {
        this.contadorTotal = contador;
    }
    
    /**
     * Define ação ao selecionar linha.
     */
    public void setAoSelecionar(Consumer<T> acao) {
        this.aoSelecionar = acao;
    }
    
    /**
     * Define ação ao dar duplo clique.
     */
    public void setAoDuploClique(Consumer<T> acao) {
        this.aoDuploClique = acao;
    }
    
    /**
     * Retorna o item selecionado.
     */
    public T getItemSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0 && linha < itensAtuais.size()) {
            return itensAtuais.get(linha);
        }
        return null;
    }
    
    /**
     * Retorna a tabela interna.
     */
    public JTable getTabela() {
        return tabela;
    }
    
    /**
     * Retorna o modelo da tabela.
     */
    public DefaultTableModel getModelo() {
        return modeloTabela;
    }
    
    /**
     * Define o tamanho da página.
     */
    public void setTamanhoPagina(int tamanho) {
        this.tamanhoPagina = tamanho;
        comboTamanhoPagina.setSelectedItem(tamanho);
    }
    
    /**
     * Recarrega os dados.
     */
    public void recarregar() {
        carregar();
    }
}

package tabela;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

/**
 * Tabela com edição inline de células.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * TabelaEditavel<Produto> tabela = new TabelaEditavel<>();
 * tabela.setColunas("ID", "Nome", "Preço");
 * tabela.setColunasEditaveis(1, 2); // Nome e Preço editáveis
 * tabela.setAoEditar((produto, coluna, valorNovo) -> {
 *     if (coluna == 1) produto.setNome((String) valorNovo);
 *     if (coluna == 2) produto.setPreco((Double) valorNovo);
 * });
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class TabelaEditavel<T> extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    
    private List<T> itens = new ArrayList<>();
    private Function<T, Object[]> conversor;
    private Set<Integer> colunasEditaveis = new HashSet<>();
    
    private TriConsumer<T, Integer, Object> aoEditar;
    private Consumer<T> aoSelecionar;
    
    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
    
    public TabelaEditavel() {
        setLayout(new BorderLayout());
        
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return colunasEditaveis.contains(column);
            }
        };
        
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);
        tabela.putClientProperty("terminateEditOnFocusLost", true);
        
        // Listener para edição
        modeloTabela.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && aoEditar != null) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                
                if (row >= 0 && row < itens.size() && col >= 0) {
                    T item = itens.get(row);
                    Object valor = modeloTabela.getValueAt(row, col);
                    aoEditar.accept(item, col, valor);
                }
            }
        });
        
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && aoSelecionar != null) {
                T item = getItemSelecionado();
                if (item != null) {
                    aoSelecionar.accept(item);
                }
            }
        });
        
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }
    
    /**
     * Define as colunas da tabela.
     */
    public void setColunas(String... colunas) {
        modeloTabela.setColumnIdentifiers(colunas);
    }
    
    /**
     * Define quais colunas são editáveis.
     */
    public void setColunasEditaveis(Integer... colunas) {
        colunasEditaveis.clear();
        colunasEditaveis.addAll(Arrays.asList(colunas));
    }
    
    /**
     * Define o conversor de item para linha.
     */
    public void setConversor(Function<T, Object[]> conversor) {
        this.conversor = conversor;
    }
    
    /**
     * Define os itens da tabela.
     */
    public void setItens(List<T> itens) {
        this.itens = new ArrayList<>(itens);
        modeloTabela.setRowCount(0);
        
        for (T item : itens) {
            Object[] linha = conversor != null ? conversor.apply(item) : new Object[]{item.toString()};
            modeloTabela.addRow(linha);
        }
    }
    
    /**
     * Adiciona um item.
     */
    public void adicionarItem(T item) {
        itens.add(item);
        Object[] linha = conversor != null ? conversor.apply(item) : new Object[]{item.toString()};
        modeloTabela.addRow(linha);
    }
    
    /**
     * Remove o item selecionado.
     */
    public T removerItemSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0 && row < itens.size()) {
            T item = itens.remove(row);
            modeloTabela.removeRow(row);
            return item;
        }
        return null;
    }
    
    /**
     * Remove um item específico.
     */
    public void removerItem(T item) {
        int index = itens.indexOf(item);
        if (index >= 0) {
            itens.remove(index);
            modeloTabela.removeRow(index);
        }
    }
    
    /**
     * Atualiza um item na tabela.
     */
    public void atualizarItem(T item) {
        int index = itens.indexOf(item);
        if (index >= 0 && conversor != null) {
            Object[] linha = conversor.apply(item);
            for (int c = 0; c < linha.length; c++) {
                modeloTabela.setValueAt(linha[c], index, c);
            }
        }
    }
    
    /**
     * Define ação ao editar célula.
     */
    public void setAoEditar(TriConsumer<T, Integer, Object> acao) {
        this.aoEditar = acao;
    }
    
    /**
     * Define ação ao selecionar linha.
     */
    public void setAoSelecionar(Consumer<T> acao) {
        this.aoSelecionar = acao;
    }
    
    /**
     * Retorna o item selecionado.
     */
    public T getItemSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0 && row < itens.size()) {
            return itens.get(row);
        }
        return null;
    }
    
    /**
     * Retorna todos os itens.
     */
    public List<T> getItens() {
        return new ArrayList<>(itens);
    }
    
    /**
     * Retorna a tabela interna.
     */
    public JTable getTabela() {
        return tabela;
    }
    
    /**
     * Limpa a tabela.
     */
    public void limpar() {
        itens.clear();
        modeloTabela.setRowCount(0);
    }
    
    /**
     * Define editor customizado para uma coluna.
     */
    public void setEditorColuna(int coluna, TableCellEditor editor) {
        tabela.getColumnModel().getColumn(coluna).setCellEditor(editor);
    }
    
    /**
     * Define renderizador customizado para uma coluna.
     */
    public void setRenderizadorColuna(int coluna, TableCellRenderer renderer) {
        tabela.getColumnModel().getColumn(coluna).setCellRenderer(renderer);
    }
    
    /**
     * Inicia edição na célula especificada.
     */
    public void editarCelula(int row, int col) {
        tabela.editCellAt(row, col);
        tabela.getEditorComponent().requestFocus();
    }
}

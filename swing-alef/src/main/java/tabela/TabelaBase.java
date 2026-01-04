package tabela;

import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Tabela base com suporte a CRUD e filtragem.
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * TabelaBase tabela = new TabelaBase();
 * tabela.definirColunas(new String[]{"ID", "Nome", "Email"});
 * tabela.adicionarLinha(new Object[]{1, "João", "joao@email.com"});
 * tabela.filtrar("João");
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class TabelaBase extends JTable {
    
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Cria uma tabela base com modelo padrão.
     */
    public TabelaBase() {
        // Não cria modelo nem sorter aqui - deixa para setModel() configurar
        // Isso permite que o GUI Builder configure o modelo primeiro

        // Altura de linhas mais confortável
        setRowHeight(32);
        
        // Adicionar margem nas células
        setIntercellSpacing(new Dimension(8, 4));
        setShowGrid(true);
        // Usa cor do tema para a grid
        setGridColor(UIManager.getColor("Table.gridColor"));
    }
    
    /**
     * Define as colunas da tabela.
     * 
     * @param colunas array com nomes das colunas
     */
    public void definirColunas(String[] colunas) {
        DefaultTableModel modelo = (DefaultTableModel) getModel();
        modelo.setColumnIdentifiers(colunas);
    }
    
    /**
     * Adiciona uma linha à tabela.
     * 
     * @param dados array de dados da linha
     */
    public void adicionarLinha(Object[] dados) {
        DefaultTableModel modelo = (DefaultTableModel) getModel();
        modelo.addRow(dados);
    }
    
    /**
     * Remove uma linha específica.
     * 
     * @param linha índice da linha a remover
     */
    public void removerLinha(int linha) {
        if (linha >= 0 && linha < getRowCount()) {
            DefaultTableModel modelo = (DefaultTableModel) getModel();
            modelo.removeRow(linha);
        }
    }
    
    /**
     * Remove a linha selecionada.
     * 
     * @return true se removeu, false se nenhuma linha selecionada
     */
    public boolean removerLinhaAtual() {
        int linhaSelecionada = getSelectedRow();
        if (linhaSelecionada >= 0) {
            removerLinha(linhaSelecionada);
            return true;
        }
        return false;
    }
    
    /**
     * Limpa todas as linhas da tabela.
     */
    public void limpar() {
        DefaultTableModel modelo = (DefaultTableModel) getModel();
        modelo.setRowCount(0);
    }
    
    /**
     * Obtém um valor específico da tabela.
     * 
     * @param linha índice da linha
     * @param coluna índice da coluna
     * @return valor ou null
     */
    public Object obterValor(int linha, int coluna) {
        if (linha >= 0 && linha < getRowCount() && coluna >= 0 && coluna < getColumnCount()) {
            return getValueAt(linha, coluna);
        }
        return null;
    }
    
    /**
     * Define um valor na tabela.
     * 
     * @param linha índice da linha
     * @param coluna índice da coluna
     * @param valor novo valor
     */
    public void definirValor(int linha, int coluna, Object valor) {
        if (linha >= 0 && linha < getRowCount() && coluna >= 0 && coluna < getColumnCount()) {
            setValueAt(valor, linha, coluna);
        }
    }
    
    /**
     * Obtém os dados da linha selecionada.
     * 
     * @return array de objetos ou null se nenhuma linha selecionada
     */
    public Object[] obterLinhaAtual() {
        int linhaSelecionada = getSelectedRow();
        if (linhaSelecionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) getModel();
            Object[] dados = new Object[modelo.getColumnCount()];
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                dados[i] = modelo.getValueAt(linhaSelecionada, i);
            }
            return dados;
        }
        return null;
    }
    
    /**
     * Retorna o número total de linhas.
     * 
     * @return quantidade de linhas
     */
    public int obterQuantidadeLinhas() {
        return getRowCount();
    }

    /**
     * Aplica um filtro de texto (case-insensitive) nas linhas da tabela.
     * Se texto for vazio ou nulo, o filtro é removido.
     * 
     * @param texto texto para filtrar
     */
    public void filtrar(String texto) {
        // Cria sorter sob demanda se necessário
        if (sorter == null && getModel() instanceof DefaultTableModel) {
            sorter = new TableRowSorter<>((DefaultTableModel) getModel());
            setRowSorter(sorter);
        }
        
        if (sorter == null) return;
        
        if (texto == null || texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        String regex = "(?i)" + java.util.regex.Pattern.quote(texto.trim());
        sorter.setRowFilter(RowFilter.regexFilter(regex));
    }
    
    /**
     * Configura o sorter para ordenação e filtragem.
     * Chamado automaticamente quando necessário.
     */
    public void configurarSorter() {
        if (sorter == null && getModel() instanceof DefaultTableModel) {
            sorter = new TableRowSorter<>((DefaultTableModel) getModel());
            setRowSorter(sorter);
        }
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        // Atualiza cor da grid quando o tema muda
        java.awt.Color gridColor = UIManager.getColor("Table.gridColor");
        if (gridColor != null) {
            setGridColor(gridColor);
        }
    }
}

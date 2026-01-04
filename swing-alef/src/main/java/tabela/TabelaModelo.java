package tabela;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * TableModel genérico que elimina a necessidade de métodos toArray() nas classes de modelo.
 * 
 * <h3>Problema que resolve:</h3>
 * Em vez de cada classe (Cliente, Produto, etc.) precisar implementar um método toArray()
 * para ser exibida em tabelas, este modelo usa funções extratoras (lambdas) para obter
 * os valores de cada coluna.
 * 
 * <h3>Vantagens:</h3>
 * <ul>
 *   <li>Zero acoplamento entre modelo de dados e apresentação</li>
 *   <li>Type-safe com autocompletar do IDE</li>
 *   <li>Flexível - pode mudar ordem das colunas sem alterar a classe modelo</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Classe modelo simples (sem toArray!)
 * public class Cliente {
 *     private int id;
 *     private String nome;
 *     private String email;
 *     // getters...
 * }
 * 
 * // Configuração da tabela
 * TabelaModelo<Cliente> modelo = new TabelaModelo<>();
 * modelo.addColuna("ID", Cliente::getId);
 * modelo.addColuna("Nome", Cliente::getNome);
 * modelo.addColuna("Email", Cliente::getEmail);
 * 
 * // Usar com JTable
 * JTable tabela = new JTable(modelo);
 * 
 * // Adicionar dados
 * modelo.adicionar(new Cliente(1, "João", "joao@email.com"));
 * modelo.adicionarTodos(listaClientes);
 * 
 * // Obter objeto selecionado (não precisa mais de getValueAt!)
 * Cliente selecionado = modelo.getItem(tabela.getSelectedRow());
 * }</pre>
 * 
 * <h3>Integração com GUI Builder:</h3>
 * <pre>{@code
 * public class CadastroCliente extends PainelCrud {
 *     private TabelaModelo<Cliente> modelo;
 *     
 *     public CadastroCliente() {
 *         initComponents(); // GUI Builder cria tblClientes
 *         configurarModelo();
 *         configurarCrud();
 *     }
 *     
 *     private void configurarModelo() {
 *         modelo = new TabelaModelo<>();
 *         modelo.addColuna("ID", Cliente::getId);
 *         modelo.addColuna("Nome", Cliente::getNome);
 *         modelo.addColuna("Email", Cliente::getEmail);
 *         tblClientes.setModel(modelo); // Substitui o modelo do GUI Builder
 *     }
 * }
 * }</pre>
 * 
 * @param <T> Tipo do objeto que será exibido nas linhas da tabela
 * @author alefi
 * @since 2.0
 */
public class TabelaModelo<T> extends AbstractTableModel {
    
    private final List<T> dados = new ArrayList<>();
    private final List<ColunaConfig<T>> colunas = new ArrayList<>();
    
    /**
     * Configuração de uma coluna.
     */
    private static class ColunaConfig<T> {
        final String nome;
        final Function<T, ?> extrator;
        final Class<?> tipo;
        
        ColunaConfig(String nome, Function<T, ?> extrator, Class<?> tipo) {
            this.nome = nome;
            this.extrator = extrator;
            this.tipo = tipo;
        }
    }
    
    /**
     * Cria um modelo de tabela vazio.
     */
    public TabelaModelo() {
    }
    
    // ==================== CONFIGURAÇÃO DE COLUNAS ====================
    
    /**
     * Adiciona uma coluna à tabela.
     * 
     * @param nome Nome da coluna (cabeçalho)
     * @param extrator Função que extrai o valor da coluna do objeto
     * @return this para encadeamento
     * 
     * @example
     * modelo.addColuna("Nome", Cliente::getNome);
     */
    public TabelaModelo<T> addColuna(String nome, Function<T, ?> extrator) {
        return addColuna(nome, extrator, Object.class);
    }
    
    /**
     * Adiciona uma coluna com tipo específico.
     * Útil para renderização especial (ex: Boolean mostra checkbox).
     * 
     * @param nome Nome da coluna
     * @param extrator Função extratora
     * @param tipo Classe do tipo da coluna
     * @return this para encadeamento
     * 
     * @example
     * modelo.addColuna("Ativo", Cliente::isAtivo, Boolean.class);
     */
    public TabelaModelo<T> addColuna(String nome, Function<T, ?> extrator, Class<?> tipo) {
        colunas.add(new ColunaConfig<>(nome, extrator, tipo));
        fireTableStructureChanged();
        return this;
    }
    
    /**
     * Remove todas as colunas configuradas.
     * 
     * @return this para encadeamento
     */
    public TabelaModelo<T> limparColunas() {
        colunas.clear();
        fireTableStructureChanged();
        return this;
    }
    
    // ==================== MANIPULAÇÃO DE DADOS ====================
    
    /**
     * Adiciona um item à tabela.
     * 
     * @param item Objeto a adicionar
     */
    public void adicionar(T item) {
        dados.add(item);
        int linha = dados.size() - 1;
        fireTableRowsInserted(linha, linha);
    }
    
    /**
     * Adiciona vários itens à tabela.
     * 
     * @param itens Lista de objetos a adicionar
     */
    public void adicionarTodos(List<T> itens) {
        if (itens == null || itens.isEmpty()) return;
        int primeiraLinha = dados.size();
        dados.addAll(itens);
        int ultimaLinha = dados.size() - 1;
        fireTableRowsInserted(primeiraLinha, ultimaLinha);
    }
    
    /**
     * Remove um item da tabela.
     * 
     * @param linha Índice da linha a remover
     * @return O item removido
     */
    public T remover(int linha) {
        if (linha < 0 || linha >= dados.size()) return null;
        T removido = dados.remove(linha);
        fireTableRowsDeleted(linha, linha);
        return removido;
    }
    
    /**
     * Remove um item específico da tabela.
     * 
     * @param item Objeto a remover
     * @return true se removido
     */
    public boolean remover(T item) {
        int indice = dados.indexOf(item);
        if (indice >= 0) {
            remover(indice);
            return true;
        }
        return false;
    }
    
    /**
     * Atualiza um item na tabela.
     * 
     * @param linha Índice da linha
     * @param item Novo objeto
     */
    public void atualizar(int linha, T item) {
        if (linha < 0 || linha >= dados.size()) return;
        dados.set(linha, item);
        fireTableRowsUpdated(linha, linha);
    }
    
    /**
     * Limpa todos os dados da tabela.
     */
    public void limpar() {
        int tamanho = dados.size();
        if (tamanho > 0) {
            dados.clear();
            fireTableRowsDeleted(0, tamanho - 1);
        }
    }
    
    /**
     * Substitui todos os dados da tabela.
     * 
     * @param novosItens Nova lista de objetos
     */
    public void setDados(List<T> novosItens) {
        limpar();
        if (novosItens != null) {
            adicionarTodos(novosItens);
        }
    }
    
    // ==================== ACESSO AOS DADOS ====================
    
    /**
     * Obtém o item em uma linha específica.
     * 
     * @param linha Índice da linha
     * @return O objeto na linha ou null se índice inválido
     */
    public T getItem(int linha) {
        if (linha < 0 || linha >= dados.size()) return null;
        return dados.get(linha);
    }
    
    /**
     * Obtém todos os itens.
     * 
     * @return Lista imutável dos itens
     */
    public List<T> getItens() {
        return new ArrayList<>(dados);
    }
    
    /**
     * Encontra o índice de um item.
     * 
     * @param item Objeto a procurar
     * @return Índice ou -1 se não encontrado
     */
    public int indexOf(T item) {
        return dados.indexOf(item);
    }
    
    // ==================== IMPLEMENTAÇÃO AbstractTableModel ====================
    
    @Override
    public int getRowCount() {
        return dados.size();
    }
    
    @Override
    public int getColumnCount() {
        return colunas.size();
    }
    
    @Override
    public String getColumnName(int column) {
        if (column < 0 || column >= colunas.size()) return "";
        return colunas.get(column).nome;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= colunas.size()) return Object.class;
        return colunas.get(columnIndex).tipo;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= dados.size()) return null;
        if (columnIndex < 0 || columnIndex >= colunas.size()) return null;
        
        T item = dados.get(rowIndex);
        Function<T, ?> extrator = colunas.get(columnIndex).extrator;
        return extrator.apply(item);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Por padrão não editável
    }
}

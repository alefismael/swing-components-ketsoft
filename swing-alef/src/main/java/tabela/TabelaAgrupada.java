package tabela;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

/**
 * Tabela com agrupamento de linhas.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * TabelaAgrupada<Venda> tabela = new TabelaAgrupada<>();
 * tabela.setColunas("Data", "Cliente", "Valor");
 * tabela.setConversor(v -> new Object[]{v.getData(), v.getCliente(), v.getValor()});
 * tabela.setChaveAgrupamento(v -> v.getData().getMonth()); // Agrupar por mês
 * tabela.setRotuloGrupo(mes -> "Mês " + mes);
 * tabela.setItens(vendas);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class TabelaAgrupada<T> extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    
    private List<T> itens = new ArrayList<>();
    private Function<T, Object[]> conversor;
    private Function<T, Object> chaveAgrupamento;
    private Function<Object, String> rotuloGrupo;
    
    private Map<Object, Boolean> gruposExpandidos = new HashMap<>();
    private Map<Integer, Object> linhaParaGrupo = new HashMap<>();
    private Map<Integer, T> linhaParaItem = new HashMap<>();
    
    private Color corGrupo = new Color(0xE3F2FD);
    
    public TabelaAgrupada() {
        setLayout(new BorderLayout());
        
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);
        
        // Renderizador customizado para linhas de grupo
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (linhaParaGrupo.containsKey(row)) {
                    c.setBackground(corGrupo);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (!isSelected) {
                    c.setBackground(table.getBackground());
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                
                return c;
            }
        });
        
        // Click para expandir/recolher
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                if (row >= 0 && linhaParaGrupo.containsKey(row)) {
                    Object grupo = linhaParaGrupo.get(row);
                    boolean expandido = gruposExpandidos.getOrDefault(grupo, true);
                    gruposExpandidos.put(grupo, !expandido);
                    atualizarTabela();
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
     * Define o conversor de item para linha.
     */
    public void setConversor(Function<T, Object[]> conversor) {
        this.conversor = conversor;
    }
    
    /**
     * Define a função de agrupamento.
     */
    public void setChaveAgrupamento(Function<T, Object> chave) {
        this.chaveAgrupamento = chave;
    }
    
    /**
     * Define a função de rótulo do grupo.
     */
    public void setRotuloGrupo(Function<Object, String> rotulo) {
        this.rotuloGrupo = rotulo;
    }
    
    /**
     * Define os itens e atualiza a tabela.
     */
    public void setItens(List<T> itens) {
        this.itens = new ArrayList<>(itens);
        atualizarTabela();
    }
    
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        linhaParaGrupo.clear();
        linhaParaItem.clear();
        
        if (chaveAgrupamento == null) {
            // Sem agrupamento
            int row = 0;
            for (T item : itens) {
                Object[] linha = conversor != null ? conversor.apply(item) : new Object[]{item.toString()};
                modeloTabela.addRow(linha);
                linhaParaItem.put(row++, item);
            }
            return;
        }
        
        // Com agrupamento
        Map<Object, List<T>> grupos = new LinkedHashMap<>();
        for (T item : itens) {
            Object chave = chaveAgrupamento.apply(item);
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(item);
        }
        
        int row = 0;
        int numColunas = modeloTabela.getColumnCount();
        
        for (Map.Entry<Object, List<T>> entry : grupos.entrySet()) {
            Object chave = entry.getKey();
            List<T> itensGrupo = entry.getValue();
            
            // Linha do grupo
            String rotulo = rotuloGrupo != null ? rotuloGrupo.apply(chave) : chave.toString();
            boolean expandido = gruposExpandidos.getOrDefault(chave, true);
            String prefixo = expandido ? "▼ " : "▶ ";
            
            Object[] linhaGrupo = new Object[numColunas];
            linhaGrupo[0] = prefixo + rotulo + " (" + itensGrupo.size() + ")";
            modeloTabela.addRow(linhaGrupo);
            linhaParaGrupo.put(row++, chave);
            
            // Itens do grupo
            if (expandido) {
                for (T item : itensGrupo) {
                    Object[] linha = conversor != null ? conversor.apply(item) : new Object[]{item.toString()};
                    // Indentar primeira coluna
                    if (linha.length > 0 && linha[0] != null) {
                        linha[0] = "    " + linha[0];
                    }
                    modeloTabela.addRow(linha);
                    linhaParaItem.put(row++, item);
                }
            }
        }
    }
    
    /**
     * Expande todos os grupos.
     */
    public void expandirTodos() {
        if (chaveAgrupamento != null) {
            for (T item : itens) {
                Object chave = chaveAgrupamento.apply(item);
                gruposExpandidos.put(chave, true);
            }
            atualizarTabela();
        }
    }
    
    /**
     * Recolhe todos os grupos.
     */
    public void recolherTodos() {
        if (chaveAgrupamento != null) {
            for (T item : itens) {
                Object chave = chaveAgrupamento.apply(item);
                gruposExpandidos.put(chave, false);
            }
            atualizarTabela();
        }
    }
    
    /**
     * Retorna o item selecionado (ignora linhas de grupo).
     */
    public T getItemSelecionado() {
        int row = tabela.getSelectedRow();
        return linhaParaItem.get(row);
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
     * Define a cor de fundo das linhas de grupo.
     */
    public void setCorGrupo(Color cor) {
        this.corGrupo = cor;
    }
    
    /**
     * Limpa a tabela.
     */
    public void limpar() {
        itens.clear();
        gruposExpandidos.clear();
        atualizarTabela();
    }
}

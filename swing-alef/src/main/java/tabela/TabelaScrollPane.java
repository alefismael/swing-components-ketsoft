package tabela;

import javax.swing.JScrollPane;

/**
 * ScrollPane com TabelaBase embutida.
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * TabelaScrollPane scrollTabela = new TabelaScrollPane();
 * TabelaBase tabela = scrollTabela.getTabela();
 * tabela.definirColunas(new String[]{"ID", "Nome"});
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class TabelaScrollPane extends JScrollPane {
    
    private final TabelaBase tabela;

    /**
     * Cria um ScrollPane com uma TabelaBase.
     */
    public TabelaScrollPane() {
        tabela = new TabelaBase();
        setViewportView(tabela);
    }
    
    /**
     * Retorna a tabela contida no scroll.
     * 
     * @return TabelaBase
     */
    public TabelaBase getTabela() {
        return tabela;
    }
}

package componente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Layout grid responsivo para cartões de dashboard.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * PainelDashboard dashboard = new PainelDashboard();
 * dashboard.setColunas(4);
 * dashboard.adicionarCartao(new CartaoDashboard("Vendas", "1000", "💰"));
 * dashboard.adicionarCartao(new CartaoKPI("Clientes", "500", "+5%"));
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class PainelDashboard extends JPanel {
    
    private int colunas = 4;
    private int espacamento = 15;
    private List<JComponent> cartoes = new ArrayList<>();
    
    public PainelDashboard() {
        setLayout(new GridLayout(0, colunas, espacamento, espacamento));
        setBorder(BorderFactory.createEmptyBorder(espacamento, espacamento, espacamento, espacamento));
    }
    
    /**
     * Adiciona um cartão ao dashboard.
     */
    public void adicionarCartao(JComponent cartao) {
        cartoes.add(cartao);
        add(cartao);
        revalidate();
        repaint();
    }
    
    /**
     * Remove um cartão do dashboard.
     */
    public void removerCartao(JComponent cartao) {
        cartoes.remove(cartao);
        remove(cartao);
        revalidate();
        repaint();
    }
    
    /**
     * Remove todos os cartões.
     */
    public void limpar() {
        cartoes.clear();
        removeAll();
        revalidate();
        repaint();
    }
    
    /**
     * Define o número de colunas.
     */
    public void setColunas(int colunas) {
        this.colunas = colunas;
        setLayout(new GridLayout(0, colunas, espacamento, espacamento));
        revalidate();
    }
    
    /**
     * Define o espaçamento entre cartões.
     */
    public void setEspacamento(int espacamento) {
        this.espacamento = espacamento;
        setLayout(new GridLayout(0, colunas, espacamento, espacamento));
        setBorder(BorderFactory.createEmptyBorder(espacamento, espacamento, espacamento, espacamento));
        revalidate();
    }
    
    public int getColunas() {
        return colunas;
    }
    
    public int getQuantidadeCartoes() {
        return cartoes.size();
    }
    
    /**
     * Retorna o cartão no índice especificado.
     */
    public JComponent getCartao(int indice) {
        return cartoes.get(indice);
    }
}

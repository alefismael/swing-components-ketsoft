package componente;

import javax.swing.*;
import java.awt.*;

/**
 * Cartão de KPI com indicador de tendência (↑↓).
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * CartaoKPI kpi = new CartaoKPI("Vendas", "1.234", "+12%");
 * kpi.setTendencia(CartaoKPI.TENDENCIA_ALTA);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class CartaoKPI extends JPanel {
    
    public static final int TENDENCIA_ALTA = 1;
    public static final int TENDENCIA_BAIXA = -1;
    public static final int TENDENCIA_ESTAVEL = 0;
    
    private String titulo;
    private String valor;
    private String variacao;
    private int tendencia = TENDENCIA_ESTAVEL;
    
    private Color corAlta = new Color(0x4CAF50);
    private Color corBaixa = new Color(0xF44336);
    private Color corEstavel = new Color(0x9E9E9E);
    private int arredondamento = 12;
    
    public CartaoKPI() {
        this("KPI", "0", "0%");
    }
    
    public CartaoKPI(String titulo, String valor, String variacao) {
        this.titulo = titulo;
        this.valor = valor;
        this.variacao = variacao;
        
        setOpaque(false);
        setPreferredSize(new Dimension(180, 100));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Fundo com borda
        g2d.setColor(UIManager.getColor("Panel.background"));
        g2d.fillRoundRect(0, 0, w, h, arredondamento, arredondamento);
        
        g2d.setColor(UIManager.getColor("Component.borderColor"));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(0, 0, w - 1, h - 1, arredondamento, arredondamento);
        
        // Título
        g2d.setColor(UIManager.getColor("Label.disabledForeground"));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2d.drawString(titulo, 15, 25);
        
        // Valor
        g2d.setColor(UIManager.getColor("Label.foreground"));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g2d.drawString(valor, 15, 55);
        
        // Variação com indicador
        Color corVariacao = tendencia == TENDENCIA_ALTA ? corAlta :
                           tendencia == TENDENCIA_BAIXA ? corBaixa : corEstavel;
        g2d.setColor(corVariacao);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        String seta = tendencia == TENDENCIA_ALTA ? "↑ " :
                     tendencia == TENDENCIA_BAIXA ? "↓ " : "→ ";
        g2d.drawString(seta + variacao, 15, h - 15);
        
        g2d.dispose();
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }
    
    public void setValor(String valor) {
        this.valor = valor;
        repaint();
    }
    
    public void setVariacao(String variacao) {
        this.variacao = variacao;
        repaint();
    }
    
    public void setTendencia(int tendencia) {
        this.tendencia = tendencia;
        repaint();
    }
    
    /**
     * Define a variação com detecção automática de tendência.
     */
    public void setVariacaoAutomatica(double percentual) {
        this.variacao = String.format("%.1f%%", Math.abs(percentual));
        this.tendencia = percentual > 0 ? TENDENCIA_ALTA : 
                        percentual < 0 ? TENDENCIA_BAIXA : TENDENCIA_ESTAVEL;
        repaint();
    }
}

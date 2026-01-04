package componente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Cartão de estatística com mini-gráfico sparkline.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * CartaoEstatistica stats = new CartaoEstatistica("Vendas Semanais", "R$ 5.200");
 * stats.setDados(Arrays.asList(100.0, 150.0, 120.0, 180.0, 200.0, 170.0, 220.0));
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class CartaoEstatistica extends JPanel {
    
    private String titulo;
    private String valor;
    private List<Double> dados;
    
    private Color corGrafico = new Color(0x2196F3);
    private Color corGraficoFundo = new Color(0x2196F3, true);
    private int arredondamento = 12;
    
    public CartaoEstatistica() {
        this("Estatística", "0");
    }
    
    public CartaoEstatistica(String titulo, String valor) {
        this.titulo = titulo;
        this.valor = valor;
        
        setOpaque(false);
        setPreferredSize(new Dimension(200, 120));
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
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g2d.drawString(titulo, 15, 22);
        
        // Valor
        g2d.setColor(UIManager.getColor("Label.foreground"));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2d.drawString(valor, 15, 48);
        
        // Mini gráfico (sparkline)
        if (dados != null && dados.size() > 1) {
            desenharSparkline(g2d, 15, 60, w - 30, h - 75);
        }
        
        g2d.dispose();
    }
    
    private void desenharSparkline(Graphics2D g2d, int x, int y, int w, int h) {
        if (dados.isEmpty()) return;
        
        double min = dados.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = dados.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double range = max - min;
        if (range == 0) range = 1;
        
        int n = dados.size();
        int[] pontosx = new int[n];
        int[] pontosy = new int[n];
        
        for (int i = 0; i < n; i++) {
            pontosx[i] = x + (i * w / (n - 1));
            pontosy[i] = y + h - (int) ((dados.get(i) - min) / range * h);
        }
        
        // Área preenchida
        int[] areax = new int[n + 2];
        int[] areay = new int[n + 2];
        System.arraycopy(pontosx, 0, areax, 0, n);
        System.arraycopy(pontosy, 0, areay, 0, n);
        areax[n] = x + w;
        areay[n] = y + h;
        areax[n + 1] = x;
        areay[n + 1] = y + h;
        
        g2d.setColor(new Color(corGrafico.getRed(), corGrafico.getGreen(), corGrafico.getBlue(), 40));
        g2d.fillPolygon(areax, areay, n + 2);
        
        // Linha
        g2d.setColor(corGrafico);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawPolyline(pontosx, pontosy, n);
        
        // Ponto final
        g2d.fillOval(pontosx[n - 1] - 3, pontosy[n - 1] - 3, 6, 6);
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }
    
    public void setValor(String valor) {
        this.valor = valor;
        repaint();
    }
    
    public void setDados(List<Double> dados) {
        this.dados = dados;
        repaint();
    }
    
    public void setCorGrafico(Color cor) {
        this.corGrafico = cor;
        repaint();
    }
}

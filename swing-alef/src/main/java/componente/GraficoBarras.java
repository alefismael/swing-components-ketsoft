package componente;

import javax.swing.UIManager;
import java.awt.*;
import java.util.List;

/**
 * Gráfico de barras simples.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * GraficoBarras grafico = new GraficoBarras();
 * grafico.setTitulo("Vendas por Mês");
 * grafico.setDados(
 *     Arrays.asList("Jan", "Fev", "Mar", "Abr"),
 *     Arrays.asList(100.0, 150.0, 120.0, 180.0)
 * );
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class GraficoBarras extends PainelGrafico {
    
    private boolean barrasHorizontais = false;
    private int espacamentoBarras = 10;
    
    public GraficoBarras() {
        super();
    }
    
    @Override
    protected void desenharGrafico(Graphics2D g2d) {
        if (valores.isEmpty()) return;
        
        int w = getWidth() - margemEsquerda - margemDireita;
        int h = getHeight() - margemSuperior - margemInferior;
        
        double max = getValorMaximo();
        if (max == 0) max = 1;
        
        // Grade
        if (mostrarGrade) {
            desenharGrade(g2d, w, h, max);
        }
        
        // Barras
        int n = valores.size();
        int larguraBarra = (w - espacamentoBarras * (n + 1)) / n;
        
        for (int i = 0; i < n; i++) {
            int x = margemEsquerda + espacamentoBarras + i * (larguraBarra + espacamentoBarras);
            int alturaBarra = (int) (valores.get(i) / max * h);
            int y = margemSuperior + h - alturaBarra;
            
            // Barra
            g2d.setColor(cores.get(i % cores.size()));
            g2d.fillRoundRect(x, y, larguraBarra, alturaBarra, 4, 4);
            
            // Valor
            if (mostrarValores) {
                g2d.setColor(UIManager.getColor("Label.foreground"));
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                String valorStr = formatarValor(valores.get(i));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (larguraBarra - fm.stringWidth(valorStr)) / 2;
                g2d.drawString(valorStr, textX, y - 5);
            }
            
            // Rótulo
            g2d.setColor(UIManager.getColor("Label.foreground"));
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            if (i < rotulos.size()) {
                String rotulo = rotulos.get(i);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (larguraBarra - fm.stringWidth(rotulo)) / 2;
                g2d.drawString(rotulo, textX, getHeight() - margemInferior + 15);
            }
        }
    }
    
    private void desenharGrade(Graphics2D g2d, int w, int h, double max) {
        g2d.setColor(UIManager.getColor("Component.borderColor"));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
        
        int linhas = 5;
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        
        for (int i = 0; i <= linhas; i++) {
            int y = margemSuperior + i * h / linhas;
            
            // Linha
            g2d.drawLine(margemEsquerda, y, margemEsquerda + w, y);
            
            // Valor do eixo
            double valor = max * (linhas - i) / linhas;
            String valorStr = formatarValor(valor);
            g2d.setColor(UIManager.getColor("Label.disabledForeground"));
            g2d.drawString(valorStr, 5, y + 4);
            g2d.setColor(UIManager.getColor("Component.borderColor"));
        }
    }
    
    private String formatarValor(double valor) {
        if (valor == (long) valor) {
            return String.valueOf((long) valor);
        }
        return String.format("%.1f", valor);
    }
    
    public void setBarrasHorizontais(boolean horizontal) {
        this.barrasHorizontais = horizontal;
        repaint();
    }
    
    public void setEspacamentoBarras(int espacamento) {
        this.espacamentoBarras = espacamento;
        repaint();
    }
}

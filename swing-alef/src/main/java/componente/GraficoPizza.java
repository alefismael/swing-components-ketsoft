package componente;

import javax.swing.UIManager;
import java.awt.*;
import java.util.List;

/**
 * Gráfico de pizza (pie chart).
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * GraficoPizza grafico = new GraficoPizza();
 * grafico.setTitulo("Vendas por Região");
 * grafico.setDados(
 *     Arrays.asList("Norte", "Sul", "Leste", "Oeste"),
 *     Arrays.asList(25.0, 35.0, 20.0, 20.0)
 * );
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class GraficoPizza extends PainelGrafico {
    
    private boolean mostrarPercentual = true;
    private boolean efeito3D = false;
    private boolean rosca = false; // donut chart
    
    public GraficoPizza() {
        super();
        mostrarGrade = false;
    }
    
    @Override
    protected void desenharGrafico(Graphics2D g2d) {
        if (valores.isEmpty()) return;
        
        double total = valores.stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) return;
        
        int centroX = margemEsquerda + (getWidth() - margemEsquerda - margemDireita - 120) / 2;
        int centroY = margemSuperior + (getHeight() - margemSuperior - margemInferior) / 2;
        int raio = Math.min(getWidth() - margemEsquerda - margemDireita - 140, 
                          getHeight() - margemSuperior - margemInferior) / 2 - 10;
        
        // Efeito 3D (sombra)
        if (efeito3D) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(centroX - raio + 5, centroY - raio + 10, raio * 2, raio * 2);
        }
        
        // Desenhar fatias
        double anguloInicio = 0;
        
        for (int i = 0; i < valores.size(); i++) {
            double percentual = valores.get(i) / total;
            double angulo = percentual * 360;
            
            // Fatia
            g2d.setColor(cores.get(i % cores.size()));
            g2d.fillArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 
                       (int) anguloInicio, (int) Math.ceil(angulo));
            
            // Borda
            g2d.setColor(UIManager.getColor("Panel.background"));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 
                       (int) anguloInicio, (int) Math.ceil(angulo));
            
            // Percentual
            if (mostrarPercentual && percentual > 0.05) {
                double anguloMeio = Math.toRadians(anguloInicio + angulo / 2);
                int textoX = (int) (centroX + Math.cos(anguloMeio) * raio * 0.6);
                int textoY = (int) (centroY - Math.sin(anguloMeio) * raio * 0.6);
                
                String texto = String.format("%.0f%%", percentual * 100);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(texto, textoX - fm.stringWidth(texto) / 2, textoY + fm.getAscent() / 2);
            }
            
            anguloInicio += angulo;
        }
        
        // Rosca (donut) - buraco no centro
        if (rosca) {
            int raioInterno = raio / 2;
            g2d.setColor(UIManager.getColor("Panel.background"));
            g2d.fillOval(centroX - raioInterno, centroY - raioInterno, raioInterno * 2, raioInterno * 2);
        }
    }
    
    @Override
    protected void desenharLegenda(Graphics2D g2d) {
        int x = getWidth() - 130;
        int y = margemSuperior + 20;
        
        double total = valores.stream().mapToDouble(Double::doubleValue).sum();
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        for (int i = 0; i < rotulos.size() && i < cores.size(); i++) {
            // Quadrado de cor
            g2d.setColor(cores.get(i));
            g2d.fillRect(x, y + i * 20, 12, 12);
            
            // Texto
            g2d.setColor(UIManager.getColor("Label.foreground"));
            String texto = rotulos.get(i);
            if (total > 0 && i < valores.size()) {
                texto += String.format(" (%.0f%%)", valores.get(i) / total * 100);
            }
            g2d.drawString(texto, x + 18, y + i * 20 + 10);
        }
    }
    
    public void setMostrarPercentual(boolean mostrar) {
        this.mostrarPercentual = mostrar;
        repaint();
    }
    
    public void setEfeito3D(boolean efeito) {
        this.efeito3D = efeito;
        repaint();
    }
    
    public void setRosca(boolean rosca) {
        this.rosca = rosca;
        repaint();
    }
}

package componente;

import javax.swing.*;
import java.awt.*;

/**
 * Indicador visual de progresso para assistentes.
 * 
 * @author alefi
 * @since 1.1
 */
public class IndicadorEtapas extends JPanel {
    
    private String[] etapas = {};
    private int etapaAtual = 0;
    
    private Color corAtiva = new Color(0x2196F3);
    private Color corConcluida = new Color(0x4CAF50);
    private Color corPendente = new Color(0xBDBDBD);
    private Color corTexto = Color.WHITE;
    
    public IndicadorEtapas() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 60));
    }
    
    public void setEtapas(String[] etapas) {
        this.etapas = etapas;
        repaint();
    }
    
    public void setEtapaAtual(int etapa) {
        this.etapaAtual = etapa;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (etapas.length == 0) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int largura = getWidth();
        int altura = getHeight();
        int raio = 20;
        int espacamento = (largura - 40) / Math.max(1, etapas.length - 1);
        int y = altura / 2;
        
        // Desenhar linhas de conexão
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < etapas.length - 1; i++) {
            int x1 = 20 + i * espacamento;
            int x2 = 20 + (i + 1) * espacamento;
            
            if (i < etapaAtual) {
                g2d.setColor(corConcluida);
            } else {
                g2d.setColor(corPendente);
            }
            
            g2d.drawLine(x1 + raio, y, x2 - raio, y);
        }
        
        // Desenhar círculos e números
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        for (int i = 0; i < etapas.length; i++) {
            int x = 20 + i * espacamento;
            
            Color corCirculo;
            if (i < etapaAtual) {
                corCirculo = corConcluida;
            } else if (i == etapaAtual) {
                corCirculo = corAtiva;
            } else {
                corCirculo = corPendente;
            }
            
            // Círculo
            g2d.setColor(corCirculo);
            g2d.fillOval(x - raio, y - raio, raio * 2, raio * 2);
            
            // Número ou check
            g2d.setColor(corTexto);
            String texto;
            if (i < etapaAtual) {
                texto = "✓";
            } else {
                texto = String.valueOf(i + 1);
            }
            
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x - fm.stringWidth(texto) / 2;
            int textY = y + fm.getAscent() / 2 - 2;
            g2d.drawString(texto, textX, textY);
            
            // Título da etapa
            g2d.setColor(i == etapaAtual ? corAtiva : UIManager.getColor("Label.foreground"));
            g2d.setFont(new Font(Font.SANS_SERIF, i == etapaAtual ? Font.BOLD : Font.PLAIN, 11));
            fm = g2d.getFontMetrics();
            
            String titulo = etapas[i];
            if (fm.stringWidth(titulo) > espacamento - 10 && espacamento > 0) {
                titulo = titulo.substring(0, Math.min(titulo.length(), 10)) + "...";
            }
            
            textX = x - fm.stringWidth(titulo) / 2;
            g2d.drawString(titulo, textX, y + raio + 15);
        }
        
        g2d.dispose();
    }
    
    public void setCorAtiva(Color cor) {
        this.corAtiva = cor;
        repaint();
    }
    
    public void setCorConcluida(Color cor) {
        this.corConcluida = cor;
        repaint();
    }
}

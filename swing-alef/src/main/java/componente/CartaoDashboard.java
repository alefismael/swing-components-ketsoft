package componente;

import javax.swing.*;
import java.awt.*;

/**
 * Cartão para dashboard com ícone, título e valor.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * CartaoDashboard cartao = new CartaoDashboard("Vendas", "R$ 15.000", "💰");
 * cartao.setCorFundo(new Color(0x4CAF50));
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class CartaoDashboard extends JPanel {
    
    private String titulo;
    private String valor;
    private String icone;
    private String subtitulo;
    
    private Color corFundo = new Color(0x2196F3);
    private Color corTexto = Color.WHITE;
    private int arredondamento = 15;
    
    public CartaoDashboard() {
        this("Título", "0", "📊");
    }
    
    public CartaoDashboard(String titulo, String valor, String icone) {
        this.titulo = titulo;
        this.valor = valor;
        this.icone = icone;
        
        setOpaque(false);
        setPreferredSize(new Dimension(200, 120));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Fundo
        g2d.setColor(corFundo);
        g2d.fillRoundRect(0, 0, w, h, arredondamento, arredondamento);
        
        // Ícone (canto superior direito)
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
        g2d.setColor(new Color(255, 255, 255, 50));
        FontMetrics fmIcone = g2d.getFontMetrics();
        g2d.drawString(icone, w - fmIcone.stringWidth(icone) - 15, 45);
        
        // Título
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g2d.drawString(titulo, 15, 30);
        
        // Valor
        g2d.setColor(corTexto);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g2d.drawString(valor, 15, 65);
        
        // Subtítulo
        if (subtitulo != null && !subtitulo.isEmpty()) {
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            g2d.drawString(subtitulo, 15, h - 15);
        }
        
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
    
    public void setIcone(String icone) {
        this.icone = icone;
        repaint();
    }
    
    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
        repaint();
    }
    
    public void setCorFundo(Color cor) {
        this.corFundo = cor;
        repaint();
    }
    
    public void setCorTexto(Color cor) {
        this.corTexto = cor;
        repaint();
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getValor() {
        return valor;
    }
}

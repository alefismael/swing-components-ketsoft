package componente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel base para gráficos com funcionalidades comuns.
 * 
 * @author alefi
 * @since 1.1
 */
public abstract class PainelGrafico extends JPanel {
    
    protected String titulo = "";
    protected List<String> rotulos = new ArrayList<>();
    protected List<Double> valores = new ArrayList<>();
    protected List<Color> cores = new ArrayList<>();
    
    protected boolean mostrarLegenda = true;
    protected boolean mostrarValores = true;
    protected boolean mostrarGrade = true;
    
    protected int margemEsquerda = 60;
    protected int margemDireita = 20;
    protected int margemSuperior = 40;
    protected int margemInferior = 40;
    
    protected static final Color[] PALETA_CORES = {
        new Color(0x2196F3), // Azul
        new Color(0x4CAF50), // Verde
        new Color(0xFFC107), // Amarelo
        new Color(0xF44336), // Vermelho
        new Color(0x9C27B0), // Roxo
        new Color(0xFF5722), // Laranja
        new Color(0x00BCD4), // Ciano
        new Color(0x795548), // Marrom
        new Color(0x607D8B), // Cinza
        new Color(0xE91E63)  // Rosa
    };
    
    public PainelGrafico() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 300));
    }
    
    /**
     * Define os dados do gráfico.
     */
    public void setDados(List<String> rotulos, List<Double> valores) {
        this.rotulos = new ArrayList<>(rotulos);
        this.valores = new ArrayList<>(valores);
        
        // Gerar cores automaticamente se necessário
        if (cores.size() < valores.size()) {
            cores.clear();
            for (int i = 0; i < valores.size(); i++) {
                cores.add(PALETA_CORES[i % PALETA_CORES.length]);
            }
        }
        
        repaint();
    }
    
    /**
     * Define cores personalizadas.
     */
    public void setCores(List<Color> cores) {
        this.cores = new ArrayList<>(cores);
        repaint();
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }
    
    public void setMostrarLegenda(boolean mostrar) {
        this.mostrarLegenda = mostrar;
        repaint();
    }
    
    public void setMostrarValores(boolean mostrar) {
        this.mostrarValores = mostrar;
        repaint();
    }
    
    public void setMostrarGrade(boolean mostrar) {
        this.mostrarGrade = mostrar;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Título
        if (titulo != null && !titulo.isEmpty()) {
            g2d.setColor(UIManager.getColor("Label.foreground"));
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(titulo)) / 2;
            g2d.drawString(titulo, x, 25);
        }
        
        // Desenhar gráfico específico
        desenharGrafico(g2d);
        
        // Legenda
        if (mostrarLegenda && !rotulos.isEmpty()) {
            desenharLegenda(g2d);
        }
        
        g2d.dispose();
    }
    
    /**
     * Método abstrato para desenhar o gráfico específico.
     */
    protected abstract void desenharGrafico(Graphics2D g2d);
    
    /**
     * Desenha a legenda do gráfico.
     */
    protected void desenharLegenda(Graphics2D g2d) {
        int x = getWidth() - margemDireita - 100;
        int y = margemSuperior + 10;
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        for (int i = 0; i < rotulos.size() && i < cores.size(); i++) {
            // Quadrado de cor
            g2d.setColor(cores.get(i));
            g2d.fillRect(x, y + i * 18, 12, 12);
            
            // Texto
            g2d.setColor(UIManager.getColor("Label.foreground"));
            g2d.drawString(rotulos.get(i), x + 18, y + i * 18 + 10);
        }
    }
    
    /**
     * Retorna o valor máximo dos dados.
     */
    protected double getValorMaximo() {
        return valores.stream().mapToDouble(Double::doubleValue).max().orElse(100);
    }
    
    /**
     * Retorna o valor mínimo dos dados.
     */
    protected double getValorMinimo() {
        return valores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }
}

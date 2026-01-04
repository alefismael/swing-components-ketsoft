package componente;

import javax.swing.UIManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico de linhas com múltiplas séries.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * GraficoLinhas grafico = new GraficoLinhas();
 * grafico.setTitulo("Evolução Mensal");
 * grafico.adicionarSerie("Vendas", Arrays.asList(100.0, 150.0, 120.0, 180.0));
 * grafico.adicionarSerie("Custos", Arrays.asList(80.0, 90.0, 85.0, 95.0));
 * grafico.setRotulos(Arrays.asList("Jan", "Fev", "Mar", "Abr"));
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class GraficoLinhas extends PainelGrafico {
    
    private List<List<Double>> series = new ArrayList<>();
    private List<String> nomesSeries = new ArrayList<>();
    private boolean mostrarPontos = true;
    private boolean preencherArea = false;
    
    public GraficoLinhas() {
        super();
    }
    
    /**
     * Adiciona uma série de dados.
     */
    public void adicionarSerie(String nome, List<Double> dados) {
        nomesSeries.add(nome);
        series.add(new ArrayList<>(dados));
        
        // Adicionar cor se necessário
        if (cores.size() < series.size()) {
            cores.add(PALETA_CORES[(series.size() - 1) % PALETA_CORES.length]);
        }
        
        repaint();
    }
    
    /**
     * Define os rótulos do eixo X.
     */
    public void setRotulos(List<String> rotulos) {
        this.rotulos = new ArrayList<>(rotulos);
        repaint();
    }
    
    /**
     * Limpa todas as séries.
     */
    public void limparSeries() {
        series.clear();
        nomesSeries.clear();
        repaint();
    }
    
    @Override
    protected void desenharGrafico(Graphics2D g2d) {
        if (series.isEmpty()) return;
        
        int w = getWidth() - margemEsquerda - margemDireita;
        int h = getHeight() - margemSuperior - margemInferior;
        
        double max = getMaximoGlobal();
        double min = 0;
        if (max == 0) max = 1;
        
        // Grade
        if (mostrarGrade) {
            desenharGrade(g2d, w, h, max, min);
        }
        
        // Séries
        for (int s = 0; s < series.size(); s++) {
            List<Double> dados = series.get(s);
            if (dados.isEmpty()) continue;
            
            int n = dados.size();
            int[] pontosx = new int[n];
            int[] pontosy = new int[n];
            
            for (int i = 0; i < n; i++) {
                pontosx[i] = margemEsquerda + (n > 1 ? i * w / (n - 1) : w / 2);
                pontosy[i] = margemSuperior + h - (int) ((dados.get(i) - min) / (max - min) * h);
            }
            
            Color cor = cores.get(s % cores.size());
            
            // Área preenchida
            if (preencherArea) {
                int[] areax = new int[n + 2];
                int[] areay = new int[n + 2];
                System.arraycopy(pontosx, 0, areax, 0, n);
                System.arraycopy(pontosy, 0, areay, 0, n);
                areax[n] = margemEsquerda + w;
                areay[n] = margemSuperior + h;
                areax[n + 1] = margemEsquerda;
                areay[n + 1] = margemSuperior + h;
                
                g2d.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40));
                g2d.fillPolygon(areax, areay, n + 2);
            }
            
            // Linha
            g2d.setColor(cor);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawPolyline(pontosx, pontosy, n);
            
            // Pontos
            if (mostrarPontos) {
                for (int i = 0; i < n; i++) {
                    g2d.fillOval(pontosx[i] - 4, pontosy[i] - 4, 8, 8);
                }
            }
        }
        
        // Rótulos do eixo X
        g2d.setColor(UIManager.getColor("Label.foreground"));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        int n = Math.max(rotulos.size(), series.isEmpty() ? 0 : series.get(0).size());
        for (int i = 0; i < rotulos.size() && i < n; i++) {
            int x = margemEsquerda + (n > 1 ? i * w / (n - 1) : w / 2);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x - fm.stringWidth(rotulos.get(i)) / 2;
            g2d.drawString(rotulos.get(i), textX, getHeight() - margemInferior + 15);
        }
    }
    
    private void desenharGrade(Graphics2D g2d, int w, int h, double max, double min) {
        g2d.setColor(UIManager.getColor("Component.borderColor"));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
        
        int linhas = 5;
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        
        for (int i = 0; i <= linhas; i++) {
            int y = margemSuperior + i * h / linhas;
            g2d.drawLine(margemEsquerda, y, margemEsquerda + w, y);
            
            double valor = max - (max - min) * i / linhas;
            String valorStr = formatarValor(valor);
            g2d.setColor(UIManager.getColor("Label.disabledForeground"));
            g2d.drawString(valorStr, 5, y + 4);
            g2d.setColor(UIManager.getColor("Component.borderColor"));
        }
    }
    
    private double getMaximoGlobal() {
        return series.stream()
            .flatMap(List::stream)
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(100);
    }
    
    private String formatarValor(double valor) {
        if (valor == (long) valor) {
            return String.valueOf((long) valor);
        }
        return String.format("%.1f", valor);
    }
    
    @Override
    protected void desenharLegenda(Graphics2D g2d) {
        int x = getWidth() - margemDireita - 100;
        int y = margemSuperior + 10;
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        for (int i = 0; i < nomesSeries.size() && i < cores.size(); i++) {
            g2d.setColor(cores.get(i));
            g2d.fillRect(x, y + i * 18, 12, 12);
            
            g2d.setColor(UIManager.getColor("Label.foreground"));
            g2d.drawString(nomesSeries.get(i), x + 18, y + i * 18 + 10);
        }
    }
    
    public void setMostrarPontos(boolean mostrar) {
        this.mostrarPontos = mostrar;
        repaint();
    }
    
    public void setPreencherArea(boolean preencher) {
        this.preencherArea = preencher;
        repaint();
    }
}

package componente;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.print.PrinterJob;

/**
 * Visualizador de relatórios com preview de impressão.
 * 
 * @author alefi
 * @since 1.1
 */
public class VisualizadorRelatorio extends JPanel {
    
    private ConstrutorRelatorio relatorio;
    private JScrollPane scrollPane;
    private JPanel painelConteudo;
    private double zoom = 1.0;
    
    public VisualizadorRelatorio(ConstrutorRelatorio relatorio) {
        this.relatorio = relatorio;
        setLayout(new BorderLayout());
        
        // Barra de ferramentas
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton btnImprimir = new JButton("🖨️ Imprimir");
        btnImprimir.addActionListener(e -> relatorio.imprimir());
        
        JButton btnZoomMenos = new JButton("−");
        btnZoomMenos.addActionListener(e -> ajustarZoom(-0.1));
        
        JLabel lblZoom = new JLabel("100%");
        
        JButton btnZoomMais = new JButton("+");
        btnZoomMais.addActionListener(e -> ajustarZoom(0.1));
        
        toolbar.add(btnImprimir);
        toolbar.addSeparator();
        toolbar.add(btnZoomMenos);
        toolbar.add(lblZoom);
        toolbar.add(btnZoomMais);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Conteúdo do relatório
        painelConteudo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharRelatorio((Graphics2D) g);
            }
        };
        painelConteudo.setBackground(Color.WHITE);
        painelConteudo.setPreferredSize(new Dimension(595, calcularAltura())); // A4 em pixels (72dpi)
        
        scrollPane = new JScrollPane(painelConteudo);
        scrollPane.getViewport().setBackground(new Color(0x808080));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private int calcularAltura() {
        int altura = 150; // Título + subtítulo + margens
        
        for (ConstrutorRelatorio.ElementoRelatorio elemento : relatorio.getElementos()) {
            switch (elemento.getTipo()) {
                case TEXTO:
                    altura += 20;
                    break;
                case SECAO:
                    altura += 50;
                    break;
                case TABELA:
                    TableModel modelo = (TableModel) elemento.getDados();
                    altura += 25 + modelo.getRowCount() * 20;
                    break;
                case SEPARADOR:
                    altura += 15;
                    break;
                case ESPACO:
                    altura += (Integer) elemento.getDados();
                    break;
            }
        }
        
        return Math.max(altura, 842); // Mínimo A4
    }
    
    private void desenharRelatorio(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int margemX = 50;
        int margemY = 50;
        int largura = painelConteudo.getWidth() - margemX * 2;
        int y = margemY;
        
        // Cabeçalho
        if (!relatorio.getCabecalho().isEmpty()) {
            g2d.setFont(relatorio.getFonteCabecalho());
            g2d.setColor(Color.GRAY);
            g2d.drawString(relatorio.getCabecalho(), margemX, y);
            y += 20;
        }
        
        // Título
        if (!relatorio.getTitulo().isEmpty()) {
            g2d.setFont(relatorio.getFonteTitulo());
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (painelConteudo.getWidth() - fm.stringWidth(relatorio.getTitulo())) / 2;
            g2d.drawString(relatorio.getTitulo(), x, y + fm.getAscent());
            y += fm.getHeight() + 5;
        }
        
        // Subtítulo
        if (!relatorio.getSubtitulo().isEmpty()) {
            g2d.setFont(relatorio.getFonteSubtitulo());
            g2d.setColor(Color.DARK_GRAY);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (painelConteudo.getWidth() - fm.stringWidth(relatorio.getSubtitulo())) / 2;
            g2d.drawString(relatorio.getSubtitulo(), x, y + fm.getAscent());
            y += fm.getHeight() + 20;
        }
        
        // Elementos
        for (ConstrutorRelatorio.ElementoRelatorio elemento : relatorio.getElementos()) {
            switch (elemento.getTipo()) {
                case TEXTO:
                    g2d.setFont(relatorio.getFonteConteudo());
                    g2d.setColor(Color.BLACK);
                    g2d.drawString((String) elemento.getDados(), margemX, y + 15);
                    y += 20;
                    break;
                    
                case SECAO:
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString((String) elemento.getDados(), margemX, y + 18);
                    y += 22;
                    
                    g2d.setFont(relatorio.getFonteConteudo());
                    g2d.drawString((String) elemento.getDados2(), margemX + 10, y + 15);
                    y += 25;
                    break;
                    
                case TABELA:
                    y = desenharTabela(g2d, (TableModel) elemento.getDados(), margemX, y, largura);
                    break;
                    
                case SEPARADOR:
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawLine(margemX, y + 7, margemX + largura, y + 7);
                    y += 15;
                    break;
                    
                case ESPACO:
                    y += (Integer) elemento.getDados();
                    break;
            }
        }
        
        // Rodapé
        g2d.setFont(relatorio.getFonteCabecalho());
        g2d.setColor(Color.GRAY);
        String rodape = String.format(relatorio.getRodape(), 1, 1);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (painelConteudo.getWidth() - fm.stringWidth(rodape)) / 2;
        g2d.drawString(rodape, x, painelConteudo.getHeight() - 30);
    }
    
    private int desenharTabela(Graphics2D g2d, TableModel modelo, int x, int y, int largura) {
        int colunas = modelo.getColumnCount();
        int linhas = modelo.getRowCount();
        int larguraColuna = largura / colunas;
        int alturaLinha = 20;
        
        // Cabeçalho da tabela
        g2d.setColor(new Color(0xE0E0E0));
        g2d.fillRect(x, y, largura, alturaLinha);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        
        for (int c = 0; c < colunas; c++) {
            g2d.drawRect(x + c * larguraColuna, y, larguraColuna, alturaLinha);
            g2d.drawString(modelo.getColumnName(c), x + c * larguraColuna + 5, y + 14);
        }
        
        y += alturaLinha;
        
        // Linhas
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        for (int l = 0; l < linhas; l++) {
            // Zebra
            if (l % 2 == 1) {
                g2d.setColor(new Color(0xF5F5F5));
                g2d.fillRect(x, y, largura, alturaLinha);
            }
            
            g2d.setColor(Color.BLACK);
            
            for (int c = 0; c < colunas; c++) {
                g2d.drawRect(x + c * larguraColuna, y, larguraColuna, alturaLinha);
                Object valor = modelo.getValueAt(l, c);
                if (valor != null) {
                    g2d.drawString(valor.toString(), x + c * larguraColuna + 5, y + 14);
                }
            }
            
            y += alturaLinha;
        }
        
        return y + 10;
    }
    
    private void ajustarZoom(double delta) {
        zoom = Math.max(0.5, Math.min(2.0, zoom + delta));
        int novaLargura = (int) (595 * zoom);
        int novaAltura = (int) (calcularAltura() * zoom);
        painelConteudo.setPreferredSize(new Dimension(novaLargura, novaAltura));
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
}

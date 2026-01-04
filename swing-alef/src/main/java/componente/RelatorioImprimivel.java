package componente;

import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.print.*;

/**
 * Implementação de Printable para impressão de relatórios.
 * 
 * @author alefi
 * @since 1.1
 */
public class RelatorioImprimivel implements Printable {
    
    private ConstrutorRelatorio relatorio;
    
    public RelatorioImprimivel(ConstrutorRelatorio relatorio) {
        this.relatorio = relatorio;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int largura = (int) pageFormat.getImageableWidth();
        int altura = (int) pageFormat.getImageableHeight();
        int y = 0;
        int margemX = 10;
        
        // Cabeçalho
        if (!relatorio.getCabecalho().isEmpty()) {
            g2d.setFont(relatorio.getFonteCabecalho());
            g2d.setColor(Color.GRAY);
            g2d.drawString(relatorio.getCabecalho(), margemX, y + 10);
            y += 15;
        }
        
        // Título
        if (!relatorio.getTitulo().isEmpty()) {
            g2d.setFont(relatorio.getFonteTitulo());
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (largura - fm.stringWidth(relatorio.getTitulo())) / 2;
            g2d.drawString(relatorio.getTitulo(), x, y + fm.getAscent());
            y += fm.getHeight() + 3;
        }
        
        // Subtítulo
        if (!relatorio.getSubtitulo().isEmpty()) {
            g2d.setFont(relatorio.getFonteSubtitulo());
            g2d.setColor(Color.DARK_GRAY);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (largura - fm.stringWidth(relatorio.getSubtitulo())) / 2;
            g2d.drawString(relatorio.getSubtitulo(), x, y + fm.getAscent());
            y += fm.getHeight() + 15;
        }
        
        // Elementos
        for (ConstrutorRelatorio.ElementoRelatorio elemento : relatorio.getElementos()) {
            switch (elemento.getTipo()) {
                case TEXTO:
                    g2d.setFont(relatorio.getFonteConteudo());
                    g2d.setColor(Color.BLACK);
                    g2d.drawString((String) elemento.getDados(), margemX, y + 12);
                    y += 16;
                    break;
                    
                case SECAO:
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString((String) elemento.getDados(), margemX, y + 14);
                    y += 18;
                    
                    g2d.setFont(relatorio.getFonteConteudo());
                    g2d.drawString((String) elemento.getDados2(), margemX + 10, y + 12);
                    y += 18;
                    break;
                    
                case TABELA:
                    y = imprimirTabela(g2d, (TableModel) elemento.getDados(), margemX, y, largura - margemX * 2);
                    break;
                    
                case SEPARADOR:
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawLine(margemX, y + 5, largura - margemX, y + 5);
                    y += 10;
                    break;
                    
                case ESPACO:
                    y += (Integer) elemento.getDados();
                    break;
            }
        }
        
        // Rodapé
        g2d.setFont(relatorio.getFonteCabecalho());
        g2d.setColor(Color.GRAY);
        String rodape = String.format(relatorio.getRodape(), pageIndex + 1, 1);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (largura - fm.stringWidth(rodape)) / 2;
        g2d.drawString(rodape, x, altura - 5);
        
        return PAGE_EXISTS;
    }
    
    private int imprimirTabela(Graphics2D g2d, TableModel modelo, int x, int y, int largura) {
        int colunas = modelo.getColumnCount();
        int linhas = modelo.getRowCount();
        int larguraColuna = largura / colunas;
        int alturaLinha = 14;
        
        // Cabeçalho
        g2d.setColor(new Color(0xE0E0E0));
        g2d.fillRect(x, y, largura, alturaLinha);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
        
        for (int c = 0; c < colunas; c++) {
            g2d.drawRect(x + c * larguraColuna, y, larguraColuna, alturaLinha);
            g2d.drawString(modelo.getColumnName(c), x + c * larguraColuna + 3, y + 10);
        }
        
        y += alturaLinha;
        
        // Linhas
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
        
        for (int l = 0; l < linhas; l++) {
            if (l % 2 == 1) {
                g2d.setColor(new Color(0xF5F5F5));
                g2d.fillRect(x, y, largura, alturaLinha);
            }
            
            g2d.setColor(Color.BLACK);
            
            for (int c = 0; c < colunas; c++) {
                g2d.drawRect(x + c * larguraColuna, y, larguraColuna, alturaLinha);
                Object valor = modelo.getValueAt(l, c);
                if (valor != null) {
                    g2d.drawString(valor.toString(), x + c * larguraColuna + 3, y + 10);
                }
            }
            
            y += alturaLinha;
        }
        
        return y + 8;
    }
}

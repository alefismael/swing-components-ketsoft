package componente;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Construtor fluente para relatórios imprimíveis.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * ConstrutorRelatorio relatorio = new ConstrutorRelatorio()
 *     .titulo("Relatório de Vendas")
 *     .subtitulo("Janeiro 2024")
 *     .cabecalho("Sistema XYZ")
 *     .rodape("Página %d de %d")
 *     .tabela(modeloTabela)
 *     .secao("Observações", "Dados extraídos em 01/01/2024");
 * 
 * relatorio.visualizar();
 * relatorio.imprimir();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class ConstrutorRelatorio {
    
    private String titulo = "";
    private String subtitulo = "";
    private String cabecalho = "";
    private String rodape = "Página %d";
    private List<ElementoRelatorio> elementos = new ArrayList<>();
    
    private Font fonteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    private Font fonteSubtitulo = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private Font fonteCabecalho = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private Font fonteConteudo = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    
    public ConstrutorRelatorio() {
    }
    
    /**
     * Define o título do relatório.
     */
    public ConstrutorRelatorio titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }
    
    /**
     * Define o subtítulo.
     */
    public ConstrutorRelatorio subtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
        return this;
    }
    
    /**
     * Define o cabeçalho (aparece em todas as páginas).
     */
    public ConstrutorRelatorio cabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
        return this;
    }
    
    /**
     * Define o rodapé (use %d para número da página).
     */
    public ConstrutorRelatorio rodape(String rodape) {
        this.rodape = rodape;
        return this;
    }
    
    /**
     * Adiciona um texto ao relatório.
     */
    public ConstrutorRelatorio texto(String texto) {
        elementos.add(new ElementoRelatorio(TipoElemento.TEXTO, texto));
        return this;
    }
    
    /**
     * Adiciona uma seção com título e conteúdo.
     */
    public ConstrutorRelatorio secao(String tituloSecao, String conteudo) {
        elementos.add(new ElementoRelatorio(TipoElemento.SECAO, tituloSecao, conteudo));
        return this;
    }
    
    /**
     * Adiciona uma tabela ao relatório.
     */
    public ConstrutorRelatorio tabela(TableModel modelo) {
        elementos.add(new ElementoRelatorio(TipoElemento.TABELA, modelo));
        return this;
    }
    
    /**
     * Adiciona uma linha separadora.
     */
    public ConstrutorRelatorio separador() {
        elementos.add(new ElementoRelatorio(TipoElemento.SEPARADOR, null));
        return this;
    }
    
    /**
     * Adiciona espaço em branco.
     */
    public ConstrutorRelatorio espaco(int pixels) {
        elementos.add(new ElementoRelatorio(TipoElemento.ESPACO, pixels));
        return this;
    }
    
    /**
     * Visualiza o relatório em uma janela.
     */
    public void visualizar() {
        VisualizadorRelatorio visualizador = new VisualizadorRelatorio(this);
        
        JFrame frame = new JFrame("Visualizar Relatório - " + titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(visualizador);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * Imprime o relatório.
     */
    public void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new RelatorioImprimivel(this));
        
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, 
                    "Erro ao imprimir: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Getters para uso interno
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public String getCabecalho() { return cabecalho; }
    public String getRodape() { return rodape; }
    public List<ElementoRelatorio> getElementos() { return elementos; }
    public Font getFonteTitulo() { return fonteTitulo; }
    public Font getFonteSubtitulo() { return fonteSubtitulo; }
    public Font getFonteCabecalho() { return fonteCabecalho; }
    public Font getFonteConteudo() { return fonteConteudo; }
    
    public void setFonteTitulo(Font fonte) { this.fonteTitulo = fonte; }
    public void setFonteSubtitulo(Font fonte) { this.fonteSubtitulo = fonte; }
    public void setFonteConteudo(Font fonte) { this.fonteConteudo = fonte; }
    
    // Classes internas
    public enum TipoElemento {
        TEXTO, SECAO, TABELA, SEPARADOR, ESPACO
    }
    
    public static class ElementoRelatorio {
        private TipoElemento tipo;
        private Object dados;
        private Object dados2;
        
        public ElementoRelatorio(TipoElemento tipo, Object dados) {
            this.tipo = tipo;
            this.dados = dados;
        }
        
        public ElementoRelatorio(TipoElemento tipo, Object dados, Object dados2) {
            this.tipo = tipo;
            this.dados = dados;
            this.dados2 = dados2;
        }
        
        public TipoElemento getTipo() { return tipo; }
        public Object getDados() { return dados; }
        public Object getDados2() { return dados2; }
    }
}

package util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utilitário para exportar tabelas para CSV e Excel.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * ExportadorUtil.exportarCSV(tabela.getModel(), new File("dados.csv"));
 * ExportadorUtil.exportarCSVComDialogo(tabela.getModel(), frame);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class ExportadorUtil {
    
    private ExportadorUtil() {}
    
    /**
     * Exporta TableModel para CSV.
     */
    public static void exportarCSV(TableModel modelo, File arquivo) throws IOException {
        exportarCSV(modelo, arquivo, ";");
    }
    
    /**
     * Exporta TableModel para CSV com separador customizado.
     */
    public static void exportarCSV(TableModel modelo, File arquivo, String separador) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
            
            // BOM para Excel reconhecer UTF-8
            writer.write('\ufeff');
            
            // Cabeçalho
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                if (c > 0) writer.write(separador);
                writer.write(escaparCSV(modelo.getColumnName(c)));
            }
            writer.newLine();
            
            // Dados
            for (int l = 0; l < modelo.getRowCount(); l++) {
                for (int c = 0; c < modelo.getColumnCount(); c++) {
                    if (c > 0) writer.write(separador);
                    Object valor = modelo.getValueAt(l, c);
                    writer.write(escaparCSV(valor != null ? valor.toString() : ""));
                }
                writer.newLine();
            }
        }
    }
    
    /**
     * Exporta para CSV com diálogo de arquivo.
     */
    public static boolean exportarCSVComDialogo(TableModel modelo, JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar para CSV");
        chooser.setSelectedFile(new File("dados.csv"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV (*.csv)", "csv"));
        
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".csv");
            }
            
            try {
                exportarCSV(modelo, arquivo);
                JOptionPane.showMessageDialog(parent, 
                    "Arquivo exportado com sucesso!\n" + arquivo.getAbsolutePath(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Erro ao exportar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    /**
     * Exporta para Excel XML (SpreadsheetML).
     * Formato XML que o Excel abre nativamente.
     */
    public static void exportarExcelXML(TableModel modelo, File arquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
            writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            writer.write("  xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n");
            writer.write("  <Worksheet ss:Name=\"Dados\">\n");
            writer.write("    <Table>\n");
            
            // Cabeçalho
            writer.write("      <Row>\n");
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                writer.write("        <Cell><Data ss:Type=\"String\">");
                writer.write(escaparXML(modelo.getColumnName(c)));
                writer.write("</Data></Cell>\n");
            }
            writer.write("      </Row>\n");
            
            // Dados
            for (int l = 0; l < modelo.getRowCount(); l++) {
                writer.write("      <Row>\n");
                for (int c = 0; c < modelo.getColumnCount(); c++) {
                    Object valor = modelo.getValueAt(l, c);
                    String tipo = "String";
                    String valorStr = valor != null ? valor.toString() : "";
                    
                    // Detectar tipo numérico
                    if (valor instanceof Number) {
                        tipo = "Number";
                    }
                    
                    writer.write("        <Cell><Data ss:Type=\"" + tipo + "\">");
                    writer.write(escaparXML(valorStr));
                    writer.write("</Data></Cell>\n");
                }
                writer.write("      </Row>\n");
            }
            
            writer.write("    </Table>\n");
            writer.write("  </Worksheet>\n");
            writer.write("</Workbook>\n");
        }
    }
    
    /**
     * Exporta para Excel com diálogo.
     */
    public static boolean exportarExcelComDialogo(TableModel modelo, JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar para Excel");
        chooser.setSelectedFile(new File("dados.xml"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel XML (*.xml)", "xml"));
        
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".xml")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".xml");
            }
            
            try {
                exportarExcelXML(modelo, arquivo);
                JOptionPane.showMessageDialog(parent,
                    "Arquivo exportado com sucesso!\n" + arquivo.getAbsolutePath(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Erro ao exportar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    /**
     * Gera string CSV de um TableModel.
     */
    public static String gerarCSV(TableModel modelo) {
        StringBuilder sb = new StringBuilder();
        
        for (int c = 0; c < modelo.getColumnCount(); c++) {
            if (c > 0) sb.append(";");
            sb.append(escaparCSV(modelo.getColumnName(c)));
        }
        sb.append("\n");
        
        for (int l = 0; l < modelo.getRowCount(); l++) {
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                if (c > 0) sb.append(";");
                Object valor = modelo.getValueAt(l, c);
                sb.append(escaparCSV(valor != null ? valor.toString() : ""));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private static String escaparCSV(String valor) {
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
    
    private static String escaparXML(String valor) {
        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}

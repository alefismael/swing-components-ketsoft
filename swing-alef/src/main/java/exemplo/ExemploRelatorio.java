package exemplo;

import componente.*;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Exemplo dos componentes de Relatório (Ciclo 14).
 */
public class ExemploRelatorio extends JPanel {
    
    public ExemploRelatorio() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Descrição
        JPanel topo = new JPanel(new BorderLayout());
        JTextArea descricao = new JTextArea(
            "O ConstrutorRelatorio permite criar relatórios com API fluente.\n" +
            "O ExportadorUtil exporta tabelas para CSV e Excel.\n"
        );
        descricao.setEditable(false);
        descricao.setRows(2);
        descricao.setBackground(getBackground());
        topo.add(descricao, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);
        
        // Tabela de exemplo
        String[] colunas = {"ID", "Produto", "Quantidade", "Preço"};
        Object[][] dados = {
            {1, "Notebook Dell", 5, 4500.00},
            {2, "Mouse Logitech", 20, 150.00},
            {3, "Teclado Microsoft", 15, 200.00},
            {4, "Monitor LG 27\"", 8, 1800.00},
            {5, "Webcam HD", 12, 350.00}
        };
        
        DefaultTableModel modelo = new DefaultTableModel(dados, colunas);
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnVisualizar = new JButton("📄 Visualizar Relatório");
        btnVisualizar.addActionListener(e -> {
            ConstrutorRelatorio relatorio = new ConstrutorRelatorio()
                .titulo("Relatório de Produtos")
                .subtitulo("Inventário Atual")
                .cabecalho("Sistema de Gestão - Swing Alef")
                .rodape("Página %d")
                .texto("Lista de produtos em estoque:")
                .separador()
                .tabela(modelo)
                .separador()
                .secao("Observações", "Relatório gerado automaticamente.");
            
            relatorio.visualizar();
        });
        
        JButton btnImprimir = new JButton("🖨️ Imprimir");
        btnImprimir.addActionListener(e -> {
            new ConstrutorRelatorio()
                .titulo("Relatório de Produtos")
                .tabela(modelo)
                .imprimir();
        });
        
        JButton btnCSV = new JButton("📊 Exportar CSV");
        btnCSV.addActionListener(e -> {
            ExportadorUtil.exportarCSVComDialogo(modelo, 
                (JFrame) SwingUtilities.getWindowAncestor(this));
        });
        
        JButton btnExcel = new JButton("📗 Exportar Excel");
        btnExcel.addActionListener(e -> {
            ExportadorUtil.exportarExcelComDialogo(modelo,
                (JFrame) SwingUtilities.getWindowAncestor(this));
        });
        
        botoes.add(btnVisualizar);
        botoes.add(btnImprimir);
        botoes.add(btnCSV);
        botoes.add(btnExcel);
        
        add(botoes, BorderLayout.SOUTH);
    }
}

package exemplo;

import campo.*;
import componente.*;

import javax.swing.*;
import java.awt.*;
import java.time.*;

/**
 * Exemplo dos componentes de Calendário (Ciclo 16).
 */
public class ExemploCalendario extends JPanel {
    
    public ExemploCalendario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Esquerda - Calendário grande
        JPanel esquerda = new JPanel(new BorderLayout());
        esquerda.setBorder(BorderFactory.createTitledBorder("PainelCalendario"));
        
        PainelCalendario calendario = new PainelCalendario();
        calendario.setPreferredSize(new Dimension(300, 280));
        
        // Destacar algumas datas
        calendario.destacarData(LocalDate.now().plusDays(2), new Color(0xFFF3E0));
        calendario.destacarData(LocalDate.now().plusDays(5), new Color(0xE8F5E9));
        calendario.destacarData(LocalDate.now().minusDays(3), new Color(0xFFEBEE));
        
        JLabel lblSelecionado = new JLabel("Data: " + LocalDate.now());
        lblSelecionado.setFont(lblSelecionado.getFont().deriveFont(14f));
        
        calendario.setAoSelecionar(data -> lblSelecionado.setText("Data: " + data));
        
        esquerda.add(calendario, BorderLayout.CENTER);
        esquerda.add(lblSelecionado, BorderLayout.SOUTH);
        
        // Direita - Seletores
        JPanel direita = new JPanel();
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));
        
        // SeletorData
        JPanel pSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSeletor.setBorder(BorderFactory.createTitledBorder("SeletorData"));
        
        SeletorData seletorData = new SeletorData();
        seletorData.setData(LocalDate.now());
        seletorData.setAoAlterar(d -> System.out.println("Data alterada: " + d));
        
        pSeletor.add(new JLabel("Data:"));
        pSeletor.add(seletorData);
        
        JButton btnHoje = new JButton("Hoje");
        btnHoje.addActionListener(e -> seletorData.setData(LocalDate.now()));
        pSeletor.add(btnHoje);
        
        direita.add(pSeletor);
        
        // SeletorPeriodo
        JPanel pPeriodo = new JPanel(new BorderLayout(5, 5));
        pPeriodo.setBorder(BorderFactory.createTitledBorder("SeletorPeriodo"));
        
        SeletorPeriodo seletorPeriodo = new SeletorPeriodo("De:", "Até:");
        seletorPeriodo.setPeriodo(LocalDate.now().minusDays(7), LocalDate.now());
        
        JPanel periodoTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodoTop.add(seletorPeriodo);
        pPeriodo.add(periodoTop, BorderLayout.NORTH);
        
        // Períodos predefinidos
        JPanel predef = new JPanel(new FlowLayout(FlowLayout.LEFT));
        predef.add(new JLabel("Predefinidos:"));
        
        JButton btn7d = new JButton("7 dias");
        btn7d.addActionListener(e -> seletorPeriodo.setPeriodoPredefinido(SeletorPeriodo.PeriodoPredefinido.ULTIMOS_7_DIAS));
        
        JButton btn30d = new JButton("30 dias");
        btn30d.addActionListener(e -> seletorPeriodo.setPeriodoPredefinido(SeletorPeriodo.PeriodoPredefinido.ULTIMOS_30_DIAS));
        
        JButton btnMes = new JButton("Mês atual");
        btnMes.addActionListener(e -> seletorPeriodo.setPeriodoPredefinido(SeletorPeriodo.PeriodoPredefinido.MES_ATUAL));
        
        JButton btnAno = new JButton("Ano atual");
        btnAno.addActionListener(e -> seletorPeriodo.setPeriodoPredefinido(SeletorPeriodo.PeriodoPredefinido.ANO_ATUAL));
        
        predef.add(btn7d);
        predef.add(btn30d);
        predef.add(btnMes);
        predef.add(btnAno);
        
        pPeriodo.add(predef, BorderLayout.CENTER);
        
        direita.add(pPeriodo);
        direita.add(Box.createVerticalGlue());
        
        // Layout principal
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, esquerda, direita);
        split.setDividerLocation(320);
        
        add(split, BorderLayout.CENTER);
    }
}

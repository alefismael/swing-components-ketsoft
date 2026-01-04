package componente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Painel de calendário mensal com seleção de data.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * PainelCalendario calendario = new PainelCalendario();
 * calendario.setAoSelecionar(data -> System.out.println("Selecionado: " + data));
 * calendario.destacarData(LocalDate.now(), Color.GREEN);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class PainelCalendario extends JPanel {
    
    private YearMonth mesAtual;
    private LocalDate dataSelecionada;
    
    private JLabel lblMesAno;
    private JPanel painelDias;
    private JButton[] botoesDias = new JButton[42]; // 6 semanas x 7 dias
    
    private Consumer<LocalDate> aoSelecionar;
    private Map<LocalDate, Color> datasDestacadas = new HashMap<>();
    
    private static final String[] DIAS_SEMANA = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));
    
    public PainelCalendario() {
        this(YearMonth.now());
    }
    
    public PainelCalendario(YearMonth mes) {
        this.mesAtual = mes;
        this.dataSelecionada = LocalDate.now();
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Navegação
        JPanel painelNav = new JPanel(new BorderLayout());
        
        JButton btnAnterior = new JButton("◀");
        btnAnterior.setMargin(new Insets(2, 8, 2, 8));
        btnAnterior.addActionListener(e -> mesAnterior());
        
        JButton btnProximo = new JButton("▶");
        btnProximo.setMargin(new Insets(2, 8, 2, 8));
        btnProximo.addActionListener(e -> proximoMes());
        
        lblMesAno = new JLabel("", SwingConstants.CENTER);
        lblMesAno.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblMesAno.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblMesAno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                irParaHoje();
            }
        });
        
        painelNav.add(btnAnterior, BorderLayout.WEST);
        painelNav.add(lblMesAno, BorderLayout.CENTER);
        painelNav.add(btnProximo, BorderLayout.EAST);
        
        add(painelNav, BorderLayout.NORTH);
        
        // Dias da semana
        JPanel painelSemana = new JPanel(new GridLayout(1, 7, 2, 2));
        for (String dia : DIAS_SEMANA) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            lbl.setForeground(UIManager.getColor("Label.disabledForeground"));
            painelSemana.add(lbl);
        }
        add(painelSemana, BorderLayout.CENTER);
        
        // Grade de dias
        painelDias = new JPanel(new GridLayout(6, 7, 2, 2));
        
        for (int i = 0; i < 42; i++) {
            final int indice = i;
            botoesDias[i] = new JButton();
            botoesDias[i].setMargin(new Insets(5, 5, 5, 5));
            botoesDias[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            botoesDias[i].addActionListener(e -> selecionarDia(indice));
            painelDias.add(botoesDias[i]);
        }
        
        // Wrapper para cabeçalho + dias
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.add(painelSemana, BorderLayout.NORTH);
        wrapper.add(painelDias, BorderLayout.CENTER);
        
        add(wrapper, BorderLayout.CENTER);
        
        atualizarCalendario();
    }
    
    private void atualizarCalendario() {
        lblMesAno.setText(mesAtual.format(FORMATO_MES).substring(0, 1).toUpperCase() + 
                         mesAtual.format(FORMATO_MES).substring(1));
        
        LocalDate primeiroDia = mesAtual.atDay(1);
        int diaSemanaInicio = primeiroDia.getDayOfWeek().getValue() % 7; // Domingo = 0
        int diasNoMes = mesAtual.lengthOfMonth();
        
        LocalDate hoje = LocalDate.now();
        
        for (int i = 0; i < 42; i++) {
            int dia = i - diaSemanaInicio + 1;
            JButton btn = botoesDias[i];
            
            if (dia >= 1 && dia <= diasNoMes) {
                LocalDate data = mesAtual.atDay(dia);
                btn.setText(String.valueOf(dia));
                btn.setEnabled(true);
                btn.setVisible(true);
                
                // Estilo padrão
                btn.setBackground(null);
                btn.setForeground(UIManager.getColor("Button.foreground"));
                
                // Hoje
                if (data.equals(hoje)) {
                    btn.setBorder(BorderFactory.createLineBorder(new Color(0x2196F3), 2));
                } else {
                    btn.setBorder(UIManager.getBorder("Button.border"));
                }
                
                // Selecionado
                if (data.equals(dataSelecionada)) {
                    btn.setBackground(new Color(0x2196F3));
                    btn.setForeground(Color.WHITE);
                }
                
                // Destacado
                Color corDestaque = datasDestacadas.get(data);
                if (corDestaque != null && !data.equals(dataSelecionada)) {
                    btn.setBackground(corDestaque);
                }
                
                // Fim de semana
                if (i % 7 == 0 || i % 7 == 6) {
                    if (!data.equals(dataSelecionada)) {
                        btn.setForeground(new Color(0xF44336));
                    }
                }
            } else {
                btn.setText("");
                btn.setEnabled(false);
                btn.setVisible(false);
            }
        }
        
        painelDias.revalidate();
        painelDias.repaint();
    }
    
    private void selecionarDia(int indice) {
        LocalDate primeiroDia = mesAtual.atDay(1);
        int diaSemanaInicio = primeiroDia.getDayOfWeek().getValue() % 7;
        int dia = indice - diaSemanaInicio + 1;
        
        if (dia >= 1 && dia <= mesAtual.lengthOfMonth()) {
            dataSelecionada = mesAtual.atDay(dia);
            atualizarCalendario();
            
            if (aoSelecionar != null) {
                aoSelecionar.accept(dataSelecionada);
            }
        }
    }
    
    /**
     * Vai para o mês anterior.
     */
    public void mesAnterior() {
        mesAtual = mesAtual.minusMonths(1);
        atualizarCalendario();
    }
    
    /**
     * Vai para o próximo mês.
     */
    public void proximoMes() {
        mesAtual = mesAtual.plusMonths(1);
        atualizarCalendario();
    }
    
    /**
     * Vai para a data atual.
     */
    public void irParaHoje() {
        mesAtual = YearMonth.now();
        dataSelecionada = LocalDate.now();
        atualizarCalendario();
        
        if (aoSelecionar != null) {
            aoSelecionar.accept(dataSelecionada);
        }
    }
    
    /**
     * Define a data selecionada.
     */
    public void setDataSelecionada(LocalDate data) {
        this.dataSelecionada = data;
        this.mesAtual = YearMonth.from(data);
        atualizarCalendario();
    }
    
    /**
     * Retorna a data selecionada.
     */
    public LocalDate getDataSelecionada() {
        return dataSelecionada;
    }
    
    /**
     * Define ação ao selecionar data.
     */
    public void setAoSelecionar(Consumer<LocalDate> acao) {
        this.aoSelecionar = acao;
    }
    
    /**
     * Destaca uma data com cor.
     */
    public void destacarData(LocalDate data, Color cor) {
        datasDestacadas.put(data, cor);
        atualizarCalendario();
    }
    
    /**
     * Remove destaque de uma data.
     */
    public void removerDestaque(LocalDate data) {
        datasDestacadas.remove(data);
        atualizarCalendario();
    }
    
    /**
     * Limpa todos os destaques.
     */
    public void limparDestaques() {
        datasDestacadas.clear();
        atualizarCalendario();
    }
    
    /**
     * Define o mês/ano exibido.
     */
    public void setMesAno(YearMonth mesAno) {
        this.mesAtual = mesAno;
        atualizarCalendario();
    }
    
    /**
     * Retorna o mês/ano exibido.
     */
    public YearMonth getMesAno() {
        return mesAtual;
    }
}

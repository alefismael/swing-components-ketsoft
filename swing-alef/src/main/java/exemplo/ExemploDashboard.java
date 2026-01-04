package exemplo;

import componente.*;
import dialogo.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Exemplo dos componentes de Dashboard (Ciclo 12).
 */
public class ExemploDashboard extends JPanel {
    
    public ExemploDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Dashboard principal
        PainelDashboard dashboard = new PainelDashboard();
        dashboard.setColunas(4);
        
        // Cartões coloridos
        CartaoDashboard vendas = new CartaoDashboard("Total Vendas", "R$ 45.230", "💰");
        vendas.setCorFundo(new Color(0x4CAF50));
        vendas.setSubtitulo("+12% este mês");
        
        CartaoDashboard clientes = new CartaoDashboard("Clientes", "1.234", "👥");
        clientes.setCorFundo(new Color(0x2196F3));
        clientes.setSubtitulo("25 novos hoje");
        
        CartaoDashboard pedidos = new CartaoDashboard("Pedidos", "567", "📦");
        pedidos.setCorFundo(new Color(0xFF9800));
        pedidos.setSubtitulo("42 pendentes");
        
        CartaoDashboard tickets = new CartaoDashboard("Tickets", "23", "🎫");
        tickets.setCorFundo(new Color(0xF44336));
        tickets.setSubtitulo("5 urgentes");
        
        dashboard.adicionarCartao(vendas);
        dashboard.adicionarCartao(clientes);
        dashboard.adicionarCartao(pedidos);
        dashboard.adicionarCartao(tickets);
        
        // KPIs
        CartaoKPI kpi1 = new CartaoKPI("Conversão", "3.2%", "+0.5%");
        kpi1.setTendencia(CartaoKPI.TENDENCIA_ALTA);
        
        CartaoKPI kpi2 = new CartaoKPI("Ticket Médio", "R$ 180", "-5%");
        kpi2.setTendencia(CartaoKPI.TENDENCIA_BAIXA);
        
        CartaoKPI kpi3 = new CartaoKPI("NPS", "72", "0%");
        kpi3.setTendencia(CartaoKPI.TENDENCIA_ESTAVEL);
        
        // Cartão com sparkline
        CartaoEstatistica stats = new CartaoEstatistica("Vendas Semanais", "R$ 12.500");
        stats.setDados(Arrays.asList(100.0, 150.0, 120.0, 180.0, 160.0, 200.0, 220.0));
        
        dashboard.adicionarCartao(kpi1);
        dashboard.adicionarCartao(kpi2);
        dashboard.adicionarCartao(kpi3);
        dashboard.adicionarCartao(stats);
        
        add(new JScrollPane(dashboard), BorderLayout.CENTER);
        
        // Controles
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JSpinner spinColunas = new JSpinner(new SpinnerNumberModel(4, 1, 6, 1));
        spinColunas.addChangeListener(e -> dashboard.setColunas((Integer) spinColunas.getValue()));
        
        controles.add(new JLabel("Colunas:"));
        controles.add(spinColunas);
        
        add(controles, BorderLayout.NORTH);
    }
}

package exemplo;

import componente.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Exemplo dos componentes de Gráficos (Ciclo 13).
 */
public class ExemploGraficos extends JPanel {
    
    public ExemploGraficos() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Gráfico de barras
        GraficoBarras barras = new GraficoBarras();
        barras.setTitulo("Vendas por Mês");
        barras.setDados(
            Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun"),
            Arrays.asList(120.0, 150.0, 180.0, 140.0, 200.0, 170.0)
        );
        
        JPanel painelBarras = new JPanel(new BorderLayout());
        painelBarras.setBorder(BorderFactory.createTitledBorder("Gráfico de Barras"));
        painelBarras.add(barras);
        add(painelBarras);
        
        // Gráfico de linhas
        GraficoLinhas linhas = new GraficoLinhas();
        linhas.setTitulo("Evolução Anual");
        linhas.adicionarSerie("Receita", Arrays.asList(100.0, 120.0, 115.0, 140.0, 160.0, 180.0));
        linhas.adicionarSerie("Custos", Arrays.asList(80.0, 85.0, 90.0, 95.0, 100.0, 105.0));
        linhas.setRotulos(Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun"));
        linhas.setPreencherArea(true);
        
        JPanel painelLinhas = new JPanel(new BorderLayout());
        painelLinhas.setBorder(BorderFactory.createTitledBorder("Gráfico de Linhas"));
        painelLinhas.add(linhas);
        add(painelLinhas);
        
        // Gráfico de pizza
        GraficoPizza pizza = new GraficoPizza();
        pizza.setTitulo("Vendas por Região");
        pizza.setDados(
            Arrays.asList("Norte", "Sul", "Leste", "Oeste", "Centro"),
            Arrays.asList(25.0, 30.0, 15.0, 18.0, 12.0)
        );
        
        JPanel painelPizza = new JPanel(new BorderLayout());
        painelPizza.setBorder(BorderFactory.createTitledBorder("Gráfico de Pizza"));
        painelPizza.add(pizza);
        add(painelPizza);
        
        // Pizza rosca
        GraficoPizza rosca = new GraficoPizza();
        rosca.setTitulo("Categorias de Produtos");
        rosca.setDados(
            Arrays.asList("Eletrônicos", "Roupas", "Alimentos", "Outros"),
            Arrays.asList(40.0, 25.0, 20.0, 15.0)
        );
        rosca.setRosca(true);
        
        JPanel painelRosca = new JPanel(new BorderLayout());
        painelRosca.setBorder(BorderFactory.createTitledBorder("Gráfico Rosca (Donut)"));
        painelRosca.add(rosca);
        add(painelRosca);
    }
}

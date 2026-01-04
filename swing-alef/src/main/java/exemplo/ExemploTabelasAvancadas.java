package exemplo;

import tabela.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Exemplo das Tabelas Avançadas (Ciclo 17).
 */
public class ExemploTabelasAvancadas extends JPanel {
    
    public ExemploTabelasAvancadas() {
        setLayout(new GridLayout(1, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // TabelaPaginada
        add(criarExemploPaginada());
        
        // TabelaEditavel
        add(criarExemploEditavel());
        
        // TabelaAgrupada
        add(criarExemploAgrupada());
    }
    
    private JPanel criarExemploPaginada() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("TabelaPaginada"));
        
        // Dados simulados
        List<String[]> todosDados = new ArrayList<>();
        for (int i = 1; i <= 95; i++) {
            todosDados.add(new String[]{
                String.valueOf(i),
                "Produto " + i,
                "R$ " + (100 + i * 10)
            });
        }
        
        TabelaPaginada<String[]> tabela = new TabelaPaginada<>();
        tabela.setColunas("ID", "Nome", "Preço");
        tabela.setConversor(arr -> arr);
        tabela.setTamanhoPagina(10);
        
        tabela.setContadorTotal(todosDados::size);
        tabela.setFonteDados((pagina, tamanho) -> {
            int inicio = pagina * tamanho;
            int fim = Math.min(inicio + tamanho, todosDados.size());
            if (inicio >= todosDados.size()) return List.of();
            return todosDados.subList(inicio, fim);
        });
        
        tabela.carregar();
        
        p.add(tabela, BorderLayout.CENTER);
        return p;
    }
    
    private JPanel criarExemploEditavel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("TabelaEditavel"));
        
        TabelaEditavel<String[]> tabela = new TabelaEditavel<>();
        tabela.setColunas("ID", "Nome", "Qtd");
        tabela.setColunasEditaveis(1, 2); // Nome e Qtd editáveis
        tabela.setConversor(arr -> arr);
        
        List<String[]> itens = new ArrayList<>();
        itens.add(new String[]{"1", "Item A", "10"});
        itens.add(new String[]{"2", "Item B", "20"});
        itens.add(new String[]{"3", "Item C", "30"});
        
        tabela.setItens(itens);
        
        tabela.setAoEditar((item, col, valor) -> {
            item[col] = valor.toString();
            System.out.println("Editado: " + Arrays.toString(item));
        });
        
        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnAdd = new JButton("+");
        btnAdd.addActionListener(e -> {
            int id = tabela.getItens().size() + 1;
            tabela.adicionarItem(new String[]{String.valueOf(id), "Novo", "0"});
        });
        
        JButton btnRemove = new JButton("-");
        btnRemove.addActionListener(e -> tabela.removerItemSelecionado());
        
        botoes.add(btnAdd);
        botoes.add(btnRemove);
        
        p.add(tabela, BorderLayout.CENTER);
        p.add(botoes, BorderLayout.SOUTH);
        
        return p;
    }
    
    private JPanel criarExemploAgrupada() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("TabelaAgrupada"));
        
        // Dados com categoria
        record Produto(String categoria, String nome, double preco) {}
        
        List<Produto> produtos = Arrays.asList(
            new Produto("Eletrônicos", "Notebook", 4500),
            new Produto("Eletrônicos", "Mouse", 150),
            new Produto("Eletrônicos", "Teclado", 200),
            new Produto("Móveis", "Mesa", 800),
            new Produto("Móveis", "Cadeira", 600),
            new Produto("Papelaria", "Caneta", 5),
            new Produto("Papelaria", "Caderno", 20),
            new Produto("Papelaria", "Borracha", 3)
        );
        
        TabelaAgrupada<Produto> tabela = new TabelaAgrupada<>();
        tabela.setColunas("Nome", "Preço");
        tabela.setConversor(prod -> new Object[]{prod.nome(), String.format("R$ %.2f", prod.preco())});
        tabela.setChaveAgrupamento(Produto::categoria);
        tabela.setRotuloGrupo(cat -> (String) cat);
        
        tabela.setItens(produtos);
        
        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnExpandir = new JButton("Expandir");
        btnExpandir.addActionListener(e -> tabela.expandirTodos());
        
        JButton btnRecolher = new JButton("Recolher");
        btnRecolher.addActionListener(e -> tabela.recolherTodos());
        
        botoes.add(btnExpandir);
        botoes.add(btnRecolher);
        
        p.add(tabela, BorderLayout.CENTER);
        p.add(botoes, BorderLayout.SOUTH);
        
        return p;
    }
}

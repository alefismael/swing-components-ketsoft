package exemplo;

import tabela.TabelaModelo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exemplo comparando o padrão ANTIGO (toArray) vs NOVO (TabelaModelo).
 * 
 * Execute para ver as duas abordagens lado a lado.
 * 
 * @author alefi
 */
public class ExemploTabelaModelo extends JFrame {
    
    // ==================== MODELO DE DADOS (IGUAL NAS DUAS ABORDAGENS) ====================
    
    /**
     * Classe Produto - modelo limpo, sem conhecimento de tabelas!
     * Note: NÃO TEM método toArray()
     */
    public static class Produto {
        private final int id;
        private final String nome;
        private final String categoria;
        private final double preco;
        private final int estoque;
        private final boolean ativo;
        
        public Produto(int id, String nome, String categoria, double preco, int estoque, boolean ativo) {
            this.id = id;
            this.nome = nome;
            this.categoria = categoria;
            this.preco = preco;
            this.estoque = estoque;
            this.ativo = ativo;
        }
        
        // Apenas getters - classe focada apenas em seus dados
        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getCategoria() { return categoria; }
        public double getPreco() { return preco; }
        public int getEstoque() { return estoque; }
        public boolean isAtivo() { return ativo; }
        
        // ❌ PADRÃO ANTIGO exigiria isso:
        // public Object[] toArray() {
        //     return new Object[]{id, nome, categoria, preco, estoque, ativo};
        // }
    }
    
    // Dados de exemplo
    private final List<Produto> produtos = new ArrayList<>();
    
    public ExemploTabelaModelo() {
        super("Comparação: DefaultTableModel vs TabelaModelo<T>");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        
        // Criar dados de exemplo
        criarDadosExemplo();
        
        // Layout com duas tabelas lado a lado
        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 0));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        painelPrincipal.add(criarPainelAntigoToArray());
        painelPrincipal.add(criarPainelNovoTabelaModelo());
        
        add(painelPrincipal);
    }
    
    private void criarDadosExemplo() {
        produtos.add(new Produto(1, "Notebook Dell", "Informática", 4599.90, 15, true));
        produtos.add(new Produto(2, "Mouse Logitech", "Periféricos", 149.90, 50, true));
        produtos.add(new Produto(3, "Monitor 27\"", "Informática", 1899.90, 8, true));
        produtos.add(new Produto(4, "Teclado Mecânico", "Periféricos", 399.90, 25, false));
        produtos.add(new Produto(5, "Webcam HD", "Periféricos", 299.90, 0, false));
    }
    
    // ==================== PADRÃO ANTIGO (toArray) ====================
    
    private JPanel criarPainelAntigoToArray() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("❌ ANTIGO: DefaultTableModel + toArray()"));
        
        // Problema: colunas definidas aqui E no toArray() - duplicação!
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "Categoria", "Preço", "Estoque", "Ativo"}, 0
        );
        
        // Problema: precisa converter cada objeto manualmente
        for (Produto p : produtos) {
            // Se não tiver toArray(), precisa fazer isso:
            modelo.addRow(new Object[]{
                p.getId(), 
                p.getNome(), 
                p.getCategoria(), 
                p.getPreco(), 
                p.getEstoque(), 
                p.isAtivo()
            });
        }
        
        JTable tabela = new JTable(modelo);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        // Problema: para obter o objeto, precisa reconstruir
        JButton btnMostrar = new JButton("Mostrar Selecionado");
        btnMostrar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                // ❌ Feio: precisa pegar coluna por coluna
                String info = String.format(
                    "ID: %s, Nome: %s, Preço: %s",
                    modelo.getValueAt(linha, 0),
                    modelo.getValueAt(linha, 1),
                    modelo.getValueAt(linha, 3)
                );
                JOptionPane.showMessageDialog(this, info);
            }
        });
        painel.add(btnMostrar, BorderLayout.SOUTH);
        
        // Explicação
        JTextArea txtExplicacao = new JTextArea(
            "Problemas:\n" +
            "• Modelo precisa método toArray()\n" +
            "• Duplicação: colunas no modelo E na tabela\n" +
            "• Para obter dados: getValueAt() manual\n" +
            "• Mudou ordem? Muda em 2 lugares!"
        );
        txtExplicacao.setEditable(false);
        txtExplicacao.setRows(5);
        txtExplicacao.setBackground(new Color(255, 230, 230));
        painel.add(txtExplicacao, BorderLayout.NORTH);
        
        return painel;
    }
    
    // ==================== PADRÃO NOVO (TabelaModelo<T>) ====================
    
    private JPanel criarPainelNovoTabelaModelo() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("✅ NOVO: TabelaModelo<Produto>"));
        
        // Configuração clara e type-safe
        TabelaModelo<Produto> modelo = new TabelaModelo<>();
        modelo.addColuna("ID", Produto::getId)
              .addColuna("Nome", Produto::getNome)
              .addColuna("Categoria", Produto::getCategoria)
              .addColuna("Preço", Produto::getPreco)
              .addColuna("Estoque", Produto::getEstoque)
              .addColuna("Ativo", Produto::isAtivo, Boolean.class); // Boolean mostra checkbox!
        
        // Adicionar dados - direto!
        modelo.adicionarTodos(produtos);
        
        JTable tabela = new JTable(modelo);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        // Para obter o objeto: direto!
        JButton btnMostrar = new JButton("Mostrar Selecionado");
        btnMostrar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                // ✅ Limpo: obtém o objeto diretamente
                Produto p = modelo.getItem(linha);
                String info = String.format(
                    "ID: %d, Nome: %s, Preço: R$ %.2f",
                    p.getId(),
                    p.getNome(),
                    p.getPreco()
                );
                JOptionPane.showMessageDialog(this, info);
            }
        });
        painel.add(btnMostrar, BorderLayout.SOUTH);
        
        // Explicação
        JTextArea txtExplicacao = new JTextArea(
            "Vantagens:\n" +
            "• Modelo limpo (sem toArray)\n" +
            "• Colunas definidas em UM só lugar\n" +
            "• getItem() retorna o objeto tipado!\n" +
            "• Type-safe com autocompletar"
        );
        txtExplicacao.setEditable(false);
        txtExplicacao.setRows(5);
        txtExplicacao.setBackground(new Color(230, 255, 230));
        painel.add(txtExplicacao, BorderLayout.NORTH);
        
        return painel;
    }
    
    // ==================== MAIN ====================
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            new ExemploTabelaModelo().setVisible(true);
        });
    }
}

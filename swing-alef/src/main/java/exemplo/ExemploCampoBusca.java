package exemplo;

import campo.CampoBusca;
import componente.Toast;
import dialogo.DialogoUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Exemplo de uso do CampoBusca (autocomplete).
 * 
 * Demonstra:
 * - Busca com strings simples
 * - Busca com objetos customizados
 * 
 * @author alefi
 */
public class ExemploCampoBusca extends JPanel {
    
    // Classe de exemplo para objetos
    public static class Produto {
        int id;
        String nome;
        String categoria;
        double preco;
        
        Produto(int id, String nome, String categoria, double preco) {
            this.id = id;
            this.nome = nome;
            this.categoria = categoria;
            this.preco = preco;
        }
        
        @Override
        public String toString() {
            return nome + " - R$ " + String.format("%.2f", preco);
        }
    }
    
    public ExemploCampoBusca() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ======= Exemplo 1: Busca simples (strings) =======
        addTitulo("1. Busca Simples (strings)");
        addDescricao("Digite para filtrar frutas. Selecione uma para ver o Toast.");
        
        List<String> frutas = Arrays.asList(
            "Maçã", "Banana", "Laranja", "Uva", "Manga", 
            "Abacaxi", "Morango", "Melancia", "Pêra", "Kiwi",
            "Mamão", "Goiaba", "Limão", "Tangerina", "Cereja"
        );
        
        CampoBusca<String> buscaFrutas = new CampoBusca<>(frutas);
        buscaFrutas.setPlaceholder("Buscar fruta...");
        buscaFrutas.setMaximumSize(new Dimension(400, 32));
        buscaFrutas.setAlignmentX(LEFT_ALIGNMENT);
        buscaFrutas.aoSelecionar(fruta -> {
            Toast.success(this, "Você selecionou: " + fruta);
        });
        add(buscaFrutas);
        add(Box.createVerticalStrut(20));
        
        // ======= Exemplo 2: Busca com objetos =======
        addTitulo("2. Busca com Objetos (Produto)");
        addDescricao("O toString() do objeto é usado para exibição.");
        
        List<Produto> produtos = Arrays.asList(
            new Produto(1, "Notebook Dell", "Eletrônicos", 3500.00),
            new Produto(2, "Mouse Logitech", "Periféricos", 150.00),
            new Produto(3, "Teclado Mecânico", "Periféricos", 350.00),
            new Produto(4, "Monitor 27\"", "Eletrônicos", 1200.00),
            new Produto(5, "Webcam HD", "Periféricos", 200.00),
            new Produto(6, "Headset Gamer", "Áudio", 280.00),
            new Produto(7, "Caixa de Som", "Áudio", 450.00),
            new Produto(8, "SSD 1TB", "Armazenamento", 400.00)
        );
        
        CampoBusca<Produto> buscaProdutos = new CampoBusca<>(produtos);
        buscaProdutos.setPlaceholder("Buscar produto...");
        buscaProdutos.setMaximumSize(new Dimension(400, 32));
        buscaProdutos.setAlignmentX(LEFT_ALIGNMENT);
        buscaProdutos.aoSelecionar(produto -> {
            DialogoUtil.info(this, 
                "Produto: " + produto.nome + "\n" +
                "Categoria: " + produto.categoria + "\n" +
                "Preço: R$ " + String.format("%.2f", produto.preco),
                "Produto Selecionado");
        });
        add(buscaProdutos);
        add(Box.createVerticalStrut(20));
        
        // ======= Exemplo 3: Campo com busca vazia =======
        addTitulo("3. Campo com Busca Vazia");
        addDescricao("Demonstra comportamento quando não há correspondências.");
        
        CampoBusca<String> buscaVazia = new CampoBusca<>(Arrays.asList("Item A", "Item B", "Item C"));
        buscaVazia.setPlaceholder("Digite 'xyz' para ver 'Nenhum resultado'...");
        buscaVazia.setMaximumSize(new Dimension(400, 32));
        buscaVazia.setAlignmentX(LEFT_ALIGNMENT);
        add(buscaVazia);
        
        // Espacador final
        add(Box.createVerticalGlue());
    }
    
    private void addTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5));
    }
    
    private void addDescricao(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(8));
    }
    
    /**
     * Método main para execução standalone.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            // Usar tema padrão
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exemplo CampoBusca");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.add(new ExemploCampoBusca());
            frame.setVisible(true);
        });
    }
}

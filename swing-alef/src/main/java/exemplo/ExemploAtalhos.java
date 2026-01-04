package exemplo;

import util.GerenciadorAtalhos;
import util.GerenciadorAtalhos.Atalho;

import javax.swing.*;
import java.awt.*;

/**
 * Exemplo de uso do GerenciadorAtalhos.
 * 
 * Demonstra como registrar atalhos de teclado usando a nova API.
 * 
 * @author alefi
 */
public class ExemploAtalhos extends JPanel {
    
    private JTextArea logArea;
    
    public ExemploAtalhos() {
        initComponents();
        configurarAtalhos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel de informações
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel titulo = new JLabel("Atalhos Registrados neste Painel:");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        infoPanel.add(titulo);
        infoPanel.add(Box.createVerticalStrut(10));
        
        String[] atalhos = {
            "Ctrl+N - Novo registro",
            "Ctrl+S - Salvar",
            "Delete - Excluir",
            "F5 - Atualizar",
            "Escape - Cancelar"
        };
        
        for (String atalho : atalhos) {
            JLabel label = new JLabel("• " + atalho);
            label.setAlignmentX(LEFT_ALIGNMENT);
            infoPanel.add(label);
        }
        
        add(infoPanel, BorderLayout.NORTH);
        
        // Área de log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setText("Pressione os atalhos para ver os eventos...\n\n");
        
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        scroll.setPreferredSize(new Dimension(400, 200));
        add(scroll, BorderLayout.CENTER);
        
        // Botão para limpar log
        JButton btnLimpar = new JButton("Limpar Log");
        btnLimpar.addActionListener(e -> logArea.setText(""));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLimpar);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void configurarAtalhos() {
        // Usando o enum Atalho predefinido
        GerenciadorAtalhos.registrar(this, Atalho.NOVO, () -> log("Ctrl+N: Novo registro"));
        GerenciadorAtalhos.registrar(this, Atalho.SALVAR, () -> log("Ctrl+S: Salvar"));
        GerenciadorAtalhos.registrar(this, Atalho.EXCLUIR, () -> log("Delete: Excluir"));
        GerenciadorAtalhos.registrar(this, Atalho.ATUALIZAR, () -> log("F5: Atualizar"));
        GerenciadorAtalhos.registrar(this, Atalho.CANCELAR, () -> log("Escape: Cancelar"));
        
        // Usando KeyStroke customizado
        GerenciadorAtalhos.registrar(this, "ctrl shift A", () -> log("Ctrl+Shift+A: Atalho customizado!"));
    }
    
    private void log(String mensagem) {
        logArea.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + mensagem + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
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
            JFrame frame = new JFrame("Exemplo GerenciadorAtalhos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            
            ExemploAtalhos painel = new ExemploAtalhos();
            frame.add(painel);
            
            // Foco no painel para capturar atalhos
            painel.setFocusable(true);
            painel.requestFocusInWindow();
            
            frame.setVisible(true);
        });
    }
}

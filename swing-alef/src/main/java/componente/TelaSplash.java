package componente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Tela de splash com progresso para inicialização de aplicativos.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * TelaSplash splash = new TelaSplash("Meu Sistema", "1.0.0");
 * splash.mostrar();
 * 
 * // Carregar componentes
 * splash.setProgresso(20, "Conectando banco...");
 * // ...
 * splash.setProgresso(100, "Pronto!");
 * splash.fechar();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class TelaSplash extends JWindow {
    
    private JLabel lblTitulo;
    private JLabel lblVersao;
    private JLabel lblMensagem;
    private JProgressBar progressBar;
    
    private Image imagemFundo;
    private Color corFundo;
    private Color corTexto = Color.WHITE;
    
    public TelaSplash(String titulo, String versao) {
        atualizarCoresDoTema();
        
        setSize(450, 280);
        setLocationRelativeTo(null);
        
        JPanel painel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Gradiente padrão
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, corFundo, 0, getHeight(), corFundo.darker()));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        painel.setBorder(BorderFactory.createLineBorder(corFundo.darker(), 2));
        
        // Conteúdo central
        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(50, 30, 30, 30));
        
        // Título
        lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        lblTitulo.setForeground(corTexto);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(lblTitulo);
        
        conteudo.add(Box.createVerticalStrut(10));
        
        // Versão
        lblVersao = new JLabel("Versão " + versao);
        lblVersao.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        lblVersao.setForeground(new Color(corTexto.getRed(), corTexto.getGreen(), corTexto.getBlue(), 200));
        lblVersao.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(lblVersao);
        
        conteudo.add(Box.createVerticalGlue());
        
        // Mensagem
        lblMensagem = new JLabel("Carregando...");
        lblMensagem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblMensagem.setForeground(corTexto);
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(lblMensagem);
        
        conteudo.add(Box.createVerticalStrut(10));
        
        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(350, 8));
        progressBar.setMaximumSize(new Dimension(350, 8));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setBorderPainted(false);
        conteudo.add(progressBar);
        
        painel.add(conteudo, BorderLayout.CENTER);
        
        setContentPane(painel);
    }
    
    /**
     * Exibe a tela de splash.
     */
    public void mostrar() {
        setVisible(true);
        toFront();
    }
    
    /**
     * Fecha a tela de splash.
     */
    public void fechar() {
        dispose();
    }
    
    /**
     * Define o progresso e mensagem.
     */
    public void setProgresso(int percentual, String mensagem) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percentual);
            lblMensagem.setText(mensagem);
        });
    }
    
    /**
     * Define apenas o progresso.
     */
    public void setProgresso(int percentual) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(percentual));
    }
    
    /**
     * Define apenas a mensagem.
     */
    public void setMensagem(String mensagem) {
        SwingUtilities.invokeLater(() -> lblMensagem.setText(mensagem));
    }
    
    /**
     * Define a imagem de fundo.
     */
    public void setImagemFundo(Image imagem) {
        this.imagemFundo = imagem;
        repaint();
    }
    
    /**
     * Define a cor de fundo (quando sem imagem).
     */
    public void setCorFundo(Color cor) {
        this.corFundo = cor;
        repaint();
    }
    
    /**
     * Atualiza as cores baseado no tema atual do UIManager.
     * Deve ser chamado após mudança de tema para refletir as novas cores.
     */
    public void atualizarCoresDoTema() {
        // Tenta obter a cor de accent do tema atual
        Color accentColor = UIManager.getColor("Component.accentColor");
        if (accentColor == null) {
            accentColor = UIManager.getColor("Button.default.focusColor");
        }
        if (accentColor == null) {
            accentColor = UIManager.getColor("TabbedPane.underlineColor");
        }
        if (accentColor == null) {
            // Tenta cores específicas do FlatLaf
            accentColor = UIManager.getColor("Button.focusedBorderColor");
        }
        if (accentColor == null) {
            accentColor = UIManager.getColor("Button.select");
        }
        if (accentColor == null) {
            accentColor = UIManager.getColor("ProgressBar.foreground");
        }
        if (accentColor == null) {
            // Fallback para branco (como solicitado)
            accentColor = Color.WHITE;
        }
        
        this.corFundo = accentColor;
        
        // Ajusta a cor do texto baseado na luminosidade do fundo
        // Se o fundo for claro, texto escuro; se escuro, texto claro
        double luminosidade = (0.299 * corFundo.getRed() + 0.587 * corFundo.getGreen() + 0.114 * corFundo.getBlue()) / 255;
        this.corTexto = luminosidade > 0.5 ? Color.BLACK : Color.WHITE;
        
        // Atualiza as cores dos labels se já foram criados
        if (lblTitulo != null) {
            lblTitulo.setForeground(corTexto);
            lblMensagem.setForeground(corTexto);
            lblVersao.setForeground(new Color(corTexto.getRed(), corTexto.getGreen(), corTexto.getBlue(), 200));
        }
        
        repaint();
    }
    
    /**
     * Executa tarefas de carregamento com atualização automática.
     */
    public void carregarCom(Consumer<TelaSplash> tarefas) {
        mostrar();
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                tarefas.accept(TelaSplash.this);
                return null;
            }
            
            @Override
            protected void done() {
                fechar();
            }
        };
        
        worker.execute();
    }
    
    /**
     * Simula carregamento com delay.
     */
    public void simularCarregamento(int passos, int delayMs) {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= passos; i++) {
                    int progresso = i * 100 / passos;
                    publish(progresso);
                    Thread.sleep(delayMs);
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int ultimo = chunks.get(chunks.size() - 1);
                setProgresso(ultimo);
            }
            
            @Override
            protected void done() {
                fechar();
            }
        };
        
        mostrar();
        worker.execute();
    }
}

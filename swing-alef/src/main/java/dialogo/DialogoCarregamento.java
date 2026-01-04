package dialogo;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de carregamento com spinner animado.
 * 
 * <p>Mostra um spinner enquanto uma operação está em progresso.
 * Pode ser modal (bloqueia interação) ou não-modal.</p>
 * 
 * <h3>Uso básico:</h3>
 * <pre>{@code
 * // Criar e mostrar
 * DialogoCarregamento loading = new DialogoCarregamento(frame, "Carregando dados...");
 * loading.mostrar();
 * 
 * // Após conclusão
 * loading.fechar();
 * }</pre>
 * 
 * <h3>Com SwingWorker:</h3>
 * <pre>{@code
 * DialogoCarregamento.executar(frame, "Salvando...", () -> {
 *     // Operação demorada
 *     repository.save(entity);
 * });
 * }</pre>
 * 
 * @author alefi
 * @since 1.0
 */
public class DialogoCarregamento extends JDialog {
    
    private JLabel lblMensagem;
    private JLabel lblSpinner;
    private Timer animacaoTimer;
    private int frameIndex = 0;
    
    // Frames do spinner ASCII
    private static final String[] SPINNER_FRAMES = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    
    /**
     * Cria um diálogo de carregamento modal.
     * 
     * @param owner janela pai
     * @param mensagem mensagem a exibir
     */
    public DialogoCarregamento(Window owner, String mensagem) {
        this(owner, mensagem, true);
    }
    
    /**
     * Cria um diálogo de carregamento.
     * 
     * @param owner janela pai
     * @param mensagem mensagem a exibir
     * @param modal se deve bloquear a janela pai
     */
    public DialogoCarregamento(Window owner, String mensagem, boolean modal) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        initComponents(mensagem);
        iniciarAnimacao();
        
        pack();
        setLocationRelativeTo(owner);
    }
    
    private void initComponents(String mensagem) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        panel.setBackground(UIManager.getColor("Panel.background"));
        
        // Spinner
        lblSpinner = new JLabel(SPINNER_FRAMES[0]);
        lblSpinner.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblSpinner.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Mensagem
        lblMensagem = new JLabel(mensagem);
        lblMensagem.setFont(lblMensagem.getFont().deriveFont(14f));
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblSpinner, BorderLayout.CENTER);
        panel.add(lblMensagem, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    private void iniciarAnimacao() {
        animacaoTimer = new Timer(80, e -> {
            frameIndex = (frameIndex + 1) % SPINNER_FRAMES.length;
            lblSpinner.setText(SPINNER_FRAMES[frameIndex]);
        });
        animacaoTimer.start();
    }
    
    /**
     * Atualiza a mensagem exibida.
     * 
     * @param mensagem nova mensagem
     */
    public void setMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
    
    /**
     * Mostra o diálogo.
     */
    public void mostrar() {
        setVisible(true);
    }
    
    /**
     * Fecha o diálogo e para a animação.
     */
    public void fechar() {
        if (animacaoTimer != null) {
            animacaoTimer.stop();
        }
        dispose();
    }
    
    /**
     * Executa uma tarefa exibindo o diálogo de carregamento.
     * 
     * @param owner janela pai
     * @param mensagem mensagem a exibir
     * @param tarefa tarefa a executar
     */
    public static void executar(Window owner, String mensagem, Runnable tarefa) {
        executar(owner, mensagem, tarefa, null, null);
    }
    
    /**
     * Executa uma tarefa com callbacks de sucesso e erro.
     * 
     * @param owner janela pai
     * @param mensagem mensagem a exibir
     * @param tarefa tarefa a executar
     * @param onSucesso callback de sucesso (opcional)
     * @param onErro callback de erro (opcional)
     */
    public static void executar(Window owner, String mensagem, Runnable tarefa, 
                                 Runnable onSucesso, java.util.function.Consumer<Exception> onErro) {
        DialogoCarregamento loading = new DialogoCarregamento(owner, mensagem, false);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Exception erro;
            
            @Override
            protected Void doInBackground() {
                try {
                    tarefa.run();
                } catch (Exception e) {
                    erro = e;
                }
                return null;
            }
            
            @Override
            protected void done() {
                loading.fechar();
                if (erro != null) {
                    if (onErro != null) {
                        onErro.accept(erro);
                    } else {
                        JOptionPane.showMessageDialog(owner, 
                            "Erro: " + erro.getMessage(), 
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (onSucesso != null) {
                    onSucesso.run();
                }
            }
        };
        
        worker.execute();
        loading.mostrar();
    }
}

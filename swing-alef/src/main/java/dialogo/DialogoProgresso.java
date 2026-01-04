package dialogo;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de progresso com barra de progresso.
 * 
 * <p>Mostra o progresso de uma operação com valor numérico.
 * Suporta modo determinado (valor específico) e indeterminado.</p>
 * 
 * <h3>Uso com progresso determinado:</h3>
 * <pre>{@code
 * DialogoProgresso progresso = new DialogoProgresso(frame, "Importando...");
 * progresso.setMaximo(100);
 * progresso.mostrar();
 * 
 * for (int i = 0; i <= 100; i++) {
 *     progresso.setProgresso(i);
 *     progresso.setMensagem("Processando item " + i);
 * }
 * 
 * progresso.fechar();
 * }</pre>
 * 
 * <h3>Uso com progresso indeterminado:</h3>
 * <pre>{@code
 * DialogoProgresso progresso = new DialogoProgresso(frame, "Aguarde...", true);
 * progresso.mostrar();
 * // ... operação
 * progresso.fechar();
 * }</pre>
 * 
 * @author alefi
 * @since 1.0
 */
public class DialogoProgresso extends JDialog {
    
    private JLabel lblMensagem;
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JButton btnCancelar;
    private boolean cancelado = false;
    
    /**
     * Cria um diálogo de progresso determinado.
     * 
     * @param owner janela pai
     * @param titulo título do diálogo
     */
    public DialogoProgresso(Window owner, String titulo) {
        this(owner, titulo, false);
    }
    
    /**
     * Cria um diálogo de progresso.
     * 
     * @param owner janela pai
     * @param titulo título do diálogo
     * @param indeterminado se deve mostrar progresso indeterminado
     */
    public DialogoProgresso(Window owner, String titulo, boolean indeterminado) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
        initComponents(indeterminado);
        
        setSize(400, 150);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents(boolean indeterminado) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Mensagem
        lblMensagem = new JLabel("Processando...");
        lblMensagem.setFont(lblMensagem.getFont().deriveFont(Font.BOLD, 14f));
        
        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(indeterminado);
        progressBar.setStringPainted(!indeterminado);
        progressBar.setPreferredSize(new Dimension(360, 25));
        
        // Status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(lblStatus.getFont().deriveFont(11f));
        lblStatus.setForeground(UIManager.getColor("Label.disabledForeground"));
        
        // Painel central
        JPanel centerPanel = new JPanel(new BorderLayout(5, 8));
        centerPanel.add(lblMensagem, BorderLayout.NORTH);
        centerPanel.add(progressBar, BorderLayout.CENTER);
        centerPanel.add(lblStatus, BorderLayout.SOUTH);
        
        // Botão cancelar (opcional)
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setVisible(false);
        btnCancelar.addActionListener(e -> {
            cancelado = true;
            btnCancelar.setEnabled(false);
            lblStatus.setText("Cancelando...");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(btnCancelar);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    /**
     * Define a mensagem principal.
     * 
     * @param mensagem mensagem a exibir
     */
    public void setMensagem(String mensagem) {
        SwingUtilities.invokeLater(() -> lblMensagem.setText(mensagem));
    }
    
    /**
     * Define o texto de status (abaixo da barra).
     * 
     * @param status texto de status
     */
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> lblStatus.setText(status));
    }
    
    /**
     * Define o valor máximo do progresso.
     * 
     * @param max valor máximo
     */
    public void setMaximo(int max) {
        progressBar.setMaximum(max);
    }
    
    /**
     * Define o progresso atual.
     * 
     * @param valor valor atual (0 a máximo)
     */
    public void setProgresso(int valor) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(valor);
            progressBar.setString(valor + "%");
        });
    }
    
    /**
     * Define o progresso como porcentagem (0.0 a 1.0).
     * 
     * @param porcentagem valor entre 0.0 e 1.0
     */
    public void setProgressoPorcentagem(double porcentagem) {
        int valor = (int) (porcentagem * progressBar.getMaximum());
        setProgresso(valor);
    }
    
    /**
     * Incrementa o progresso.
     * 
     * @param incremento valor a incrementar
     */
    public void incrementar(int incremento) {
        setProgresso(progressBar.getValue() + incremento);
    }
    
    /**
     * Habilita/desabilita o botão de cancelar.
     * 
     * @param cancelavel se permite cancelamento
     */
    public void setCancelavel(boolean cancelavel) {
        btnCancelar.setVisible(cancelavel);
    }
    
    /**
     * Verifica se o usuário solicitou cancelamento.
     * 
     * @return true se foi cancelado
     */
    public boolean isCancelado() {
        return cancelado;
    }
    
    /**
     * Mostra o diálogo.
     */
    public void mostrar() {
        setVisible(true);
    }
    
    /**
     * Fecha o diálogo.
     */
    public void fechar() {
        dispose();
    }
    
    /**
     * Define o modo indeterminado.
     * 
     * @param indeterminado true para modo indeterminado
     */
    public void setIndeterminado(boolean indeterminado) {
        progressBar.setIndeterminate(indeterminado);
        progressBar.setStringPainted(!indeterminado);
    }
}

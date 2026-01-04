package componente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Botão que mostra estado de carregamento durante processamento.
 * 
 * <p>Enquanto está processando, o botão mostra um spinner animado
 * e fica desabilitado para evitar cliques duplos.</p>
 * 
 * <h3>Uso básico:</h3>
 * <pre>{@code
 * BotaoCarregamento btn = new BotaoCarregamento("Salvar");
 * btn.addActionListener(e -> {
 *     // A ação será executada em background automaticamente
 *     repository.save(entity);
 * });
 * }</pre>
 * 
 * <h3>Controle manual:</h3>
 * <pre>{@code
 * BotaoCarregamento btn = new BotaoCarregamento("Processar");
 * btn.setAutoLoading(false); // Controle manual
 * 
 * btn.addActionListener(e -> {
 *     btn.iniciarCarregamento("Processando...");
 *     
 *     // Fazer algo...
 *     
 *     btn.finalizarCarregamento();
 * });
 * }</pre>
 * 
 * @author alefi
 * @since 1.0
 */
public class BotaoCarregamento extends JButton {
    
    private String textoOriginal;
    private Icon iconeOriginal;
    private boolean carregando = false;
    private boolean autoLoading = true;
    private Timer animacaoTimer;
    private int frameIndex = 0;
    
    // Frames do spinner
    private static final String[] SPINNER_FRAMES = {"◐", "◓", "◑", "◒"};
    
    /**
     * Cria um botão de carregamento.
     * 
     * @param texto texto do botão
     */
    public BotaoCarregamento(String texto) {
        super(texto);
        this.textoOriginal = texto;
    }
    
    /**
     * Cria um botão de carregamento com ícone.
     * 
     * @param texto texto do botão
     * @param icon ícone do botão
     */
    public BotaoCarregamento(String texto, Icon icon) {
        super(texto, icon);
        this.textoOriginal = texto;
        this.iconeOriginal = icon;
    }
    
    /**
     * Define se o loading automático está ativado.
     * 
     * <p>Quando ativado, o botão mostra loading automaticamente
     * ao executar o actionListener.</p>
     * 
     * @param auto true para ativar loading automático
     */
    public void setAutoLoading(boolean auto) {
        this.autoLoading = auto;
    }
    
    /**
     * Inicia o estado de carregamento.
     */
    public void iniciarCarregamento() {
        iniciarCarregamento(null);
    }
    
    /**
     * Inicia o estado de carregamento com mensagem customizada.
     * 
     * @param mensagem mensagem a exibir (null para mostrar spinner apenas)
     */
    public void iniciarCarregamento(String mensagem) {
        if (carregando) return;
        
        carregando = true;
        setEnabled(false);
        
        // Guarda estado original
        if (textoOriginal == null) {
            textoOriginal = getText();
        }
        if (iconeOriginal == null) {
            iconeOriginal = getIcon();
        }
        
        setIcon(null);
        
        // Inicia animação
        animacaoTimer = new Timer(150, e -> {
            frameIndex = (frameIndex + 1) % SPINNER_FRAMES.length;
            String spinner = SPINNER_FRAMES[frameIndex];
            setText(mensagem != null ? spinner + " " + mensagem : spinner);
        });
        animacaoTimer.start();
        
        // Mostra primeiro frame imediatamente
        String spinner = SPINNER_FRAMES[0];
        setText(mensagem != null ? spinner + " " + mensagem : spinner);
    }
    
    /**
     * Finaliza o estado de carregamento.
     */
    public void finalizarCarregamento() {
        if (!carregando) return;
        
        carregando = false;
        
        if (animacaoTimer != null) {
            animacaoTimer.stop();
            animacaoTimer = null;
        }
        
        setText(textoOriginal);
        setIcon(iconeOriginal);
        setEnabled(true);
    }
    
    /**
     * Verifica se está carregando.
     * 
     * @return true se está em estado de carregamento
     */
    public boolean isCarregando() {
        return carregando;
    }
    
    @Override
    public void addActionListener(ActionListener l) {
        if (autoLoading) {
            // Envolve o listener para executar em background
            super.addActionListener(e -> {
                if (carregando) return;
                
                iniciarCarregamento();
                
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        l.actionPerformed(e);
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                        finalizarCarregamento();
                    }
                };
                worker.execute();
            });
        } else {
            super.addActionListener(l);
        }
    }
    
    /**
     * Adiciona um listener que será executado diretamente (sem auto-loading).
     * 
     * @param l listener
     */
    public void addActionListenerDireto(ActionListener l) {
        super.addActionListener(l);
    }
}

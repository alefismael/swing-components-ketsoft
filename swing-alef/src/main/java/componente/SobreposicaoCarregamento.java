package componente;

import javax.swing.*;
import java.awt.*;

/**
 * Sobreposição de carregamento para cobrir componentes durante operações longas.
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * SobreposicaoCarregamento overlay = new SobreposicaoCarregamento(meuPainel, "Carregando dados...");
 * overlay.mostrar();
 * 
 * // ... operação longa ...
 * 
 * overlay.esconder();
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class SobreposicaoCarregamento extends JComponent {
    
    private final JComponent alvo;
    private final String mensagem;

    /**
     * Cria uma sobreposição de carregamento.
     * 
     * @param alvo componente a ser coberto
     * @param mensagem mensagem a exibir (ou null para "Carregando...")
     */
    public SobreposicaoCarregamento(JComponent alvo, String mensagem) {
        this.alvo = alvo;
        this.mensagem = mensagem != null ? mensagem : "Carregando...";
        setOpaque(false);
    }
    
    /**
     * Cria uma sobreposição de carregamento com mensagem padrão.
     * 
     * @param alvo componente a ser coberto
     */
    public SobreposicaoCarregamento(JComponent alvo) {
        this(alvo, null);
    }

    /**
     * Exibe a sobreposição sobre o componente alvo.
     */
    public void mostrar() {
        RootPaneContainer rpc = (RootPaneContainer) SwingUtilities.getWindowAncestor(alvo);
        if (rpc == null) return;
        JRootPane root = rpc.getRootPane();
        root.setGlassPane(this);
        setVisible(true);
        repaint();
    }

    /**
     * Esconde a sobreposição.
     */
    public void esconder() {
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Fundo semi-transparente
        g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Texto centralizado
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        Font font = g2.getFont().deriveFont(Font.BOLD, 16f);
        g2.setFont(font);
        
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(mensagem);
        int x = (getWidth() - w) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        
        // Sombra do texto
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(mensagem, x + 2, y + 2);
        
        // Texto
        g2.setColor(Color.WHITE);
        g2.drawString(mensagem, x, y);
        
        g2.dispose();
    }
}

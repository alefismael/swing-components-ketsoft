package componente;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Visualizador de imagens com zoom e navegação.
 * 
 * <p>Permite visualizar imagens com controles de zoom,
 * arrastar para mover e ajuste automático.</p>
 * 
 * <h3>Controles:</h3>
 * <ul>
 *   <li>Scroll do mouse: zoom in/out</li>
 *   <li>Arrastar: mover imagem</li>
 *   <li>Duplo clique: ajustar à janela</li>
 * </ul>
 * 
 * @author alefi
 * @since 1.1
 */
public class VisualizadorImagem extends JPanel {
    
    private BufferedImage imagem;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point dragStart;
    
    private static final double ZOOM_MIN = 0.1;
    private static final double ZOOM_MAX = 5.0;
    private static final double ZOOM_FATOR = 1.15;
    
    private JLabel lblStatus;
    private JPanel painelControles;
    private PainelImagem painelImagem;
    
    public VisualizadorImagem() {
        this(null);
    }
    
    public VisualizadorImagem(BufferedImage imagem) {
        setLayout(new BorderLayout());
        
        // Painel de visualização
        painelImagem = new PainelImagem();
        add(painelImagem, BorderLayout.CENTER);
        
        // Controles
        criarControles();
        add(painelControles, BorderLayout.SOUTH);
        
        if (imagem != null) {
            setImagem(imagem);
        }
    }
    
    private void criarControles() {
        painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelControles.setBackground(new Color(50, 50, 50));
        
        JButton btnZoomOut = criarBotao("−", "Reduzir", e -> zoomOut());
        JButton btnAjustar = criarBotao("⊡", "Ajustar à janela", e -> ajustarAJanela());
        JButton btnZoom100 = criarBotao("1:1", "Tamanho real", e -> zoom100());
        JButton btnZoomIn = criarBotao("+", "Ampliar", e -> zoomIn());
        
        lblStatus = new JLabel("Nenhuma imagem");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(12f));
        
        painelControles.add(btnZoomOut);
        painelControles.add(btnAjustar);
        painelControles.add(btnZoom100);
        painelControles.add(btnZoomIn);
        painelControles.add(Box.createHorizontalStrut(20));
        painelControles.add(lblStatus);
    }
    
    private JButton criarBotao(String texto, String tooltip, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setToolTipText(tooltip);
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(45, 28));
        btn.addActionListener(acao);
        return btn;
    }
    
    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
        if (imagem != null) {
            // Ajustar após o componente estar visível
            SwingUtilities.invokeLater(() -> {
                ajustarAJanela();
                painelImagem.repaint();
            });
        }
        atualizarStatus();
    }
    
    public void setImagem(File arquivo) throws IOException {
        setImagem(ImageIO.read(arquivo));
    }
    
    public BufferedImage getImagem() {
        return imagem;
    }
    
    public void zoomIn() {
        if (imagem == null) return;
        double novoZoom = zoom * ZOOM_FATOR;
        if (novoZoom <= ZOOM_MAX) {
            zoom = novoZoom;
            centralizarImagem();
            atualizarStatus();
            painelImagem.repaint();
        }
    }
    
    public void zoomOut() {
        if (imagem == null) return;
        double novoZoom = zoom / ZOOM_FATOR;
        if (novoZoom >= ZOOM_MIN) {
            zoom = novoZoom;
            centralizarImagem();
            atualizarStatus();
            painelImagem.repaint();
        }
    }
    
    public void zoom100() {
        if (imagem == null) return;
        zoom = 1.0;
        centralizarImagem();
        atualizarStatus();
        painelImagem.repaint();
    }
    
    public void ajustarAJanela() {
        if (imagem == null) return;
        
        int w = painelImagem.getWidth();
        int h = painelImagem.getHeight();
        
        if (w <= 0 || h <= 0) {
            zoom = 1.0;
            offsetX = 0;
            offsetY = 0;
            return;
        }
        
        double zoomX = (double) (w - 20) / imagem.getWidth();
        double zoomY = (double) (h - 20) / imagem.getHeight();
        zoom = Math.min(zoomX, zoomY);
        zoom = Math.max(ZOOM_MIN, Math.min(zoom, 1.0)); // Não aumentar além de 100%
        
        centralizarImagem();
        atualizarStatus();
        painelImagem.repaint();
    }
    
    private void centralizarImagem() {
        if (imagem == null) return;
        
        int w = painelImagem.getWidth();
        int h = painelImagem.getHeight();
        
        int imgW = (int) (imagem.getWidth() * zoom);
        int imgH = (int) (imagem.getHeight() * zoom);
        
        offsetX = (w - imgW) / 2;
        offsetY = (h - imgH) / 2;
    }
    
    private void atualizarStatus() {
        if (imagem != null) {
            int pct = (int) (zoom * 100);
            lblStatus.setText(imagem.getWidth() + " × " + imagem.getHeight() + " px | " + pct + "%");
        } else {
            lblStatus.setText("Nenhuma imagem");
        }
    }
    
    /**
     * Abre um diálogo para visualizar a imagem.
     */
    public static void mostrar(Window owner, BufferedImage imagem, String titulo) {
        JDialog dialog = new JDialog(owner instanceof Frame ? (Frame) owner : null, titulo, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        VisualizadorImagem viewer = new VisualizadorImagem();
        dialog.add(viewer);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(owner);
        
        // Definir imagem após diálogo estar visível para calcular tamanho correto
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                viewer.setImagem(imagem);
            }
        });
        
        dialog.setVisible(true);
    }
    
    public static void mostrar(Window owner, File arquivo, String titulo) throws IOException {
        mostrar(owner, ImageIO.read(arquivo), titulo);
    }
    
    // Painel interno para desenhar a imagem
    private class PainelImagem extends JPanel {
        
        PainelImagem() {
            setBackground(new Color(40, 40, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Mouse wheel - zoom
            addMouseWheelListener(e -> {
                if (imagem == null) return;
                
                Point mouse = e.getPoint();
                double fatorAnterior = zoom;
                
                if (e.getWheelRotation() < 0) {
                    zoom = Math.min(zoom * ZOOM_FATOR, ZOOM_MAX);
                } else {
                    zoom = Math.max(zoom / ZOOM_FATOR, ZOOM_MIN);
                }
                
                // Zoom centrado no mouse
                double fator = zoom / fatorAnterior;
                offsetX = (int) (mouse.x - (mouse.x - offsetX) * fator);
                offsetY = (int) (mouse.y - (mouse.y - offsetY) * fator);
                
                atualizarStatus();
                repaint();
            });
            
            // Drag para mover
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragStart = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        ajustarAJanela();
                    }
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragStart != null && imagem != null) {
                        offsetX += e.getX() - dragStart.x;
                        offsetY += e.getY() - dragStart.y;
                        dragStart = e.getPoint();
                        repaint();
                    }
                }
            });
            
            // Ajustar ao redimensionar
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (imagem != null) {
                        centralizarImagem();
                        repaint();
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (imagem == null) {
                g.setColor(Color.GRAY);
                String msg = "Nenhuma imagem carregada";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(msg)) / 2;
                int y = getHeight() / 2;
                g.drawString(msg, x, y);
                return;
            }
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int imgW = (int) (imagem.getWidth() * zoom);
            int imgH = (int) (imagem.getHeight() * zoom);
            
            // Fundo quadriculado para transparência
            desenharFundoTransparencia(g2d, offsetX, offsetY, imgW, imgH);
            
            // Imagem
            g2d.drawImage(imagem, offsetX, offsetY, imgW, imgH, null);
            
            // Borda sutil
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawRect(offsetX, offsetY, imgW - 1, imgH - 1);
            
            g2d.dispose();
        }
        
        private void desenharFundoTransparencia(Graphics2D g, int x, int y, int w, int h) {
            int tamanho = 10;
            Color cor1 = new Color(200, 200, 200);
            Color cor2 = new Color(255, 255, 255);
            
            for (int i = 0; i < w; i += tamanho) {
                for (int j = 0; j < h; j += tamanho) {
                    g.setColor(((i + j) / tamanho) % 2 == 0 ? cor1 : cor2);
                    g.fillRect(x + i, y + j, 
                        Math.min(tamanho, w - i), 
                        Math.min(tamanho, h - j));
                }
            }
        }
    }
}

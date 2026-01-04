package componente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de notificações toast moderno com empilhamento.
 * 
 * <h3>Tipos disponíveis:</h3>
 * <ul>
 *   <li>INFO: Informação geral (azul)</li>
 *   <li>SUCCESS: Operação bem-sucedida (verde)</li>
 *   <li>WARNING: Aviso (amarelo)</li>
 *   <li>ERROR: Erro (vermelho)</li>
 * </ul>
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Empilhamento automático de múltiplos toasts</li>
 *   <li>Animação de entrada/saída suave</li>
 *   <li>Botão fechar com hover arredondado</li>
 *   <li>Posicionamento no canto superior direito</li>
 *   <li>Bordas arredondadas apenas na esquerda (quando no canto direito)</li>
 * </ul>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * Toast.success(parent, "Cliente salvo com sucesso!");
 * Toast.error(parent, "Erro ao conectar ao banco");
 * Toast.warning(parent, "Atenção: dados não salvos");
 * Toast.info(parent, "Processando...");
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public final class Toast {
    
    private Toast() {}
    
    /**
     * Tipos de toast com cores e ícones.
     */
    public enum Type {
        INFO("ℹ️", new Color(59, 130, 246), new Color(239, 246, 255)),      // Azul
        SUCCESS("✅", new Color(34, 197, 94), new Color(240, 253, 244)),     // Verde
        WARNING("⚠️", new Color(234, 179, 8), new Color(254, 252, 232)),    // Amarelo
        ERROR("❌", new Color(239, 68, 68), new Color(254, 242, 242));       // Vermelho
        
        final String icon;
        final Color borderColor;
        final Color backgroundColor;
        
        Type(String icon, Color borderColor, Color backgroundColor) {
            this.icon = icon;
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
        }
    }
    
    /**
     * Posições do toast na tela.
     */
    public enum Position {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        CENTER
    }
    
    // Duração padrão
    private static final int DEFAULT_DURATION = 3000;
    private static Position defaultPosition = Position.TOP_RIGHT;
    
    // Lista de toasts ativos para gerenciar empilhamento
    private static final List<ToastInfo> activeToasts = new ArrayList<>();
    private static final int TOAST_GAP = 10; // Espaço entre toasts
    private static final int MARGIN_TOP = 70; // Abaixo da toolbar/abas
    private static final int MARGIN_RIGHT = 0; // Colado na direita
    
    /**
     * Informações de um toast ativo.
     */
    private static class ToastInfo {
        JWindow window;
        Window parent;
        int height;
        Timer closeTimer;
        
        ToastInfo(JWindow window, Window parent, int height, Timer closeTimer) {
            this.window = window;
            this.parent = parent;
            this.height = height;
            this.closeTimer = closeTimer;
        }
    }
    
    /**
     * Define a posição padrão para todos os toasts.
     * @param position posição padrão
     */
    public static void setDefaultPosition(Position position) {
        defaultPosition = position;
    }
    
    // ==================== MÉTODOS DE CONVENIÊNCIA ====================
    
    /**
     * Exibe um toast de informação.
     * @param parent componente pai
     * @param message mensagem a exibir
     */
    public static void info(Component parent, String message) {
        show(parent, message, Type.INFO, defaultPosition, DEFAULT_DURATION);
    }
    
    /**
     * Exibe um toast de sucesso.
     * @param parent componente pai
     * @param message mensagem a exibir
     */
    public static void success(Component parent, String message) {
        show(parent, message, Type.SUCCESS, defaultPosition, DEFAULT_DURATION);
    }
    
    /**
     * Exibe um toast de aviso.
     * @param parent componente pai
     * @param message mensagem a exibir
     */
    public static void warning(Component parent, String message) {
        show(parent, message, Type.WARNING, defaultPosition, DEFAULT_DURATION);
    }
    
    /**
     * Exibe um toast de erro.
     * @param parent componente pai
     * @param message mensagem a exibir
     */
    public static void error(Component parent, String message) {
        show(parent, message, Type.ERROR, defaultPosition, DEFAULT_DURATION);
    }
    
    // ==================== MÉTODO PRINCIPAL ====================
    
    /**
     * Exibe um toast com configurações completas.
     * @param parent componente pai
     * @param message mensagem
     * @param type tipo do toast
     * @param position posição na tela
     * @param durationMs duração em milissegundos
     */
    public static void show(Component parent, String message, Type type, Position position, int durationMs) {
        // Obter a janela - se parent já é Window, usar diretamente
        Window window;
        if (parent instanceof Window) {
            window = (Window) parent;
        } else if (parent != null) {
            window = SwingUtilities.getWindowAncestor(parent);
        } else {
            window = null;
        }
        
        JWindow toast = new JWindow(window);
        toast.setBackground(new Color(0, 0, 0, 0));
        
        // Determinar se deve arredondar apenas esquerda (quando no canto direito)
        boolean rightAligned = (position == Position.TOP_RIGHT || position == Position.BOTTOM_RIGHT);
        
        // Painel com cantos arredondados
        JPanel panel = createToastPanel(type, rightAligned);
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));
        
        // Ícone
        JLabel iconLabel = new JLabel(type.icon);
        iconLabel.setFont(iconLabel.getFont().deriveFont(16f));
        panel.add(iconLabel, BorderLayout.WEST);
        
        // Mensagem
        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setForeground(new Color(30, 30, 30));
        messageLabel.setFont(messageLabel.getFont().deriveFont(13f));
        panel.add(messageLabel, BorderLayout.CENTER);
        
        // Botão fechar com hover arredondado
        JPanel closePanel = createCloseButton(toast);
        panel.add(closePanel, BorderLayout.EAST);
        
        toast.getContentPane().add(panel);
        toast.pack();
        
        // Timer para fechar
        Timer closeTimer = new Timer(durationMs, e -> removeToast(toast));
        closeTimer.setRepeats(false);
        
        // Registrar toast ativo
        ToastInfo toastInfo = new ToastInfo(toast, window, toast.getHeight(), closeTimer);
        
        synchronized (activeToasts) {
            activeToasts.add(toastInfo);
        }
        
        // Posicionar considerando outros toasts
        positionToast(toast, window, position);
        
        // Mostrar com fade in
        toast.setOpacity(0f);
        toast.setVisible(true);
        fadeIn(toast);
        
        // Iniciar timer
        closeTimer.start();
    }
    
    /**
     * Cria o painel do toast com bordas arredondadas.
     */
    private static JPanel createToastPanel(Type type, boolean rightAligned) {
        return new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int arc = 16;
                
                // Criar shape com bordas arredondadas apenas na esquerda se rightAligned
                Shape shape;
                Shape borderShape;
                if (rightAligned) {
                    shape = createLeftRoundedRect(0, 0, w, h, arc);
                    borderShape = createLeftRoundedRect(1, 1, w - 1, h - 2, arc);
                } else {
                    shape = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
                    borderShape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc);
                }
                
                // Fundo
                g2.setColor(type.backgroundColor);
                g2.fill(shape);
                
                // Borda
                g2.setColor(type.borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.draw(borderShape);
                
                g2.dispose();
            }
        };
    }
    
    /**
     * Cria um retângulo com cantos arredondados apenas na esquerda.
     */
    private static Shape createLeftRoundedRect(int x, int y, int w, int h, int arc) {
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Float();
        
        // Começar do canto superior esquerdo (arredondado)
        path.moveTo(x + arc, y);
        
        // Linha superior até a direita
        path.lineTo(x + w, y);
        
        // Lado direito (reto)
        path.lineTo(x + w, y + h);
        
        // Linha inferior até a esquerda
        path.lineTo(x + arc, y + h);
        
        // Canto inferior esquerdo (arredondado)
        path.quadTo(x, y + h, x, y + h - arc);
        
        // Lado esquerdo
        path.lineTo(x, y + arc);
        
        // Canto superior esquerdo (arredondado)
        path.quadTo(x, y, x + arc, y);
        
        path.closePath();
        return path;
    }
    
    /**
     * Cria o botão de fechar com hover arredondado.
     */
    private static JPanel createCloseButton(JWindow toast) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        
        JPanel closeBtn = new JPanel() {
            private boolean hovered = false;
            
            {
                setOpaque(false);
                setPreferredSize(new Dimension(24, 24));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        removeToast(toast);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Fundo arredondado no hover
                if (hovered) {
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fillRoundRect(x + 2, y + 2, size - 4, size - 4, 8, 8);
                }
                
                // Desenhar X
                g2.setColor(hovered ? new Color(60, 60, 60) : new Color(140, 140, 140));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int padding = 7;
                g2.drawLine(x + padding, y + padding, x + size - padding, y + size - padding);
                g2.drawLine(x + size - padding, y + padding, x + padding, y + size - padding);
                
                g2.dispose();
            }
        };
        
        wrapper.add(closeBtn, BorderLayout.CENTER);
        return wrapper;
    }
    
    /**
     * Remove um toast e reorganiza os outros.
     */
    private static void removeToast(JWindow toast) {
        synchronized (activeToasts) {
            ToastInfo toRemove = null;
            int removedIndex = -1;
            
            for (int i = 0; i < activeToasts.size(); i++) {
                if (activeToasts.get(i).window == toast) {
                    toRemove = activeToasts.get(i);
                    removedIndex = i;
                    break;
                }
            }
            
            if (toRemove != null) {
                toRemove.closeTimer.stop();
                activeToasts.remove(removedIndex);
                
                // Animar saída
                fadeOut(toast, () -> {
                    // Reorganizar toasts restantes
                    reorganizeToasts();
                });
            }
        }
    }
    
    /**
     * Reorganiza os toasts após um ser removido.
     */
    private static void reorganizeToasts() {
        synchronized (activeToasts) {
            for (int i = 0; i < activeToasts.size(); i++) {
                ToastInfo info = activeToasts.get(i);
                
                // Calcular nova posição Y
                int newY = calculateYPosition(i, info.parent);
                
                // Animar para nova posição
                animateToPosition(info.window, info.window.getX(), newY);
            }
        }
    }
    
    /**
     * Calcula a posição Y para um toast baseado no índice.
     */
    private static int calculateYPosition(int index, Window window) {
        int baseY;
        
        if (window != null && window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            try {
                Container contentPane = frame.getContentPane();
                Point screenLocation = contentPane.getLocationOnScreen();
                baseY = screenLocation.y;
            } catch (Exception e) {
                baseY = window.getY();
            }
        } else if (window != null) {
            baseY = window.getY();
        } else {
            baseY = 0;
        }
        
        int y = baseY + MARGIN_TOP;
        
        synchronized (activeToasts) {
            for (int i = 0; i < index && i < activeToasts.size(); i++) {
                y += activeToasts.get(i).height + TOAST_GAP;
            }
        }
        
        return y;
    }
    
    /**
     * Anima o toast para uma nova posição.
     */
    private static void animateToPosition(JWindow toast, int targetX, int targetY) {
        int startY = toast.getY();
        int deltaY = targetY - startY;
        
        if (deltaY == 0) return;
        
        Timer timer = new Timer(16, null);
        final int[] step = {0};
        final int totalSteps = 10;
        
        timer.addActionListener(e -> {
            step[0]++;
            float progress = (float) step[0] / totalSteps;
            // Easing suave
            progress = 1 - (1 - progress) * (1 - progress);
            
            int currentY = startY + (int) (deltaY * progress);
            toast.setLocation(targetX, currentY);
            
            if (step[0] >= totalSteps) {
                ((Timer) e.getSource()).stop();
                toast.setLocation(targetX, targetY);
            }
        });
        timer.start();
    }
    
    /**
     * Método legado para compatibilidade.
     * @param parent componente pai
     * @param message mensagem
     */
    public static void show(Component parent, String message) {
        info(parent, message);
    }
    
    /**
     * Método legado para compatibilidade.
     * @param parent componente pai
     * @param message mensagem
     * @param durationMs duração em ms
     */
    public static void show(Component parent, String message, int durationMs) {
        show(parent, message, Type.INFO, defaultPosition, durationMs);
    }
    
    // ==================== ANIMAÇÕES ====================
    
    private static void fadeIn(JWindow toast) {
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float opacity = toast.getOpacity();
            if (opacity < 1f) {
                toast.setOpacity(Math.min(1f, opacity + 0.15f));
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }
    
    private static void fadeOut(JWindow toast, Runnable onComplete) {
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float opacity = toast.getOpacity();
            if (opacity > 0f) {
                toast.setOpacity(Math.max(0f, opacity - 0.15f));
            } else {
                ((Timer) e.getSource()).stop();
                toast.dispose();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        timer.start();
    }
    
    // ==================== POSICIONAMENTO ====================
    
    private static void positionToast(JWindow toast, Window window, Position position) {
        int x, y;
        
        Rectangle bounds;
        Point screenLocation;
        
        if (window != null) {
            // Usar a área interna da janela (content pane) para posicionamento correto
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                Container contentPane = frame.getContentPane();
                screenLocation = contentPane.getLocationOnScreen();
                bounds = new Rectangle(screenLocation.x, screenLocation.y, 
                    contentPane.getWidth(), contentPane.getHeight());
            } else {
                bounds = window.getBounds();
            }
        } else {
            bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        }
        
        // Calcular índice deste toast
        int index;
        synchronized (activeToasts) {
            index = activeToasts.size() - 1; // Último adicionado
        }
        
        switch (position) {
            case TOP_LEFT:
                x = bounds.x + 10;
                y = calculateYPosition(index, window);
                break;
            case TOP_CENTER:
                x = bounds.x + (bounds.width - toast.getWidth()) / 2;
                y = calculateYPosition(index, window);
                break;
            case TOP_RIGHT:
                x = bounds.x + bounds.width - toast.getWidth() - MARGIN_RIGHT;
                y = calculateYPosition(index, window);
                break;
            case BOTTOM_LEFT:
                x = bounds.x + 10;
                y = bounds.y + bounds.height - toast.getHeight() - 10;
                break;
            case BOTTOM_CENTER:
                x = bounds.x + (bounds.width - toast.getWidth()) / 2;
                y = bounds.y + bounds.height - toast.getHeight() - 10;
                break;
            case CENTER:
                x = bounds.x + (bounds.width - toast.getWidth()) / 2;
                y = bounds.y + (bounds.height - toast.getHeight()) / 2;
                break;
            case BOTTOM_RIGHT:
            default:
                x = bounds.x + bounds.width - toast.getWidth() - MARGIN_RIGHT;
                y = bounds.y + bounds.height - toast.getHeight() - 10;
                break;
        }
        
        toast.setLocation(x, y);
    }
}

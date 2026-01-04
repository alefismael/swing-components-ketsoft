package componente;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Painel para seleção de temas FlatLaf.
 * Inclui busca e visualização organizada por categorias.
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Em um JFrame
 * PainelTemas painelTemas = new PainelTemas(frame);
 * tabbedPane.addTab("Temas", painelTemas);
 * 
 * // Com callback para salvar preferência
 * painelTemas.setOnTemaAlterado(nomeTema -> {
 *     // Salvar no banco de dados
 *     usuarioService.salvarTemaPreferido(nomeTema);
 * });
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class PainelTemas extends JPanel {
    
    private final JTextField campoBusca;
    private final JPanel painelTemas;
    private final JScrollPane scrollPane;
    private final List<InfoTema> todosTemas;
    private final Window janelaParent;
    private JLabel labelTemaAtual;
    private String temaAtual;
    private Consumer<String> onTemaAlterado;
    
    /**
     * Informações de um tema.
     */
    private static class InfoTema {
        String nome;
        String categoria;
        String nomeClasse;
        boolean escuro;
        
        InfoTema(String nome, String categoria, String nomeClasse, boolean escuro) {
            this.nome = nome;
            this.categoria = categoria;
            this.nomeClasse = nomeClasse;
            this.escuro = escuro;
        }
    }
    
    /**
     * Cria um PainelTemas.
     * @param janelaParent Janela pai para exibir toasts
     */
    public PainelTemas(Window janelaParent) {
        this.janelaParent = janelaParent;
        this.todosTemas = new ArrayList<>();
        this.temaAtual = UIManager.getLookAndFeel().getName();
        
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new BorderLayout(10, 0));
        painelBusca.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel labelBusca = new JLabel("🔍 Buscar tema:");
        campoBusca = new JTextField();
        campoBusca.putClientProperty("JTextField.placeholderText", "Digite para filtrar...");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarTemas(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarTemas(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarTemas(); }
        });
        
        painelBusca.add(labelBusca, BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        
        // Info do tema atual
        labelTemaAtual = new JLabel("Tema atual: " + temaAtual);
        labelTemaAtual.setFont(labelTemaAtual.getFont().deriveFont(Font.BOLD));
        painelBusca.add(labelTemaAtual, BorderLayout.SOUTH);
        
        add(painelBusca, BorderLayout.NORTH);
        
        // Painel de temas
        painelTemas = new JPanel();
        painelTemas.setLayout(new BoxLayout(painelTemas, BoxLayout.Y_AXIS));
        
        scrollPane = new JScrollPane(painelTemas);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Carregar temas
        carregarTemas();
        exibirTemas(todosTemas);
    }
    
    /**
     * Carrega todos os temas disponíveis.
     */
    private void carregarTemas() {
        // === CORE FlatLaf ===
        todosTemas.add(new InfoTema("FlatLaf Light", "Core", "com.formdev.flatlaf.FlatLightLaf", false));
        todosTemas.add(new InfoTema("FlatLaf Dark", "Core", "com.formdev.flatlaf.FlatDarkLaf", true));
        todosTemas.add(new InfoTema("FlatLaf IntelliJ", "Core", "com.formdev.flatlaf.FlatIntelliJLaf", false));
        todosTemas.add(new InfoTema("FlatLaf Darcula", "Core", "com.formdev.flatlaf.FlatDarculaLaf", true));
        
        // === macOS Themes ===
        todosTemas.add(new InfoTema("macOS Light", "macOS", "com.formdev.flatlaf.themes.FlatMacLightLaf", false));
        todosTemas.add(new InfoTema("macOS Dark", "macOS", "com.formdev.flatlaf.themes.FlatMacDarkLaf", true));
        
        // === IntelliJ Themes - Light ===
        todosTemas.add(new InfoTema("Arc", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatArcIJTheme", false));
        todosTemas.add(new InfoTema("Arc Orange", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme", false));
        todosTemas.add(new InfoTema("Cyan Light", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme", false));
        todosTemas.add(new InfoTema("Gray", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme", false));
        todosTemas.add(new InfoTema("Light Flat", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme", false));
        todosTemas.add(new InfoTema("Solarized Light", "IntelliJ Light", "com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme", false));
        
        // === IntelliJ Themes - Dark ===
        todosTemas.add(new InfoTema("Gruvbox Dark Hard", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme", true));
        todosTemas.add(new InfoTema("Gruvbox Dark Medium", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme", true));
        todosTemas.add(new InfoTema("Gruvbox Dark Soft", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme", true));
        todosTemas.add(new InfoTema("Hiberbee Dark", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme", true));
        todosTemas.add(new InfoTema("High Contrast", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme", true));
        todosTemas.add(new InfoTema("Material Design Dark", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme", true));
        todosTemas.add(new InfoTema("Monocai", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme", true));
        todosTemas.add(new InfoTema("Nord", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatNordIJTheme", true));
        todosTemas.add(new InfoTema("One Dark", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme", true));
        todosTemas.add(new InfoTema("Solarized Dark", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme", true));
        todosTemas.add(new InfoTema("Spacegray", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme", true));
        todosTemas.add(new InfoTema("Vuesion", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme", true));
        todosTemas.add(new InfoTema("Xcode Dark", "IntelliJ Dark", "com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme", true));
        
        // === Arc Themes ===
        todosTemas.add(new InfoTema("Arc Dark", "Arc", "com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme", true));
        todosTemas.add(new InfoTema("Arc Dark Orange", "Arc", "com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme", true));
        
        // === Carbon Themes ===
        todosTemas.add(new InfoTema("Carbon", "Carbon", "com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme", true));
        
        // === Cobalt Themes ===
        todosTemas.add(new InfoTema("Cobalt 2", "Cobalt", "com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme", true));
        
        // === Dark Flat ===
        todosTemas.add(new InfoTema("Dark Flat", "Flat", "com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme", true));
        
        // === Dark Purple ===
        todosTemas.add(new InfoTema("Dark Purple", "Purple", "com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme", true));
        
        // === Dracula ===
        todosTemas.add(new InfoTema("Dracula", "Dracula", "com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme", true));
        
        // === Material Theme UI Lite ===
        todosTemas.add(new InfoTema("Material Arc Dark", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme", true));
        todosTemas.add(new InfoTema("Material Atom One Dark", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme", true));
        todosTemas.add(new InfoTema("Material Atom One Light", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme", false));
        todosTemas.add(new InfoTema("Material Dracula", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme", true));
        todosTemas.add(new InfoTema("Material GitHub", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme", false));
        todosTemas.add(new InfoTema("Material GitHub Dark", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme", true));
        todosTemas.add(new InfoTema("Material Lighter", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme", false));
        todosTemas.add(new InfoTema("Material Darker", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme", true));
        todosTemas.add(new InfoTema("Material Deep Ocean", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme", true));
        todosTemas.add(new InfoTema("Material Oceanic", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme", true));
        todosTemas.add(new InfoTema("Material Palenight", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme", true));
        todosTemas.add(new InfoTema("Material Monokai Pro", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme", true));
        todosTemas.add(new InfoTema("Material Moonlight", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme", true));
        todosTemas.add(new InfoTema("Material Night Owl", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme", true));
        todosTemas.add(new InfoTema("Material Solarized Dark", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme", true));
        todosTemas.add(new InfoTema("Material Solarized Light", "Material", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme", false));
    }
    
    /**
     * Filtra temas baseado na busca.
     */
    private void filtrarTemas() {
        String filtro = campoBusca.getText().toLowerCase().trim();
        
        if (filtro.isEmpty()) {
            exibirTemas(todosTemas);
        } else {
            List<InfoTema> filtrados = new ArrayList<>();
            for (InfoTema tema : todosTemas) {
                if (tema.nome.toLowerCase().contains(filtro) || 
                    tema.categoria.toLowerCase().contains(filtro)) {
                    filtrados.add(tema);
                }
            }
            exibirTemas(filtrados);
        }
    }
    
    /**
     * Exibe os temas agrupados por categoria.
     */
    private void exibirTemas(List<InfoTema> temas) {
        painelTemas.removeAll();
        
        String categoriaAtual = "";
        
        for (InfoTema tema : temas) {
            // Adicionar cabeçalho de categoria
            if (!tema.categoria.equals(categoriaAtual)) {
                categoriaAtual = tema.categoria;
                JLabel labelCategoria = new JLabel(categoriaAtual);
                labelCategoria.setFont(labelCategoria.getFont().deriveFont(Font.BOLD, 14f));
                labelCategoria.setBorder(new EmptyBorder(15, 5, 5, 0));
                labelCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
                painelTemas.add(labelCategoria);
            }
            
            // Criar botão do tema
            JPanel botaoTema = criarBotaoTema(tema);
            painelTemas.add(botaoTema);
        }
        
        // Espaço extra no final
        painelTemas.add(Box.createVerticalStrut(20));
        
        painelTemas.revalidate();
        painelTemas.repaint();
        
        // Voltar ao topo
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
    
    /**
     * Cria um botão para selecionar um tema.
     */
    private JPanel criarBotaoTema(InfoTema tema) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBorder(new EmptyBorder(8, 10, 8, 10));
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Indicador de cor (light/dark)
        JPanel indicadorCor = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (tema.escuro) {
                    g2.setColor(new Color(50, 50, 50));
                } else {
                    g2.setColor(new Color(240, 240, 240));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2.dispose();
            }
        };
        indicadorCor.setPreferredSize(new Dimension(30, 30));
        indicadorCor.setOpaque(false);
        
        // Nome do tema
        JLabel labelNome = new JLabel(tema.nome);
        labelNome.setFont(labelNome.getFont().deriveFont(13f));
        
        // Indicador se é o tema atual
        String textoIcone = tema.nome.equals(temaAtual) ? "✓ " : "";
        JLabel labelCheck = new JLabel(textoIcone);
        labelCheck.setForeground(new Color(34, 197, 94));
        labelCheck.setFont(labelCheck.getFont().deriveFont(Font.BOLD, 16f));
        
        JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelEsquerdo.setOpaque(false);
        painelEsquerdo.add(indicadorCor);
        painelEsquerdo.add(labelNome);
        
        painel.add(painelEsquerdo, BorderLayout.CENTER);
        painel.add(labelCheck, BorderLayout.EAST);
        
        // Efeito hover
        painel.addMouseListener(new java.awt.event.MouseAdapter() {
            Color bgOriginal;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                bgOriginal = painel.getBackground();
                painel.setBackground(UIManager.getColor("List.selectionBackground"));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                painel.setBackground(bgOriginal);
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                aplicarTema(tema);
            }
        });
        
        return painel;
    }
    
    /**
     * Aplica o tema selecionado.
     */
    private void aplicarTema(InfoTema tema) {
        try {
            // Salvar posição do scroll antes de atualizar
            int posicaoScroll = scrollPane.getVerticalScrollBar().getValue();
            
            Class<?> classeLaf = Class.forName(tema.nomeClasse);
            LookAndFeel laf = (LookAndFeel) classeLaf.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
            
            // Atualizar todas as janelas abertas
            for (Window janela : Window.getWindows()) {
                atualizarUIComponentes(janela);
            }
            
            // Limpar cache de popups do MenuSelectionManager
            MenuSelectionManager.defaultManager().clearSelectedPath();
            
            temaAtual = tema.nome;
            
            // Atualizar label do tema atual
            labelTemaAtual.setText("Tema atual: " + temaAtual);
            
            // Notificar listener
            if (onTemaAlterado != null) {
                onTemaAlterado.accept(tema.nomeClasse);
            }
            
            // Atualizar display
            filtrarTemas();
            
            // Restaurar posição do scroll após atualização
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(posicaoScroll);
            });
            
            Toast.success(janelaParent != null ? (Component) janelaParent : this, 
                "Tema '" + tema.nome + "' aplicado com sucesso!");
            
        } catch (Exception ex) {
            Toast.error(janelaParent != null ? (Component) janelaParent : this, 
                "Erro ao aplicar tema: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Atualiza recursivamente a UI de um componente e todos seus filhos.
     */
    private void atualizarUIComponentes(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            jc.updateUI();
            
            if (jc instanceof JMenuBar) {
                JMenuBar menuBar = (JMenuBar) jc;
                for (int i = 0; i < menuBar.getMenuCount(); i++) {
                    JMenu menu = menuBar.getMenu(i);
                    if (menu != null) {
                        atualizarUIMenu(menu);
                    }
                }
            }
            
            if (jc instanceof JPopupMenu) {
                JPopupMenu popup = (JPopupMenu) jc;
                popup.updateUI();
                for (Component filho : popup.getComponents()) {
                    atualizarUIComponentes(filho);
                }
            }
        }
        
        if (c instanceof Container) {
            Container container = (Container) c;
            for (Component filho : container.getComponents()) {
                atualizarUIComponentes(filho);
            }
        }
        
        if (c instanceof Window) {
            c.invalidate();
            c.validate();
            c.repaint();
        }
    }
    
    /**
     * Atualiza recursivamente um menu e seus submenus.
     */
    private void atualizarUIMenu(JMenu menu) {
        menu.updateUI();
        JPopupMenu popup = menu.getPopupMenu();
        if (popup != null) {
            popup.updateUI();
            for (Component item : popup.getComponents()) {
                if (item instanceof JMenu) {
                    atualizarUIMenu((JMenu) item);
                } else if (item instanceof JComponent) {
                    ((JComponent) item).updateUI();
                }
            }
        }
    }
    
    /**
     * Define um listener para ser notificado quando o tema for alterado.
     * O listener recebe o nome completo da classe do tema (ex: com.formdev.flatlaf.FlatDarkLaf).
     * 
     * <h3>Exemplo:</h3>
     * <pre>{@code
     * painelTemas.setOnTemaAlterado(nomeClasse -> {
     *     usuarioService.salvarTemaPreferido(nomeClasse);
     * });
     * }</pre>
     * 
     * @param listener Consumer que recebe o nome da classe do tema
     */
    public void setOnTemaAlterado(Consumer<String> listener) {
        this.onTemaAlterado = listener;
    }
    
    /**
     * Aplica um tema pelo nome completo da classe.
     * Útil para restaurar um tema salvo nas preferências do usuário.
     * 
     * <h3>Exemplo:</h3>
     * <pre>{@code
     * String temaSalvo = usuarioService.getTemaPreferido();
     * if (temaSalvo != null) {
     *     PainelTemas.aplicarTemaPorClasse(temaSalvo);
     * }
     * }</pre>
     * 
     * @param nomeClasse Nome completo da classe do tema (ex: com.formdev.flatlaf.FlatDarkLaf)
     * @return true se o tema foi aplicado com sucesso, false caso contrário
     */
    public static boolean aplicarTemaPorClasse(String nomeClasse) {
        if (nomeClasse == null || nomeClasse.isBlank()) {
            return false;
        }
        
        try {
            Class<?> classeLaf = Class.forName(nomeClasse);
            LookAndFeel laf = (LookAndFeel) classeLaf.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
            
            // Atualizar todas as janelas abertas
            for (Window janela : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(janela);
                janela.invalidate();
                janela.validate();
                janela.repaint();
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema: " + nomeClasse + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retorna o nome do tema atualmente selecionado.
     * @return Nome do tema atual
     */
    public String getTemaAtual() {
        return temaAtual;
    }
}

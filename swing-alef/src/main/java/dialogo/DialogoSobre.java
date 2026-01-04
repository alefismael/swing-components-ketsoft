package dialogo;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo "Sobre" padrão para aplicativos.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * DialogoSobre sobre = new DialogoSobre(frame);
 * sobre.setNomeAplicativo("Meu Sistema");
 * sobre.setVersao("1.0.0");
 * sobre.setDescricao("Sistema de gerenciamento empresarial");
 * sobre.setCopyright("© 2024 Empresa XYZ");
 * sobre.setVisible(true);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class DialogoSobre extends JDialog {
    
    private JLabel lblIcone;
    private JLabel lblNome;
    private JLabel lblVersao;
    private JLabel lblDescricao;
    private JLabel lblCopyright;
    private JTextArea txtLicenca;
    
    public DialogoSobre(Frame parent) {
        super(parent, "Sobre", true);
        inicializar();
    }
    
    private void inicializar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel painel = new JPanel(new BorderLayout(20, 20));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Ícone e nome
        JPanel topo = new JPanel(new BorderLayout(15, 0));
        
        lblIcone = new JLabel();
        lblIcone.setPreferredSize(new Dimension(64, 64));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcone.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        lblIcone.setText("📦");
        topo.add(lblIcone, BorderLayout.WEST);
        
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        
        lblNome = new JLabel("Aplicativo");
        lblNome.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        info.add(lblNome);
        
        lblVersao = new JLabel("Versão 1.0.0");
        lblVersao.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblVersao.setForeground(UIManager.getColor("Label.disabledForeground"));
        info.add(lblVersao);
        
        info.add(Box.createVerticalStrut(10));
        
        lblDescricao = new JLabel("<html>Descrição do aplicativo</html>");
        lblDescricao.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        info.add(lblDescricao);
        
        topo.add(info, BorderLayout.CENTER);
        painel.add(topo, BorderLayout.NORTH);
        
        // Licença (opcional)
        txtLicenca = new JTextArea(5, 40);
        txtLicenca.setEditable(false);
        txtLicenca.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        txtLicenca.setLineWrap(true);
        txtLicenca.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(txtLicenca);
        scroll.setVisible(false);
        painel.add(scroll, BorderLayout.CENTER);
        
        // Rodapé
        JPanel rodape = new JPanel(new BorderLayout());
        
        lblCopyright = new JLabel("© 2024");
        lblCopyright.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        lblCopyright.setForeground(UIManager.getColor("Label.disabledForeground"));
        rodape.add(lblCopyright, BorderLayout.WEST);
        
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> dispose());
        rodape.add(btnOk, BorderLayout.EAST);
        
        painel.add(rodape, BorderLayout.SOUTH);
        
        setContentPane(painel);
        pack();
        setLocationRelativeTo(getParent());
    }
    
    /**
     * Define o nome do aplicativo.
     */
    public void setNomeAplicativo(String nome) {
        lblNome.setText(nome);
        setTitle("Sobre " + nome);
    }
    
    /**
     * Define a versão.
     */
    public void setVersao(String versao) {
        lblVersao.setText("Versão " + versao);
    }
    
    /**
     * Define a descrição.
     */
    public void setDescricao(String descricao) {
        lblDescricao.setText("<html>" + descricao + "</html>");
    }
    
    /**
     * Define o copyright.
     */
    public void setCopyright(String copyright) {
        lblCopyright.setText(copyright);
    }
    
    /**
     * Define o ícone do aplicativo.
     */
    public void setIconeAplicativo(Icon icone) {
        lblIcone.setIcon(icone);
        lblIcone.setText("");
    }
    
    /**
     * Define o texto de licença (mostra área de texto).
     */
    public void setLicenca(String licenca) {
        txtLicenca.setText(licenca);
        ((JScrollPane) txtLicenca.getParent().getParent()).setVisible(true);
        pack();
    }
    
    /**
     * Cria e exibe um diálogo "Sobre" simples.
     */
    public static void mostrar(Component parent, String nome, String versao, String descricao) {
        Frame frame = parent instanceof Frame ? (Frame) parent : 
                     (Frame) SwingUtilities.getWindowAncestor(parent);
        
        DialogoSobre dialogo = new DialogoSobre(frame);
        dialogo.setNomeAplicativo(nome);
        dialogo.setVersao(versao);
        dialogo.setDescricao(descricao);
        dialogo.setVisible(true);
    }
}

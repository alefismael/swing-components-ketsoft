package dialogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiFunction;

/**
 * Diálogo de login completo com usuário/senha.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * DialogoLogin login = new DialogoLogin(frame);
 * login.setTitulo("Sistema XYZ");
 * login.setValidador((usuario, senha) -> autenticar(usuario, senha));
 * login.setAoLogar(() -> abrirTelaPrincipal());
 * login.setVisible(true);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class DialogoLogin extends JDialog {
    
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JCheckBox checkLembrar;
    private JButton btnEntrar;
    private JLabel lblMensagem;
    
    private BiFunction<String, String, Boolean> validador;
    private Runnable aoLogar;
    private Runnable aoCancelar;
    
    private boolean logado = false;
    
    public DialogoLogin(Frame parent) {
        super(parent, "Login", true);
        inicializar();
    }
    
    public DialogoLogin(Dialog parent) {
        super(parent, "Login", true);
        inicializar();
    }
    
    private void inicializar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel painel = new JPanel(new BorderLayout(0, 20));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel lblTitulo = new JLabel("Bem-vindo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        painel.add(lblTitulo, BorderLayout.NORTH);
        
        // Campos
        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Usuário
        gbc.gridx = 0; gbc.gridy = 0;
        painelCampos.add(new JLabel("Usuário:"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 1;
        campoUsuario = new JTextField(20);
        campoUsuario.putClientProperty("JTextField.placeholderText", "Digite seu usuário");
        painelCampos.add(campoUsuario, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 1;
        campoSenha = new JPasswordField(20);
        campoSenha.putClientProperty("JTextField.placeholderText", "Digite sua senha");
        painelCampos.add(campoSenha, gbc);
        
        // Lembrar
        gbc.gridx = 0; gbc.gridy = 4;
        checkLembrar = new JCheckBox("Lembrar-me");
        painelCampos.add(checkLembrar, gbc);
        
        // Mensagem de erro
        gbc.gridx = 0; gbc.gridy = 5;
        lblMensagem = new JLabel(" ");
        lblMensagem.setForeground(new Color(0xF44336));
        lblMensagem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        painelCampos.add(lblMensagem, gbc);
        
        painel.add(painelCampos, BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        btnEntrar = new JButton("Entrar");
        btnEntrar.setPreferredSize(new Dimension(100, 35));
        btnEntrar.addActionListener(e -> tentarLogin());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.addActionListener(e -> cancelar());
        
        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnCancelar);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        setContentPane(painel);
        
        // Enter para login
        campoSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tentarLogin();
                }
            }
        });
        
        campoUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    campoSenha.requestFocus();
                }
            }
        });
        
        // Escape para cancelar
        getRootPane().registerKeyboardAction(e -> cancelar(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().setDefaultButton(btnEntrar);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void tentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());
        
        if (usuario.isEmpty()) {
            mostrarErro("Digite o usuário");
            campoUsuario.requestFocus();
            return;
        }
        
        if (senha.isEmpty()) {
            mostrarErro("Digite a senha");
            campoSenha.requestFocus();
            return;
        }
        
        btnEntrar.setEnabled(false);
        lblMensagem.setText("Autenticando...");
        lblMensagem.setForeground(UIManager.getColor("Label.foreground"));
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                if (validador != null) {
                    return validador.apply(usuario, senha);
                }
                return true; // Sem validador, aceita qualquer login
            }
            
            @Override
            protected void done() {
                try {
                    boolean sucesso = get();
                    if (sucesso) {
                        logado = true;
                        if (aoLogar != null) {
                            aoLogar.run();
                        }
                        dispose();
                    } else {
                        mostrarErro("Usuário ou senha inválidos");
                        campoSenha.setText("");
                        campoSenha.requestFocus();
                    }
                } catch (Exception e) {
                    mostrarErro("Erro: " + e.getMessage());
                }
                btnEntrar.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void mostrarErro(String msg) {
        lblMensagem.setText(msg);
        lblMensagem.setForeground(new Color(0xF44336));
    }
    
    private void cancelar() {
        logado = false;
        if (aoCancelar != null) {
            aoCancelar.run();
        }
        dispose();
    }
    
    /**
     * Define o validador de login.
     */
    public void setValidador(BiFunction<String, String, Boolean> validador) {
        this.validador = validador;
    }
    
    /**
     * Define ação ao logar com sucesso.
     */
    public void setAoLogar(Runnable acao) {
        this.aoLogar = acao;
    }
    
    /**
     * Define ação ao cancelar.
     */
    public void setAoCancelar(Runnable acao) {
        this.aoCancelar = acao;
    }
    
    /**
     * Define o título do diálogo.
     */
    public void setTitulo(String titulo) {
        setTitle(titulo);
    }
    
    /**
     * Verifica se o login foi bem sucedido.
     */
    public boolean isLogado() {
        return logado;
    }
    
    /**
     * Retorna o usuário digitado.
     */
    public String getUsuario() {
        return campoUsuario.getText();
    }
    
    /**
     * Retorna se "lembrar" está marcado.
     */
    public boolean isLembrar() {
        return checkLembrar.isSelected();
    }
    
    /**
     * Define o usuário inicial.
     */
    public void setUsuario(String usuario) {
        campoUsuario.setText(usuario);
    }
    
    /**
     * Define se "lembrar" está marcado.
     */
    public void setLembrar(boolean lembrar) {
        checkLembrar.setSelected(lembrar);
    }
}

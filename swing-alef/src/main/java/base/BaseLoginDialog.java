package base;

import fields.CampoTexto;
import fields.CampoSenha;
import ui.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.BiFunction;

/**
 * Diálogo de login reutilizável com campos de usuário e senha.
 * 
 * <p>Exemplo de uso básico:</p>
 * <pre>{@code
 * BaseLoginDialog login = new BaseLoginDialog(frame, "Login do Sistema");
 * login.setAutenticador((usuario, senha) -> {
 *     // Sua lógica de autenticação
 *     return usuario.equals("admin") && senha.equals("123");
 * });
 * 
 * if (login.mostrar()) {
 *     // Login bem sucedido
 *     System.out.println("Usuário: " + login.getUsuario());
 * } else {
 *     // Login cancelado ou falhou
 *     System.exit(0);
 * }
 * }</pre>
 * 
 * <p>Com banco de dados:</p>
 * <pre>{@code
 * login.setAutenticador((usuario, senha) -> {
 *     return UsuarioRepository.verificarCredenciais(usuario, senha);
 * });
 * }</pre>
 * 
 * @author alefi
 */
public class BaseLoginDialog extends JDialog {
    
    private CampoTexto campoUsuario;
    private CampoSenha campoSenha;
    private JButton btnEntrar;
    private JButton btnCancelar;
    private JLabel lblMensagem;
    
    private boolean loginRealizado = false;
    private BiFunction<String, String, Boolean> autenticador;
    
    private String titulo = "Login";
    private String labelUsuario = "Usuário";
    private String labelSenha = "Senha";
    private String mensagemInicial = "Informe suas credenciais para acessar o sistema.";
    private String mensagemErro = "Usuário ou senha inválidos!";
    
    /**
     * Cria um diálogo de login.
     * @param owner Frame pai
     */
    public BaseLoginDialog(Frame owner) {
        this(owner, "Login");
    }
    
    /**
     * Cria um diálogo de login com título.
     * @param owner Frame pai
     * @param titulo Título da janela
     */
    public BaseLoginDialog(Frame owner, String titulo) {
        super(owner, titulo, true);
        this.titulo = titulo;
        inicializar();
    }
    
    private void inicializar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Painel de campos
        JPanel painelCampos = new JPanel();
        painelCampos.setLayout(new BoxLayout(painelCampos, BoxLayout.Y_AXIS));
        painelCampos.setMinimumSize(new java.awt.Dimension(400, 700));
        
        // Campo usuário
        campoUsuario = new CampoTexto(labelUsuario);
        campoUsuario.setPreferredSize(new Dimension(300, 60));
        campoUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        painelCampos.add(campoUsuario);
        
        painelCampos.add(Box.createVerticalStrut(10));
        
        // Campo senha
        campoSenha = new CampoSenha(labelSenha);
        campoSenha.setPreferredSize(new Dimension(300, 60));
        campoSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        painelCampos.add(campoSenha);
        
        painelCampos.add(Box.createVerticalStrut(15));
        
        // Mensagem
        lblMensagem = new JLabel(mensagemInicial);
        lblMensagem.setFont(lblMensagem.getFont().deriveFont(Font.PLAIN, 11f));
        lblMensagem.setForeground(UIManager.getColor("Label.disabledForeground"));
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampos.add(lblMensagem);
        
        painelPrincipal.add(painelCampos, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        btnEntrar = new JButton("Entrar");
        btnEntrar.setPreferredSize(new Dimension(100, 30));
        btnEntrar.addActionListener(e -> tentarLogin());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.addActionListener(e -> cancelar());
        
        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnCancelar);
        
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        add(painelPrincipal);
        
        // Atalhos de teclado
        getRootPane().setDefaultButton(btnEntrar);
        
        // ESC para cancelar
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        getRootPane().getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        
        pack();
        setLocationRelativeTo(getOwner());
    }
    
    /**
     * Define o autenticador que verifica usuário/senha.
     * @param autenticador Função que recebe (usuário, senha) e retorna true se válido
     */
    public void setAutenticador(BiFunction<String, String, Boolean> autenticador) {
        this.autenticador = autenticador;
    }
    
    /**
     * Exibe o diálogo e aguarda interação.
     * @return true se login foi bem sucedido, false se cancelado ou falhou
     */
    public boolean mostrar() {
        loginRealizado = false;
        campoUsuario.setValue("");
        campoSenha.setValue("");
        lblMensagem.setText(mensagemInicial);
        lblMensagem.setForeground(UIManager.getColor("Label.disabledForeground"));
        setVisible(true);
        return loginRealizado;
    }
    
    private void tentarLogin() {
        String usuario = campoUsuario.getValue();
        String senha = campoSenha.getValue();
        
        if (usuario.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha usuário e senha!");
            return;
        }
        
        if (autenticador == null) {
            // Sem autenticador, aceita qualquer coisa
            loginRealizado = true;
            dispose();
            return;
        }
        
        try {
            loginRealizado = autenticador.apply(usuario, senha);
            if (loginRealizado) {
                dispose();
            } else {
                mostrarErro(mensagemErro);
                campoSenha.setValue("");
                campoSenha.requestFocusInWindow();
            }
        } catch (Exception e) {
            mostrarErro("Erro na autenticação: " + e.getMessage());
        }
    }
    
    private void mostrarErro(String msg) {
        lblMensagem.setText(msg);
        lblMensagem.setForeground(UIManager.getColor("Component.error.borderColor"));
    }
    
    private void cancelar() {
        loginRealizado = false;
        dispose();
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Retorna o usuário informado.
     */
    public String getUsuario() {
        return campoUsuario.getValue();
    }
    
    /**
     * Retorna a senha informada.
     */
    public String getSenha() {
        return campoSenha.getValue();
    }
    
    /**
     * Verifica se o login foi realizado com sucesso.
     */
    public boolean isLoginRealizado() {
        return loginRealizado;
    }
    
    // ==================== CUSTOMIZAÇÃO ====================
    
    /**
     * Define o label do campo usuário.
     */
    public void setLabelUsuario(String label) {
        this.labelUsuario = label;
    }
    
    /**
     * Define o label do campo senha.
     */
    public void setLabelSenha(String label) {
        this.labelSenha = label;
    }
    
    /**
     * Define a mensagem inicial exibida.
     */
    public void setMensagemInicial(String mensagem) {
        this.mensagemInicial = mensagem;
        lblMensagem.setText(mensagem);
    }
    
    /**
     * Define a mensagem de erro para credenciais inválidas.
     */
    public void setMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }
    
    /**
     * Define o texto do botão de entrar.
     */
    public void setTextoBotaoEntrar(String texto) {
        btnEntrar.setText(texto);
    }
    
    /**
     * Define o texto do botão de cancelar.
     */
    public void setTextoBotaoCancelar(String texto) {
        btnCancelar.setText(texto);
    }
    
    /**
     * Oculta o botão cancelar.
     */
    public void ocultarBotaoCancelar() {
        btnCancelar.setVisible(false);
    }
}

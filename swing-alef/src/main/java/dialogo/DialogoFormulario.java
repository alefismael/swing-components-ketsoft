package dialogo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import componente.Toast;
import nucleo.Validavel;
import util.KeyBindingManager;
import util.KeyBindingManager.Atalho;

/**
 * Diálogo base para formulários modais com validação automática.
 * 
 * <h3>Dois modos de uso:</h3>
 * 
 * <h4>1. MODO PROGRAMÁTICO (tradicional):</h4>
 * <pre>{@code
 * DialogoFormulario dialog = new DialogoFormulario(frame, "Novo Cliente");
 * dialog.adicionarCampo(new CampoTexto("Nome"));
 * dialog.adicionarCampo(new CampoEmail("Email"));
 * dialog.mostrarDialogo(() -> {
 *     // Salvar dados
 * });
 * }</pre>
 * 
 * <h4>2. MODO VISUAL (NetBeans Design Builder):</h4>
 * <pre>{@code
 * // No NetBeans, crie um JPanel com os campos
 * // e passe como conteúdo do dialog:
 * DialogoFormulario dialog = new DialogoFormulario(frame, "Novo Cliente");
 * dialog.setConteudo(meuPainelDoNetBeans);
 * dialog.mostrarDialogo(() -> {
 *     // Os campos Validavel são detectados automaticamente
 * });
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public class DialogoFormulario extends JDialog {
    
    private JPanel painelConteudo;
    private final JPanel painelBotoes;
    private final JScrollPane scrollPane;
    private boolean confirmado = false;
    private boolean validarAoConfirmar = true;
    
    /**
     * Cria um novo diálogo de formulário.
     * 
     * @param framePai janela pai
     * @param titulo título do diálogo
     */
    public DialogoFormulario(JFrame framePai, String titulo) {
        super(framePai, titulo, true);
        
        painelConteudo = new JPanel();
        // Layout vertical para campos um embaixo do outro
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        // Wrapper para o conteúdo com scroll
        scrollPane = new JScrollPane(painelConteudo,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setViewportBorder(null);
        
        configurar();
    }
    
    /**
     * Construtor para uso com NetBeans Design Builder.
     * Permite criar o dialog sem frame pai (usa null).
     */
    public DialogoFormulario() {
        this(null, "Formulário");
    }
    
    private void configurar() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(true);
        
        // Painel principal
        JPanel pnlPrincipal = new JPanel(new BorderLayout());
        pnlPrincipal.add(scrollPane, BorderLayout.CENTER);
        pnlPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        add(pnlPrincipal);
    }
    
    /**
     * Define um painel customizado como conteúdo.
     * Útil para painéis criados no NetBeans Design Builder.
     * Os campos Validavel dentro do painel são detectados automaticamente.
     * 
     * @param painel JPanel com os campos
     */
    public void setConteudo(JPanel painel) {
        this.painelConteudo = painel;
        scrollPane.setViewportView(painel);
    }
    
    /**
     * Retorna o painel de conteúdo atual.
     * 
     * @return painel de conteúdo
     */
    public JPanel getConteudo() {
        return painelConteudo;
    }
    
    /**
     * Adiciona um campo ao formulário do diálogo (modo programático).
     * Define tamanho máximo para evitar que campos fiquem muito grandes.
     * 
     * @param campo componente do campo
     */
    public void adicionarCampo(javax.swing.JComponent campo) {
        // Limitar altura máxima para evitar campos gigantes com BoxLayout
        java.awt.Dimension prefSize = campo.getPreferredSize();
        int alturaMax = Math.max(prefSize.height, 40); // Mínimo 40px de altura
        campo.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, alturaMax));
        campo.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        painelConteudo.add(campo);
        painelConteudo.add(javax.swing.Box.createVerticalStrut(5)); // Espaçamento entre campos
    }
    
    /**
     * Define os tamanho do diálogo.
     * 
     * @param largura largura em pixels
     * @param altura altura em pixels
     */
    public void definirTamanho(int largura, int altura) {
        setSize(largura, altura);
    }
    
    /**
     * Define se deve validar campos ao confirmar.
     * @param validar true para validar (padrão)
     */
    public void setValidarAoConfirmar(boolean validar) {
        this.validarAoConfirmar = validar;
    }
    
    /**
     * Mostra o diálogo e aguarda interação do usuário.
     * 
     * @param acaoConfirmar ação executada ao clicar em OK (se validação passar)
     */
    public void mostrarDialogo(Runnable acaoConfirmar) {
        painelBotoes.removeAll();
        
        JButton botaoOK = new JButton("Confirmar");
        botaoOK.setToolTipText(KeyBindingManager.tooltipComAtalho("Confirmar", Atalho.SALVAR));
        botaoOK.addActionListener(e -> confirmarDialog(acaoConfirmar));
        
        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.setToolTipText(KeyBindingManager.tooltipComAtalho("Cancelar", Atalho.CANCELAR));
        botaoCancelar.addActionListener(e -> cancelarDialog());
        
        painelBotoes.add(botaoOK);
        painelBotoes.add(botaoCancelar);
        
        // Registrar atalhos
        KeyBindingManager.registrarGlobal(this, Atalho.SALVAR, () -> confirmarDialog(acaoConfirmar));
        KeyBindingManager.registrarGlobal(this, Atalho.CANCELAR, this::cancelarDialog);
        
        setVisible(true);
    }
    
    private void confirmarDialog(Runnable acaoConfirmar) {
        if (validarAoConfirmar && !validarTodosCampos()) {
            return;
        }
        
        confirmado = true;
        if (acaoConfirmar != null) {
            acaoConfirmar.run();
        }
        dispose();
    }
    
    private void cancelarDialog() {
        confirmado = false;
        dispose();
    }
    
    /**
     * Valida todos os campos Validavel do formulário.
     * @return true se todos válidos
     */
    public boolean validarTodosCampos() {
        List<Validavel> campos = coletarCamposValidaveis();
        
        boolean todosValidos = true;
        Validavel primeiroInvalido = null;
        
        for (Validavel campo : campos) {
            if (!campo.validarComFeedback()) {
                todosValidos = false;
                if (primeiroInvalido == null) {
                    primeiroInvalido = campo;
                }
            }
        }
        
        if (!todosValidos && primeiroInvalido != null) {
            // Foca no primeiro campo inválido
            if (primeiroInvalido instanceof Component) {
                ((Component) primeiroInvalido).requestFocusInWindow();
            }
            // Mostra toast de erro
            Toast.error(this, primeiroInvalido.getMensagemErro());
        }
        
        return todosValidos;
    }
    
    /**
     * Coleta todos os campos Validavel do formulário.
     */
    private List<Validavel> coletarCamposValidaveis() {
        List<Validavel> campos = new ArrayList<>();
        coletarValidaveisRecursivo(painelConteudo, campos);
        return campos;
    }
    
    /**
     * Coleta campos Validavel recursivamente.
     */
    private void coletarValidaveisRecursivo(Container container, List<Validavel> lista) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof Validavel) {
                lista.add((Validavel) comp);
            }
            if (comp instanceof Container) {
                coletarValidaveisRecursivo((Container) comp, lista);
            }
        }
    }
    
    /**
     * Limpa erros de todos os campos.
     */
    public void limparErros() {
        for (Validavel campo : coletarCamposValidaveis()) {
            campo.limparErro();
        }
    }
    
    /**
     * Verifica se foi confirmado.
     * 
     * @return true se clicou OK
     */
    public boolean foiConfirmado() {
        return confirmado;
    }
    
    /**
     * Limpa todos os campos do formulário.
     */
    public void limpar() {
        // Limpa campos Validavel
        for (Validavel campo : coletarCamposValidaveis()) {
            campo.limparErro();
        }
    }
}

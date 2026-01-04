package campo;

import nucleo.Validavel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Classe base abstrata para componentes de formulário com label e validação.
 * Todos os campos de formulário devem estender esta classe.
 * 
 * <p>Implementa {@link Validavel} para integração com dialogs e formulários.</p>
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Label automático</li>
 *   <li>Validação com feedback visual (borda vermelha + mensagem inline)</li>
 *   <li>Campo obrigatório configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * @param <T> Tipo do valor do campo
 * @author alefi
 */
public abstract class CampoForm<T> extends JPanel implements Validavel {

    protected JLabel label;
    protected JLabel labelErro;
    protected boolean obrigatorio = false;
    protected String mensagemErro;
    private JPanel painelTopo;

    // Bordas para estados
    private static final Border BORDA_NORMAL = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    private static final Border BORDA_ERRO = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(239, 68, 68)),
        BorderFactory.createEmptyBorder(0, 0, 0, 0)
    );
    private static final Color COR_ERRO = new Color(239, 68, 68);

    /**
     * Construtor padrão para compatibilidade com GUI Builder.
     */
    public CampoForm() {
        this("Campo");
    }
    
    /**
     * Construtor com título do campo.
     * @param titulo texto do label
     */
    public CampoForm(String titulo) {
        super();
        setLayout(new BorderLayout(4, 2));

        // Painel topo com label e mensagem de erro inline
        painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);
        
        label = criarLabel(titulo);
        painelTopo.add(label, BorderLayout.WEST);

        // Label de erro inline (ao lado do label, inicialmente invisível)
        labelErro = new JLabel();
        labelErro.setForeground(COR_ERRO);
        labelErro.setFont(labelErro.getFont().deriveFont(11f));
        labelErro.setVisible(false);
        painelTopo.add(labelErro, BorderLayout.CENTER);
        
        add(painelTopo, BorderLayout.NORTH);
    }
    
    /**
     * Cria um JLabel com estilo padrão.
     * @param texto texto do label
     * @return JLabel configurado
     */
    protected JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(UIManager.getColor("Label.foreground"));
        lbl.setFont(UIManager.getFont("Label.font"));
        lbl.setFocusable(false);
        lbl.setOpaque(false);
        lbl.setHorizontalAlignment(JLabel.LEFT);
        return lbl;
    }

    /**
     * Obtém o valor atual do campo.
     * @return valor do campo
     */
    public abstract T getValue();
    
    /**
     * Define o valor do campo.
     * @param value novo valor
     */
    public abstract void setValue(T value);
    
    /**
     * Verifica se o valor do campo é válido.
     * Subclasses podem sobrescrever para adicionar validações específicas.
     * @return true se válido, false caso contrário
     */
    @Override
    public boolean validar() {
        mensagemErro = null;
        
        // Validação de campo obrigatório
        if (obrigatorio) {
            T valor = getValue();
            if (valor == null || (valor instanceof String && ((String) valor).trim().isEmpty())) {
                mensagemErro = label.getText().replace(" *", "") + " é obrigatório";
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getMensagemErro() {
        return mensagemErro;
    }
    
    @Override
    public void mostrarErro() {
        setBorder(BORDA_ERRO);
        if (mensagemErro != null && !mensagemErro.isEmpty()) {
            labelErro.setText(" - " + mensagemErro);
            labelErro.setVisible(true);
            setToolTipText(mensagemErro);
        }
    }
    
    @Override
    public void limparErro() {
        setBorder(BORDA_NORMAL);
        labelErro.setText("");
        labelErro.setVisible(false);
        setToolTipText(null);
    }
    
    /**
     * Define se o campo é obrigatório.
     * Campos obrigatórios exibem * no label.
     * @param obrigatorio true se obrigatório
     */
    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
        atualizarLabel();
    }
    
    /**
     * Verifica se o campo é obrigatório.
     * @return true se obrigatório
     */
    public boolean isObrigatorio() {
        return obrigatorio;
    }
    
    /**
     * Define uma mensagem de erro customizada.
     * @param mensagem mensagem de erro
     */
    protected void setMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }
    
    private void atualizarLabel() {
        String texto = label.getText().replace(" *", "");
        if (obrigatorio) {
            label.setText(texto + " *");
        } else {
            label.setText(texto);
        }
    }
    
    /**
     * Obtém o texto do label (sem asterisco de obrigatório).
     * @return texto do label
     */
    public String getTextLabel() {
        return label.getText().replace(" *", "");
    }
    
    /**
     * Define o texto do label.
     * @param textLabel novo texto
     */
    public void setTextLabel(String textLabel) {
        label.setText(textLabel);
        if (obrigatorio) {
            atualizarLabel();
        }
    }
    
    /**
     * Valida o campo e mostra feedback visual se inválido.
     * @return true se válido
     */
    public boolean validarComFeedback() {
        limparErro();
        if (!validar()) {
            mostrarErro();
            return false;
        }
        return true;
    }
}

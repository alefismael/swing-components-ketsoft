package campo;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Campo de texto simples para formulários.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Validação automática de campo obrigatório</li>
 *   <li>Limites de tamanho (min/max caracteres)</li>
 *   <li>Feedback visual de erro</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoTexto nome = new CampoTexto("Nome");
 * nome.setObrigatorio(true);
 * nome.setMinLength(3);
 * nome.setMaxLength(100);
 * }</pre>
 * 
 * @author alefi
 */
public class CampoTexto extends CampoForm<String> {

    protected JTextField field;
    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;
    private boolean validarEmTempoReal = false;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoTexto() {
        super("Campo de Texto");
        inicializar();
    }

    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoTexto(String titulo) {
        super(titulo);
        inicializar();
    }
    
    private void inicializar() {
        field = criarCampoTexto();
        add(field, BorderLayout.CENTER);

        // Listener para validação em tempo real (opcional)
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validarSeAtivo(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validarSeAtivo(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validarSeAtivo(); }
            
            private void validarSeAtivo() {
                if (validarEmTempoReal) {
                    validarComFeedback();
                }
            }
        });
    }
    
    /**
     * Cria o campo de texto com configurações padrão.
     * @return JTextField configurado
     */
    protected JTextField criarCampoTexto() {
        JTextField tf = new JTextField(20);
        tf.setOpaque(true);
        tf.setPreferredSize(new Dimension(300, 28));
        return tf;
    }

    @Override
    public String getValue() {
        return field != null ? field.getText() : "";
    }

    @Override
    public void setValue(String value) {
        if (field != null) {
            field.setText(value);
        }
    }

    @Override
    public boolean validar() {
        // Primeiro valida obrigatoriedade (da classe pai)
        if (!super.validar()) {
            return false;
        }
        
        String valor = getValue().trim();
        
        // Validação de tamanho mínimo
        if (valor.length() > 0 && valor.length() < minLength) {
            setMensagemErro(getTextLabel() + " deve ter no mínimo " + minLength + " caracteres");
            return false;
        }
        
        // Validação de tamanho máximo
        if (valor.length() > maxLength) {
            setMensagemErro(getTextLabel() + " deve ter no máximo " + maxLength + " caracteres");
            return false;
        }
        
        return true;
    }
    
    /**
     * Define o tamanho mínimo do texto.
     * @param minLength número mínimo de caracteres
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    
    /**
     * Obtém o tamanho mínimo do texto.
     * @return número mínimo de caracteres
     */
    public int getMinLength() {
        return minLength;
    }
    
    /**
     * Define o tamanho máximo do texto.
     * @param maxLength número máximo de caracteres
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    /**
     * Obtém o tamanho máximo do texto.
     * @return número máximo de caracteres
     */
    public int getMaxLength() {
        return maxLength;
    }
    
    /**
     * Ativa validação em tempo real enquanto digita.
     * @param ativo true para ativar
     */
    public void setValidarEmTempoReal(boolean ativo) {
        this.validarEmTempoReal = ativo;
    }
    
    /**
     * Verifica se a validação em tempo real está ativa.
     * @return true se ativo
     */
    public boolean isValidarEmTempoReal() {
        return validarEmTempoReal;
    }

    /**
     * Obtém o JTextField interno.
     * @return campo de texto
     */
    public JTextField getField() {
        return field;
    }
    
    /**
     * Define texto placeholder (FlatLaf).
     * @param placeholder texto de placeholder
     */
    public void setPlaceholder(String placeholder) {
        if (field != null) {
            field.putClientProperty("JTextField.placeholderText", placeholder);
        }
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        setValue("");
        limparErro();
    }
}

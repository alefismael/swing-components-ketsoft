package campo;

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.regex.Pattern;

/**
 * Campo de e-mail com validação em tempo real.
 * Valida o formato do e-mail e mostra feedback visual.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Validação em tempo real</li>
 *   <li>Feedback visual (borda verde/vermelha)</li>
 *   <li>Regex padrão configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoEmail email = new CampoEmail("E-mail");
 * email.setObrigatorio(true);
 * if (email.validar()) {
 *     enviarEmail(email.getValue());
 * }
 * }</pre>
 * 
 * @author alefi
 */
public class CampoEmail extends CampoForm<String> {

    protected JTextField field;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Color COR_VALIDO = new Color(76, 175, 80);
    private static final Color COR_INVALIDO = new Color(244, 67, 54);

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoEmail() {
        super("E-mail");
        inicializar();
    }

    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoEmail(String titulo) {
        super(titulo);
        inicializar();
    }
    
    private void inicializar() {
        field = new JTextField(20);
        field.setPreferredSize(new Dimension(300, 28));
        configurarValidacao();
        add(field, BorderLayout.CENTER);
    }

    private void configurarValidacao() {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarVisualmente();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarVisualmente();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarVisualmente();
            }
        });
    }

    private void validarVisualmente() {
        String texto = field.getText().trim();
        if (texto.isEmpty()) {
            field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else if (isEmailValido(texto)) {
            field.setBorder(BorderFactory.createLineBorder(COR_VALIDO, 2));
        } else {
            field.setBorder(BorderFactory.createLineBorder(COR_INVALIDO, 2));
        }
    }

    private boolean isEmailValido(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public String getValue() {
        return field != null ? field.getText().trim() : "";
    }

    @Override
    public void setValue(String value) {
        if (field != null) {
            field.setText(value);
            validarVisualmente();
        }
    }

    @Override
    public boolean validar() {
        String email = getValue();
        
        // Se não obrigatório e vazio, é válido
        if (!obrigatorio && email.isEmpty()) {
            return true;
        }
        
        // Se obrigatório e vazio
        if (obrigatorio && email.isEmpty()) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        
        // Validar formato
        if (!isEmailValido(email)) {
            setMensagemErro(getTextLabel() + " possui formato inválido");
            return false;
        }
        
        return true;
    }

    /**
     * Obtém o JTextField interno.
     * @return campo de texto
     */
    public JTextField getField() {
        return field;
    }
    
    /**
     * Verifica se o e-mail é válido sem verificar se está vazio.
     * @return true se o formato é válido
     */
    public boolean isFormatoValido() {
        String email = getValue();
        return email.isEmpty() || isEmailValido(email);
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        setValue("");
        limparErro();
    }
}

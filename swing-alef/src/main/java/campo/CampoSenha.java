package campo;

import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Campo de senha com máscara de caracteres.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara de caracteres (●●●●)</li>
 *   <li>Validação de segurança configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoSenha senha = new CampoSenha("Senha");
 * senha.setObrigatorio(true);
 * if (senha.isSegura(8)) {
 *     // senha tem pelo menos 8 caracteres
 * }
 * }</pre>
 * 
 * @author alefi
 */
public class CampoSenha extends CampoForm<String> {
    
    private JPasswordField field;
    private int minLength = 0;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoSenha() {
        super("Senha");
        inicializar();
    }

    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoSenha(String titulo) {
        super(titulo);
        inicializar();
    }
    
    private void inicializar() {
        field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(300, 28));
        add(field, BorderLayout.CENTER);
    }

    @Override
    public String getValue() {
        return field != null ? new String(field.getPassword()) : "";
    }

    @Override
    public void setValue(String value) {
        if (field != null) {
            field.setText(value);
        }
    }

    @Override
    public boolean validar() {
        // Validação de obrigatoriedade
        if (!super.validar()) {
            return false;
        }
        
        // Validação de tamanho mínimo
        if (minLength > 0 && getValue().length() < minLength) {
            setMensagemErro(getTextLabel() + " deve ter no mínimo " + minLength + " caracteres");
            return false;
        }
        
        return true;
    }

    /**
     * Obtém o campo de senha.
     * @return JPasswordField
     */
    public JPasswordField getField() {
        return field;
    }
    
    /**
     * Define o tamanho mínimo da senha.
     * @param minLength número mínimo de caracteres
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    
    /**
     * Obtém o tamanho mínimo da senha.
     * @return número mínimo de caracteres
     */
    public int getMinLength() {
        return minLength;
    }
    
    /**
     * Valida se a senha atende requisitos mínimos de segurança.
     * @param minLength tamanho mínimo
     * @return true se válida
     */
    public boolean isSegura(int minLength) {
        return getValue().length() >= minLength;
    }
    
    /**
     * Verifica se a senha contém caracteres especiais.
     * @return true se contém
     */
    public boolean contemCaracteresEspeciais() {
        return getValue().matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
    
    /**
     * Verifica se a senha contém números.
     * @return true se contém
     */
    public boolean contemNumeros() {
        return getValue().matches(".*\\d.*");
    }
    
    /**
     * Verifica se a senha contém letras maiúsculas.
     * @return true se contém
     */
    public boolean contemMaiusculas() {
        return getValue().matches(".*[A-Z].*");
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        setValue("");
        limparErro();
    }
}

package campo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;

/**
 * Campo de CEP com máscara formatada (99999-999).
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática 99999-999</li>
 *   <li>Validação de formato</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoCep cep = new CampoCep();
 * cep.setObrigatorio(true);
 * String cepLimpo = cep.getUnmaskedValue();
 * }</pre>
 * 
 * @author alefi
 */
public class CampoCep extends CampoForm<String> {

    private JFormattedTextField campo;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoCep() {
        this("CEP");
    }
    
    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoCep(String titulo) {
        super(titulo);
        try {
            MaskFormatter formatter = new MaskFormatter("#####-###");
            formatter.setPlaceholderCharacter('_');
            campo = new JFormattedTextField(formatter);
        } catch (ParseException e) {
            campo = new JFormattedTextField();
        }
        campo.setPreferredSize(new Dimension(150, 28));
        add(campo, BorderLayout.CENTER);
    }

    @Override
    public String getValue() {
        return campo != null ? campo.getText().trim() : "";
    }

    @Override
    public void setValue(String value) {
        if (campo != null) {
            campo.setText(value);
        }
    }

    @Override
    public boolean validar() {
        String valor = getValue();
        
        // Se não obrigatório e vazio, é válido
        if (!obrigatorio && (valor.isBlank() || valor.contains("_"))) {
            return true;
        }
        
        // Se obrigatório e vazio
        if (obrigatorio && (valor.isBlank() || valor.contains("_"))) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        
        // Validar formato
        if (!valor.matches("\\d{5}-\\d{3}")) {
            setMensagemErro(getTextLabel() + " está incompleto");
            return false;
        }
        
        return true;
    }
    
    /**
     * Obtém o campo formatado.
     * @return JFormattedTextField
     */
    public JFormattedTextField getCampo() {
        return campo;
    }
    
    /**
     * Obtém o valor sem máscara (só números).
     * @return CEP sem formatação
     */
    public String getUnmaskedValue() {
        return getValue().replaceAll("[^0-9]", "");
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        campo.setText("");
        limparErro();
    }
}

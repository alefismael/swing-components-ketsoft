package campo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;

/**
 * Campo de telefone com máscara formatada.
 * Suporta telefone fixo (99) 9999-9999 e celular (99) 99999-9999.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática para fixo e celular</li>
 *   <li>Validação de formato</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoTelefone celular = new CampoTelefone("Celular", true);
 * celular.setObrigatorio(true);
 * }</pre>
 * 
 * @author alefi
 */
public class CampoTelefone extends CampoForm<String> {

    private JFormattedTextField campo;
    private boolean celular;

    /**
     * Construtor padrão para GUI Builder (telefone fixo).
     */
    public CampoTelefone() {
        this("Telefone", false);
    }

    /**
     * Construtor com título (telefone fixo).
     * @param titulo texto do label
     */
    public CampoTelefone(String titulo) {
        this(titulo, false);
    }
    
    /**
     * Construtor com título e tipo.
     * @param titulo texto do label
     * @param celular true para máscara de celular
     */
    public CampoTelefone(String titulo, boolean celular) {
        super(titulo);
        this.celular = celular;
        try {
            String mask = celular ? "(##) #####-####" : "(##) ####-####";
            MaskFormatter formatter = new MaskFormatter(mask);
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
        boolean formatoValido;
        if (celular) {
            formatoValido = valor.matches("\\(\\d{2}\\) \\d{5}-\\d{4}");
        } else {
            formatoValido = valor.matches("\\(\\d{2}\\) \\d{4}-\\d{4}");
        }
        
        if (!formatoValido) {
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
     * Verifica se é máscara de celular.
     * @return true se celular
     */
    public boolean isCelular() {
        return celular;
    }
    
    /**
     * Obtém o valor sem máscara (só números).
     * @return telefone sem formatação
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

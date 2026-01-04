package campo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;

/**
 * Campo de data com máscara formatada (dd/MM/yyyy).
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática dd/MM/yyyy</li>
 *   <li>Validação de data válida</li>
 *   <li>Conversão String/Date</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoData dataNascimento = new CampoData("Data de Nascimento");
 * dataNascimento.setObrigatorio(true);
 * Date data = dataNascimento.getValue();
 * }</pre>
 * 
 * @author alefi
 */
public class CampoData extends CampoForm<Date> {

    private JFormattedTextField campo;
    private SimpleDateFormat dateFormat;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoData() {
        super("Data");
        inicializar();
    }

    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoData(String titulo) {
        super(titulo);
        inicializar();
    }
    
    private void inicializar() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            formatter.setPlaceholderCharacter('_');
            campo = new JFormattedTextField(formatter);
        } catch (ParseException e) {
            campo = new JFormattedTextField();
        }
        
        campo.setPreferredSize(new Dimension(150, 28));
        add(campo, BorderLayout.CENTER);
    }

    @Override
    public Date getValue() {
        if (campo == null) {
            return null;
        }
        try {
            String texto = campo.getText().trim();
            if (texto.isEmpty() || texto.contains("_")) {
                return null;
            }
            return dateFormat.parse(texto);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void setValue(Date value) {
        if (campo != null) {
            if (value == null) {
                campo.setText("");
            } else {
                campo.setText(dateFormat.format(value));
            }
        }
    }

    @Override
    public boolean validar() {
        // Proteção contra chamada durante construção
        if (campo == null) {
            return true;
        }
        
        // Validação de obrigatoriedade
        if (obrigatorio) {
            Date valor = getValue();
            if (valor == null) {
                String texto = campo.getText().trim();
                if (texto.isEmpty() || texto.contains("_")) {
                    setMensagemErro(getTextLabel() + " é obrigatório");
                } else {
                    setMensagemErro(getTextLabel() + " contém data inválida");
                }
                return false;
            }
        } else {
            // Se não obrigatório, verifica se preenchido parcialmente
            String texto = campo.getText().trim();
            if (!texto.isEmpty() && !texto.equals("__/__/____") && getValue() == null) {
                setMensagemErro(getTextLabel() + " contém data inválida");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtém o valor como String no formato dd/MM/yyyy.
     * @return data formatada ou string vazia
     */
    public String getValueAsString() {
        Date data = getValue();
        return data != null ? dateFormat.format(data) : "";
    }
    
    /**
     * Define o valor a partir de uma String no formato dd/MM/yyyy.
     * @param dataStr string da data
     */
    public void setValueFromString(String dataStr) {
        try {
            if (dataStr == null || dataStr.trim().isEmpty()) {
                campo.setText("");
            } else {
                Date data = dateFormat.parse(dataStr);
                setValue(data);
            }
        } catch (ParseException e) {
            campo.setText("");
        }
    }
    
    /**
     * Obtém o campo de texto formatado.
     * @return JFormattedTextField
     */
    public JFormattedTextField getCampo() {
        return campo;
    }
    
    /**
     * Define o formato da data.
     * @param pattern padrão (ex: "dd/MM/yyyy")
     */
    public void setFormatoData(String pattern) {
        this.dateFormat = new SimpleDateFormat(pattern);
        this.dateFormat.setLenient(false);
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        campo.setText("");
        limparErro();
    }
}

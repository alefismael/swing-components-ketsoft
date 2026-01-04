package campo;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Campo de entrada para CNPJ com máscara automática (00.000.000/0000-00) e validação.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática 00.000.000/0000-00</li>
 *   <li>Validação de dígitos verificadores</li>
 *   <li>Validação configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoCnpj cnpj = new CampoCnpj("CNPJ");
 * cnpj.setObrigatorio(true);
 * cnpj.setValidarCnpj(true);
 * String cnpjLimpo = cnpj.getUnmaskedValue();
 * }</pre>
 * 
 * @author alefi
 */
public class CampoCnpj extends CampoForm<String> {
    
    private final JTextField textField;
    private boolean validarCnpj = true;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoCnpj() {
        this("CNPJ");
    }
    
    /**
     * Construtor com label customizado.
     * @param labelText Texto do label
     */
    public CampoCnpj(String labelText) {
        super(labelText);
        this.textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(180, 28));
        add(textField, BorderLayout.CENTER);
        
        setupMask();
    }
    
    private void setupMask() {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder sb = new StringBuilder(currentText);
                sb.delete(offset, offset + length);
                String newText = formatCnpj(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, null);
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder sb = new StringBuilder(currentText);
                sb.replace(offset, offset + length, text);
                String newText = formatCnpj(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, attrs);
            }
        });
    }
    
    private String formatCnpj(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        
        if (digits.length() > 14) {
            digits = digits.substring(0, 14);
        }
        
        // Formato: 00.000.000/0000-00
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 2 || i == 5) {
                formatted.append(".");
            } else if (i == 8) {
                formatted.append("/");
            } else if (i == 12) {
                formatted.append("-");
            }
            formatted.append(digits.charAt(i));
        }
        
        return formatted.toString();
    }
    
    /**
     * Define se deve validar dígitos verificadores.
     * @param validate true para validar
     * @return this para encadeamento
     */
    public CampoCnpj setValidarCnpj(boolean validate) {
        this.validarCnpj = validate;
        return this;
    }
    
    /**
     * Verifica se o CNPJ é válido usando o algoritmo oficial.
     * @param cnpj CNPJ a validar (com ou sem máscara)
     * @return true se válido
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) return false;
        
        cnpj = cnpj.replaceAll("[^0-9]", "");
        
        if (cnpj.length() != 14) return false;
        if (cnpj.matches("(\\d)\\1{13}")) return false; // Todos dígitos iguais
        
        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso1[i];
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            if (digito1 != Character.getNumericValue(cnpj.charAt(12))) return false;
            
            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso2[i];
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            
            return digito2 == Character.getNumericValue(cnpj.charAt(13));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtém o JTextField interno.
     * @return campo de texto
     */
    public JTextField getTextField() {
        return textField;
    }
    
    @Override
    public String getValue() {
        return textField != null ? textField.getText() : "";
    }
    
    /**
     * Obtém o valor sem máscara (só números).
     * @return CNPJ sem formatação
     */
    public String getUnmaskedValue() {
        return textField != null ? textField.getText().replaceAll("[^0-9]", "") : "";
    }
    
    @Override
    public void setValue(String value) {
        if (value == null) {
            textField.setText("");
        } else {
            textField.setText(formatCnpj(value));
        }
    }
    
    /**
     * Limpa o conteúdo do campo.
     */
    public void limpar() {
        textField.setText("");
        limparErro();
    }
    
    @Override
    public boolean validar() {
        String cnpj = getUnmaskedValue();
        
        if (obrigatorio && cnpj.isEmpty()) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        
        if (!cnpj.isEmpty()) {
            if (cnpj.length() != 14) {
                setMensagemErro(getTextLabel() + " está incompleto");
                return false;
            }
            
            if (validarCnpj && !isValidCnpj(cnpj)) {
                setMensagemErro(getTextLabel() + " inválido");
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        label.setEnabled(enabled);
    }
    
    /**
     * Define a largura preferida.
     * @param width largura em pixels
     * @return this para encadeamento
     */
    public CampoCnpj setPreferredWidth(int width) {
        Dimension size = textField.getPreferredSize();
        textField.setPreferredSize(new Dimension(width, size.height));
        return this;
    }
}

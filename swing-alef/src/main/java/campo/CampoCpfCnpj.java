package campo;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Campo de entrada para CPF ou CNPJ com máscara automática e validação.
 * Detecta automaticamente se é CPF (11 dígitos) ou CNPJ (14 dígitos).
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática CPF (000.000.000-00) ou CNPJ (00.000.000/0000-00)</li>
 *   <li>Validação de dígitos verificadores</li>
 *   <li>Detecção automática CPF/CNPJ</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoCpfCnpj campo = new CampoCpfCnpj("CPF/CNPJ");
 * campo.setObrigatorio(true);
 * String valor = campo.getUnmaskedValue(); // Só números
 * boolean isCnpj = campo.isCnpj();
 * }</pre>
 * 
 * @author alefi
 */
public class CampoCpfCnpj extends CampoForm<String> {
    
    private final JTextField textField;
    private boolean validarDocumento = true;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoCpfCnpj() {
        this("CPF/CNPJ");
    }
    
    /**
     * Construtor com label customizado.
     * @param labelText Texto do label
     */
    public CampoCpfCnpj(String labelText) {
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
                String newText = format(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, null);
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder sb = new StringBuilder(currentText);
                sb.replace(offset, offset + length, text);
                String newText = format(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, attrs);
            }
        });
    }
    
    private String format(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        
        // CNPJ tem 14 dígitos, CPF tem 11
        if (digits.length() > 14) {
            digits = digits.substring(0, 14);
        }
        
        StringBuilder formatted = new StringBuilder();
        
        // Se tem mais de 11 dígitos, formata como CNPJ
        if (digits.length() > 11) {
            // CNPJ: 00.000.000/0000-00
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
        } else {
            // CPF: 000.000.000-00
            for (int i = 0; i < digits.length(); i++) {
                if (i == 3 || i == 6) {
                    formatted.append(".");
                } else if (i == 9) {
                    formatted.append("-");
                }
                formatted.append(digits.charAt(i));
            }
        }
        
        return formatted.toString();
    }
    
    /**
     * Retorna o valor sem máscara (apenas dígitos).
     */
    public String getUnmaskedValue() {
        return textField.getText().replaceAll("[^0-9]", "");
    }
    
    /**
     * Verifica se o documento digitado é um CNPJ.
     */
    public boolean isCnpj() {
        return getUnmaskedValue().length() > 11;
    }
    
    /**
     * Verifica se o documento digitado é um CPF.
     */
    public boolean isCpf() {
        String digits = getUnmaskedValue();
        return digits.length() <= 11 && !digits.isEmpty();
    }
    
    @Override
    public String getValue() {
        return textField.getText().isEmpty() ? null : textField.getText();
    }
    
    @Override
    public void setValue(String value) {
        if (value == null) {
            textField.setText("");
        } else {
            textField.setText(format(value));
        }
    }
    
    @Override
    public boolean validar() {
        String digits = getUnmaskedValue();
        
        if (isObrigatorio() && digits.isEmpty()) {
            return false;
        }
        
        if (!digits.isEmpty() && validarDocumento) {
            if (digits.length() == 11) {
                return validarCpf(digits);
            } else if (digits.length() == 14) {
                return validarCnpj(digits);
            } else {
                return false; // Quantidade de dígitos inválida
            }
        }
        
        return true;
    }
    
    private boolean validarCpf(String cpf) {
        // CPFs inválidos conhecidos
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        try {
            // Validação do primeiro dígito
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            if (digito1 != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }
            
            // Validação do segundo dígito
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            
            return digito2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean validarCnpj(String cnpj) {
        // CNPJs inválidos conhecidos
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        try {
            // Validação do primeiro dígito
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            if (digito1 != Character.getNumericValue(cnpj.charAt(12))) {
                return false;
            }
            
            // Validação do segundo dígito
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            
            return digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isValidarDocumento() {
        return validarDocumento;
    }
    
    public void setValidarDocumento(boolean validar) {
        this.validarDocumento = validar;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
    
    public JTextField getTextField() {
        return textField;
    }
}

package campo;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Campo de entrada para CPF com máscara automática (000.000.000-00) e validação.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Máscara automática 000.000.000-00</li>
 *   <li>Validação de dígitos verificadores</li>
 *   <li>Validação configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoCpf cpf = new CampoCpf("CPF");
 * cpf.setObrigatorio(true);
 * cpf.setValidarCpf(true);
 * String cpfLimpo = cpf.getUnmaskedValue(); // Só números
 * }</pre>
 * 
 * @author alefi
 */
public class CampoCpf extends CampoForm<String> {
    
    private final JTextField textField;
    private boolean validarCpf = true;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoCpf() {
        this("CPF");
    }
    
    /**
     * Construtor com label customizado.
     * @param labelText Texto do label
     */
    public CampoCpf(String labelText) {
        super(labelText);
        this.textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(150, 28));
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
                String newText = formatCpf(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, null);
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder sb = new StringBuilder(currentText);
                sb.replace(offset, offset + length, text);
                String newText = formatCpf(sb.toString());
                fb.replace(0, fb.getDocument().getLength(), newText, attrs);
            }
        });
    }
    
    private String formatCpf(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        
        if (digits.length() > 11) {
            digits = digits.substring(0, 11);
        }
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 3 || i == 6) {
                formatted.append(".");
            } else if (i == 9) {
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
    public CampoCpf setValidarCpf(boolean validate) {
        this.validarCpf = validate;
        return this;
    }
    
    /**
     * Verifica se o CPF é válido usando o algoritmo oficial.
     * @param cpf CPF a validar (com ou sem máscara)
     * @return true se válido
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false; // Todos dígitos iguais
        
        try {
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            if (digito1 != Character.getNumericValue(cpf.charAt(9))) return false;
            
            // Segundo dígito verificador
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
     * @return CPF sem formatação
     */
    public String getUnmaskedValue() {
        return textField != null ? textField.getText().replaceAll("[^0-9]", "") : "";
    }
    
    @Override
    public void setValue(String value) {
        if (value == null) {
            textField.setText("");
        } else {
            textField.setText(formatCpf(value));
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
        String cpf = getUnmaskedValue();
        
        if (obrigatorio && cpf.isEmpty()) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        
        if (!cpf.isEmpty()) {
            if (cpf.length() != 11) {
                setMensagemErro(getTextLabel() + " está incompleto");
                return false;
            }
            
            if (validarCpf && !isValidCpf(cpf)) {
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
    public CampoCpf setPreferredWidth(int width) {
        Dimension size = textField.getPreferredSize();
        textField.setPreferredSize(new Dimension(width, size.height));
        return this;
    }
}

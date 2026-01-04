package campo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente CheckBox estilizado com label integrado.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Estilo consistente com outros campos</li>
 *   <li>API fluente</li>
 *   <li>Listeners de alteração</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoCheckBox ativo = new CampoCheckBox("Ativo", true);
 * ativo.addChangeListener(() -> System.out.println("Alterou: " + ativo.isSelected()));
 * }</pre>
 * 
 * @author alefi
 */
public class CampoCheckBox extends CampoForm<Boolean> {
    
    private final JCheckBox checkBox;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoCheckBox() {
        this("CheckBox", false);
    }
    
    /**
     * Construtor com texto do checkbox.
     * @param text Texto exibido ao lado do checkbox
     */
    public CampoCheckBox(String text) {
        this(text, false);
    }
    
    /**
     * Construtor com texto e valor inicial.
     * @param text Texto exibido ao lado do checkbox
     * @param selected Valor inicial
     */
    public CampoCheckBox(String text, boolean selected) {
        super(""); // Não usa label padrão
        
        // Remove o painel de topo (label + erro) pois checkbox tem seu próprio texto
        for (java.awt.Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                remove(comp);
                break;
            }
        }
        
        this.checkBox = new JCheckBox(text, selected);
        checkBox.setOpaque(false);
        checkBox.setFont(checkBox.getFont().deriveFont(Font.PLAIN, 13f));
        checkBox.setFocusPainted(false);
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        add(checkBox, BorderLayout.CENTER);
    }
    
    /**
     * Define se está selecionado.
     * @param selected true para selecionado
     * @return this para encadeamento
     */
    public CampoCheckBox setSelected(boolean selected) {
        checkBox.setSelected(selected);
        return this;
    }
    
    /**
     * Verifica se está selecionado.
     * @return true se selecionado
     */
    public boolean isSelected() {
        return checkBox.isSelected();
    }
    
    /**
     * Inverte o estado de seleção.
     * @return this para encadeamento
     */
    public CampoCheckBox toggle() {
        checkBox.setSelected(!checkBox.isSelected());
        return this;
    }
    
    /**
     * Define o texto do checkbox.
     * @param text novo texto
     * @return this para encadeamento
     */
    public CampoCheckBox setTexto(String text) {
        checkBox.setText(text);
        return this;
    }
    
    /**
     * Obtém o texto do checkbox.
     * @return texto atual
     */
    public String getTexto() {
        return checkBox.getText();
    }
    
    /**
     * Adiciona listener de alteração.
     * @param listener callback executado ao alterar
     * @return this para encadeamento
     */
    public CampoCheckBox addChangeListener(Runnable listener) {
        checkBox.addActionListener(e -> listener.run());
        return this;
    }
    
    /**
     * Adiciona ActionListener.
     * @param listener listener a adicionar
     * @return this para encadeamento
     */
    public CampoCheckBox addActionListener(ActionListener listener) {
        checkBox.addActionListener(listener);
        return this;
    }
    
    /**
     * Obtém o JCheckBox interno.
     * @return checkbox
     */
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    @Override
    public Boolean getValue() {
        return checkBox != null && checkBox.isSelected();
    }
    
    @Override
    public void setValue(Boolean value) {
        checkBox.setSelected(value != null && value);
    }
    
    /**
     * Desmarca o checkbox.
     */
    public void limpar() {
        checkBox.setSelected(false);
        limparErro();
    }
    
    @Override
    public boolean validar() {
        // Se obrigatório, deve estar marcado
        if (obrigatorio && !checkBox.isSelected()) {
            setMensagemErro(checkBox.getText() + " é obrigatório");
            return false;
        }
        return true;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
    }
}

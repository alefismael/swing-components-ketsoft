package campo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Componente de grupo de RadioButtons com label integrado.
 * Permite selecionar uma opção de um grupo.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Layout horizontal ou vertical</li>
 *   <li>Mapeamento valor-opção</li>
 *   <li>API fluente</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoRadioGroup<String> sexo = new CampoRadioGroup<>("Sexo");
 * sexo.addOption("Masculino", "M")
 *     .addOption("Feminino", "F")
 *     .selectValue("M");
 * }</pre>
 * 
 * @param <T> Tipo do valor associado a cada opção
 * @author alefi
 */
public class CampoRadioGroup<T> extends CampoForm<T> {
    
    private final JPanel optionsPanel;
    private final ButtonGroup buttonGroup;
    private final Map<JRadioButton, T> radioValueMap;
    private final List<JRadioButton> radioButtons;
    private boolean horizontal = true;
    private Runnable changeListener;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoRadioGroup() {
        this("Opções");
    }
    
    /**
     * Construtor com label.
     * @param labelText Texto do label do grupo
     */
    public CampoRadioGroup(String labelText) {
        super(labelText);
        this.optionsPanel = new JPanel();
        this.buttonGroup = new ButtonGroup();
        this.radioValueMap = new LinkedHashMap<>();
        this.radioButtons = new ArrayList<>();
        
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        add(optionsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Define layout horizontal (lado a lado) ou vertical (empilhado).
     * @param horizontal true para horizontal
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
        if (horizontal) {
            optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        } else {
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        }
        return this;
    }
    
    /**
     * Verifica se é layout horizontal.
     * @return true se horizontal
     */
    public boolean isHorizontal() {
        return horizontal;
    }
    
    /**
     * Adiciona uma opção ao grupo.
     * @param text texto visível
     * @param value valor associado
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> addOption(String text, T value) {
        JRadioButton radio = new JRadioButton(text);
        radio.setOpaque(false);
        radio.setFont(radio.getFont().deriveFont(Font.PLAIN, 13f));
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        radio.setFocusPainted(false);
        
        radio.addActionListener(e -> {
            if (changeListener != null) {
                changeListener.run();
            }
        });
        
        buttonGroup.add(radio);
        radioValueMap.put(radio, value);
        radioButtons.add(radio);
        optionsPanel.add(radio);
        
        return this;
    }
    
    /**
     * Adiciona várias opções de uma vez.
     * @param options mapa texto-valor
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> addOptions(Map<String, T> options) {
        for (Map.Entry<String, T> entry : options.entrySet()) {
            addOption(entry.getKey(), entry.getValue());
        }
        return this;
    }
    
    /**
     * Remove todas as opções.
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> clearOptions() {
        for (JRadioButton radio : radioButtons) {
            buttonGroup.remove(radio);
        }
        radioButtons.clear();
        radioValueMap.clear();
        optionsPanel.removeAll();
        optionsPanel.revalidate();
        optionsPanel.repaint();
        return this;
    }
    
    /**
     * Seleciona uma opção pelo valor.
     * @param value valor a selecionar
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> selectValue(T value) {
        for (Map.Entry<JRadioButton, T> entry : radioValueMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                entry.getKey().setSelected(true);
                break;
            }
        }
        return this;
    }
    
    /**
     * Seleciona uma opção pelo índice.
     * @param index índice da opção
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> selectIndex(int index) {
        if (index >= 0 && index < radioButtons.size()) {
            radioButtons.get(index).setSelected(true);
        }
        return this;
    }
    
    /**
     * Obtém o índice selecionado.
     * @return índice ou -1 se nada selecionado
     */
    public int getSelectedIndex() {
        for (int i = 0; i < radioButtons.size(); i++) {
            if (radioButtons.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Adiciona listener de alteração.
     * @param listener callback executado ao alterar
     * @return this para encadeamento
     */
    public CampoRadioGroup<T> addChangeListener(Runnable listener) {
        this.changeListener = listener;
        return this;
    }
    
    /**
     * Obtém a quantidade de opções.
     * @return número de opções
     */
    public int getOptionCount() {
        return radioButtons.size();
    }
    
    @Override
    public T getValue() {
        for (Map.Entry<JRadioButton, T> entry : radioValueMap.entrySet()) {
            if (entry.getKey().isSelected()) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Override
    public void setValue(T value) {
        if (value == null) {
            buttonGroup.clearSelection();
            return;
        }
        selectValue(value);
    }
    
    /**
     * Limpa a seleção.
     */
    public void limpar() {
        buttonGroup.clearSelection();
        limparErro();
    }
    
    @Override
    public boolean validar() {
        if (obrigatorio && getValue() == null) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        return true;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        label.setEnabled(enabled);
        for (JRadioButton radio : radioButtons) {
            radio.setEnabled(enabled);
        }
    }
}

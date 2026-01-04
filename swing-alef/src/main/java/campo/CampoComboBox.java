package campo;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * Componente ComboBox com label integrado.
 * Permite exibir objetos com texto customizado através de um renderer.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Função de exibição customizada</li>
 *   <li>Placeholder "Selecione..."</li>
 *   <li>API fluente</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoComboBox<Estado> estados = new CampoComboBox<>("Estado");
 * estados.setDisplayFunction(Estado::getNome)
 *        .addPlaceholder("Selecione...")
 *        .addItems(listaEstados)
 *        .setObrigatorio(true);
 * }</pre>
 * 
 * @param <T> Tipo dos itens do ComboBox
 * @author alefi
 */
public class CampoComboBox<T> extends CampoForm<T> {
    
    private final JComboBox<T> comboBox;
    private final DefaultComboBoxModel<T> model;
    private Function<T, String> displayFunction;
    
    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoComboBox() {
        this("Seleção", (Function<T, String>) null);
    }
    
    /**
     * Construtor com label.
     * @param labelText Texto do label
     */
    public CampoComboBox(String labelText) {
        this(labelText, (Function<T, String>) null);
    }
    
    /**
     * Construtor com label e função de exibição.
     * @param labelText Texto do label
     * @param displayFunction Função para converter o item em texto de exibição
     */
    public CampoComboBox(String labelText, Function<T, String> displayFunction) {
        super(labelText);
        this.displayFunction = displayFunction;
        this.model = new DefaultComboBoxModel<>();
        this.comboBox = new JComboBox<>(model);
        
        // ComboBox
        comboBox.setPreferredSize(new Dimension(200, 32));
        add(comboBox, BorderLayout.CENTER);
        
        setupRenderer();
    }
    
    /**
     * Construtor com label e itens iniciais.
     * @param labelText Texto do label
     * @param items itens iniciais
     */
    @SafeVarargs
    public CampoComboBox(String labelText, T... items) {
        this(labelText, (Function<T, String>) null);
        if (items != null) {
            for (T item : items) {
                model.addElement(item);
            }
        }
    }
    
    private void setupRenderer() {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value == null) {
                    setText("");
                } else if (displayFunction != null) {
                    @SuppressWarnings("unchecked")
                    T item = (T) value;
                    setText(displayFunction.apply(item));
                } else {
                    setText(value.toString());
                }
                
                return this;
            }
        });
    }
    
    /**
     * Define a função de exibição para os itens.
     * @param displayFunction função que converte item em texto
     * @return this para encadeamento
     */
    public CampoComboBox<T> setDisplayFunction(Function<T, String> displayFunction) {
        this.displayFunction = displayFunction;
        comboBox.repaint();
        return this;
    }
    
    /**
     * Alias para setDisplayFunction.
     * Define como o objeto é convertido em texto para exibição.
     * @param conversor função que converte item em texto
     * @return this para encadeamento
     */
    public CampoComboBox<T> setConversor(Function<T, String> conversor) {
        return setDisplayFunction(conversor);
    }
    
    /**
     * Adiciona um item ao ComboBox.
     * @param item item a adicionar
     * @return this para encadeamento
     */
    public CampoComboBox<T> addItem(T item) {
        model.addElement(item);
        return this;
    }
    
    /**
     * Adiciona vários itens ao ComboBox.
     * @param items lista de itens
     * @return this para encadeamento
     */
    public CampoComboBox<T> addItems(List<T> items) {
        for (T item : items) {
            model.addElement(item);
        }
        return this;
    }
    
    /**
     * Adiciona vários itens ao ComboBox.
     * @param items itens a adicionar
     * @return this para encadeamento
     */
    @SafeVarargs
    public final CampoComboBox<T> addItems(T... items) {
        for (T item : items) {
            model.addElement(item);
        }
        return this;
    }
    
    /**
     * Define os itens do ComboBox (remove os anteriores).
     * @param items nova lista de itens
     * @return this para encadeamento
     */
    public CampoComboBox<T> setItems(List<T> items) {
        model.removeAllElements();
        for (T item : items) {
            model.addElement(item);
        }
        return this;
    }
    
    /**
     * Remove todos os itens do ComboBox.
     * @return this para encadeamento
     */
    public CampoComboBox<T> clearItems() {
        model.removeAllElements();
        return this;
    }
    
    /**
     * Adiciona um item placeholder no início (ex: "Selecione...").
     * @param placeholderText texto do placeholder
     * @return this para encadeamento
     */
    @SuppressWarnings("unchecked")
    public CampoComboBox<T> addPlaceholder(String placeholderText) {
        model.insertElementAt(null, 0);
        comboBox.setSelectedIndex(0);
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value == null) {
                    setText(placeholderText);
                    setForeground(Color.GRAY);
                } else if (displayFunction != null) {
                    setText(displayFunction.apply((T) value));
                } else {
                    setText(value.toString());
                }
                
                return this;
            }
        });
        
        return this;
    }
    
    /**
     * Alias para addPlaceholder.
     * Adiciona uma opção nula no início com o texto especificado.
     * @param text texto para a opção nula
     * @return this para encadeamento
     */
    public CampoComboBox<T> setNullOption(String text) {
        return addPlaceholder(text);
    }
    
    /**
     * Obtém o índice selecionado.
     * @return índice ou -1 se nada selecionado
     */
    public int getSelectedIndex() {
        return comboBox.getSelectedIndex();
    }
    
    /**
     * Define o índice selecionado.
     * @param index índice a selecionar
     * @return this para encadeamento
     */
    public CampoComboBox<T> setSelectedIndex(int index) {
        if (index >= 0 && index < model.getSize()) {
            comboBox.setSelectedIndex(index);
        }
        return this;
    }
    
    /**
     * Obtém a quantidade de itens.
     * @return número de itens
     */
    public int getItemCount() {
        return model.getSize();
    }
    
    /**
     * Obtém um item por índice.
     * @param index índice do item
     * @return item ou null
     */
    public T getItemAt(int index) {
        if (index >= 0 && index < model.getSize()) {
            return model.getElementAt(index);
        }
        return null;
    }
    
    /**
     * Adiciona listener de seleção.
     * @param listener callback executado ao selecionar
     * @return this para encadeamento
     */
    public CampoComboBox<T> addSelectionListener(Runnable listener) {
        comboBox.addActionListener(e -> listener.run());
        return this;
    }
    
    /**
     * Obtém o JComboBox interno.
     * @return comboBox
     */
    public JComboBox<T> getComboBox() {
        return comboBox;
    }
    
    /**
     * Define se o combobox é editável.
     * @param editable true para editável
     * @return this para encadeamento
     */
    public CampoComboBox<T> setEditable(boolean editable) {
        comboBox.setEditable(editable);
        return this;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) comboBox.getSelectedItem();
    }
    
    @Override
    public void setValue(T value) {
        comboBox.setSelectedItem(value);
    }
    
    /**
     * Define o item selecionado (alias para setValue).
     * @param item item a selecionar
     * @return this para encadeamento
     */
    public CampoComboBox<T> setSelectedItem(T item) {
        comboBox.setSelectedItem(item);
        return this;
    }

    /**
     * Limpa a seleção (seleciona primeiro item).
     */
    public void limpar() {
        if (model.getSize() > 0) {
            comboBox.setSelectedIndex(0);
        }
        limparErro();
    }
    
    @Override
    public boolean validar() {
        if (obrigatorio) {
            T value = getValue();
            if (value == null) {
                setMensagemErro(getTextLabel() + " é obrigatório");
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        comboBox.setEnabled(enabled);
        label.setEnabled(enabled);
    }
    
    /**
     * Define a largura preferida.
     * @param width largura em pixels
     * @return this para encadeamento
     */
    public CampoComboBox<T> setPreferredWidth(int width) {
        Dimension size = comboBox.getPreferredSize();
        comboBox.setPreferredSize(new Dimension(width, size.height));
        return this;
    }
}

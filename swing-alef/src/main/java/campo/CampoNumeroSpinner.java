package campo;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Campo numérico com controles de incremento/decremento.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Valores mínimo/máximo configuráveis</li>
 *   <li>Passo de incremento configurável</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoNumeroSpinner quantidade = new CampoNumeroSpinner("Quantidade", 1, 1, 100, 1);
 * quantidade.setObrigatorio(true);
 * int valor = quantidade.getValue();
 * }</pre>
 * 
 * @author alefi
 */
public class CampoNumeroSpinner extends CampoForm<Integer> {
    
    private JSpinner spinner;
    private SpinnerNumberModel model;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoNumeroSpinner() {
        super("Número");
        inicializar(0, 0, 999999, 1);
    }
    
    /**
     * Construtor com título.
     * @param titulo texto do label
     */
    public CampoNumeroSpinner(String titulo) {
        super(titulo);
        inicializar(0, 0, 999999, 1);
    }
    
    /**
     * Construtor completo.
     * @param titulo texto do label
     * @param valorInicial valor inicial
     * @param minimo valor mínimo
     * @param maximo valor máximo
     * @param passo incremento/decremento
     */
    public CampoNumeroSpinner(String titulo, int valorInicial, int minimo, int maximo, int passo) {
        super(titulo);
        inicializar(valorInicial, minimo, maximo, passo);
    }
    
    private void inicializar(int valorInicial, int minimo, int maximo, int passo) {
        model = new SpinnerNumberModel(valorInicial, minimo, maximo, passo);
        spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(100, 28));
        
        // Permite edição direta no campo de texto do spinner
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setEditable(true);
        
        add(spinner, BorderLayout.CENTER);
    }

    @Override
    public Integer getValue() {
        return spinner != null ? (Integer) spinner.getValue() : null;
    }

    @Override
    public void setValue(Integer value) {
        if (spinner != null && value != null) {
            spinner.setValue(value);
        }
    }

    @Override
    public boolean validar() {
        if (obrigatorio && getValue() == null) {
            setMensagemErro(getTextLabel() + " é obrigatório");
            return false;
        }
        return true;
    }
    
    /**
     * Obtém o JSpinner interno.
     * @return spinner
     */
    public JSpinner getSpinner() {
        return spinner;
    }
    
    /**
     * Define o valor mínimo.
     * @param minimo valor mínimo
     * @return this para encadeamento
     */
    public CampoNumeroSpinner setMinimo(int minimo) {
        model.setMinimum(minimo);
        return this;
    }
    
    /**
     * Define o valor máximo.
     * @param maximo valor máximo
     * @return this para encadeamento
     */
    public CampoNumeroSpinner setMaximo(int maximo) {
        model.setMaximum(maximo);
        return this;
    }
    
    /**
     * Define o passo de incremento.
     * @param passo valor do passo
     * @return this para encadeamento
     */
    public CampoNumeroSpinner setPasso(int passo) {
        model.setStepSize(passo);
        return this;
    }
    
    /**
     * Define o range de valores.
     * @param minimo valor mínimo
     * @param maximo valor máximo
     * @return this para encadeamento
     */
    public CampoNumeroSpinner setRange(int minimo, int maximo) {
        model.setMinimum(minimo);
        model.setMaximum(maximo);
        return this;
    }
    
    /**
     * Limpa o campo (define valor zero).
     */
    public void limpar() {
        spinner.setValue(0);
        limparErro();
    }
}

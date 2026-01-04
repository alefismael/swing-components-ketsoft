package nucleo;

/**
 * Interface para componentes que suportam validação.
 * 
 * Implementada por campos de formulário como CampoTexto, CampoEmail, etc.
 * Permite que diálogos e formulários validem automaticamente todos os campos.
 * 
 * <b>Nota:</b> Usamos {@code validar()} ao invés de {@code isValid()} para evitar
 * conflito com {@code Component.isValid()} do Swing.
 * 
 * Exemplo de uso em um campo:
 * <pre>
 * public class CampoTexto extends JPanel implements Validavel {
 *     private boolean obrigatorio = false;
 *     private String mensagemErro;
 *     
 *     public boolean validar() {
 *         if (obrigatorio &amp;&amp; getValue().isEmpty()) {
 *             mensagemErro = "Campo obrigatório";
 *             return false;
 *         }
 *         return true;
 *     }
 *     
 *     public String getMensagemErro() {
 *         return mensagemErro;
 *     }
 * }
 * </pre>
 * 
 * @author alefi
 */
public interface Validavel {
    
    /**
     * Verifica se o valor do campo é válido.
     * @return true se válido
     */
    boolean validar();
    
    /**
     * Retorna a mensagem de erro da última validação.
     * @return mensagem de erro ou null se válido
     */
    String getMensagemErro();
    
    /**
     * Exibe visualmente o estado de erro no componente.
     * Geralmente muda a borda para vermelho e mostra a mensagem.
     */
    default void mostrarErro() {
        // Implementação padrão vazia - campos podem sobrescrever
    }
    
    /**
     * Limpa o estado visual de erro do componente.
     */
    default void limparErro() {
        // Implementação padrão vazia - campos podem sobrescrever
    }
    
    /**
     * Valida e mostra erro se inválido.
     * Método de conveniência que combina validar() e mostrarErro().
     * @return true se válido
     */
    default boolean validarComFeedback() {
        if (validar()) {
            limparErro();
            return true;
        } else {
            mostrarErro();
            return false;
        }
    }
}

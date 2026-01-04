package componente;

import javax.swing.*;
import java.util.function.Supplier;

/**
 * Representa uma etapa do assistente.
 * 
 * @author alefi
 * @since 1.1
 */
public class EtapaAssistente {
    
    private String titulo;
    private Supplier<JPanel> fabricaPainel;
    private JPanel painelCache;
    private Supplier<Boolean> validacao;
    
    public EtapaAssistente(String titulo, Supplier<JPanel> fabricaPainel) {
        this.titulo = titulo;
        this.fabricaPainel = fabricaPainel;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public JPanel getPainel() {
        if (painelCache == null) {
            painelCache = fabricaPainel.get();
        }
        return painelCache;
    }
    
    public void setValidacao(Supplier<Boolean> validacao) {
        this.validacao = validacao;
    }
    
    public boolean validar() {
        if (validacao == null) {
            return true;
        }
        return validacao.get();
    }
    
    /**
     * Limpa o cache do painel (para recriar).
     */
    public void limparCache() {
        painelCache = null;
    }
}

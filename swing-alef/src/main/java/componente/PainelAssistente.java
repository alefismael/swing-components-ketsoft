package componente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Assistente passo-a-passo (wizard) para fluxos de cadastro.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * PainelAssistente wizard = new PainelAssistente();
 * wizard.adicionarEtapa("Dados Pessoais", () -> new PainelDadosPessoais());
 * wizard.adicionarEtapa("Endereço", () -> new PainelEndereco());
 * wizard.adicionarEtapa("Confirmação", () -> new PainelConfirmacao());
 * wizard.setAoConcluir(() -> salvarDados());
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class PainelAssistente extends JPanel {
    
    private List<EtapaAssistente> etapas = new ArrayList<>();
    private int etapaAtual = 0;
    
    private IndicadorEtapas indicador;
    private JPanel painelConteudo;
    private JButton btnAnterior;
    private JButton btnProximo;
    private JButton btnCancelar;
    
    private Runnable aoConcluir;
    private Runnable aoCancelar;
    
    public PainelAssistente() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Indicador de etapas (topo)
        indicador = new IndicadorEtapas();
        add(indicador, BorderLayout.NORTH);
        
        // Conteúdo (centro)
        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(painelConteudo, BorderLayout.CENTER);
        
        // Botões (rodapé)
        JPanel painelBotoes = new JPanel(new BorderLayout());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        
        JPanel botoesNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnAnterior = new JButton("← Anterior");
        btnAnterior.addActionListener(e -> anterior());
        
        btnProximo = new JButton("Próximo →");
        btnProximo.addActionListener(e -> proximo());
        
        botoesNav.add(btnAnterior);
        botoesNav.add(btnProximo);
        
        painelBotoes.add(btnCancelar, BorderLayout.WEST);
        painelBotoes.add(botoesNav, BorderLayout.EAST);
        
        add(painelBotoes, BorderLayout.SOUTH);
    }
    
    /**
     * Adiciona uma etapa ao assistente.
     */
    public void adicionarEtapa(String titulo, Supplier<JPanel> fabricaPainel) {
        EtapaAssistente etapa = new EtapaAssistente(titulo, fabricaPainel);
        etapas.add(etapa);
        indicador.setEtapas(getNomesEtapas());
        
        if (etapas.size() == 1) {
            mostrarEtapa(0);
        }
    }
    
    /**
     * Adiciona uma etapa com validação.
     */
    public void adicionarEtapa(String titulo, Supplier<JPanel> fabricaPainel, Supplier<Boolean> validacao) {
        EtapaAssistente etapa = new EtapaAssistente(titulo, fabricaPainel);
        etapa.setValidacao(validacao);
        etapas.add(etapa);
        indicador.setEtapas(getNomesEtapas());
        
        if (etapas.size() == 1) {
            mostrarEtapa(0);
        }
    }
    
    private String[] getNomesEtapas() {
        return etapas.stream().map(EtapaAssistente::getTitulo).toArray(String[]::new);
    }
    
    private void mostrarEtapa(int indice) {
        if (indice < 0 || indice >= etapas.size()) return;
        
        etapaAtual = indice;
        EtapaAssistente etapa = etapas.get(indice);
        
        painelConteudo.removeAll();
        painelConteudo.add(etapa.getPainel(), BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        
        indicador.setEtapaAtual(indice);
        atualizarBotoes();
    }
    
    private void atualizarBotoes() {
        btnAnterior.setEnabled(etapaAtual > 0);
        
        if (etapaAtual == etapas.size() - 1) {
            btnProximo.setText("Concluir ✓");
        } else {
            btnProximo.setText("Próximo →");
        }
    }
    
    /**
     * Avança para a próxima etapa.
     */
    public void proximo() {
        EtapaAssistente etapaAtualObj = etapas.get(etapaAtual);
        
        // Validar etapa atual
        if (!etapaAtualObj.validar()) {
            return;
        }
        
        if (etapaAtual < etapas.size() - 1) {
            mostrarEtapa(etapaAtual + 1);
        } else {
            concluir();
        }
    }
    
    /**
     * Volta para a etapa anterior.
     */
    public void anterior() {
        if (etapaAtual > 0) {
            mostrarEtapa(etapaAtual - 1);
        }
    }
    
    /**
     * Cancela o assistente.
     */
    public void cancelar() {
        if (aoCancelar != null) {
            aoCancelar.run();
        }
        
        // Fechar janela se estiver em um diálogo
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
    
    private void concluir() {
        if (aoConcluir != null) {
            aoConcluir.run();
        }
    }
    
    /**
     * Define ação ao concluir.
     */
    public void setAoConcluir(Runnable acao) {
        this.aoConcluir = acao;
    }
    
    /**
     * Define ação ao cancelar.
     */
    public void setAoCancelar(Runnable acao) {
        this.aoCancelar = acao;
    }
    
    /**
     * Retorna o painel da etapa atual.
     */
    public JPanel getPainelEtapaAtual() {
        if (etapaAtual >= 0 && etapaAtual < etapas.size()) {
            return etapas.get(etapaAtual).getPainel();
        }
        return null;
    }
    
    /**
     * Retorna o índice da etapa atual.
     */
    public int getEtapaAtual() {
        return etapaAtual;
    }
    
    /**
     * Retorna o total de etapas.
     */
    public int getTotalEtapas() {
        return etapas.size();
    }
    
    /**
     * Vai para uma etapa específica.
     */
    public void irParaEtapa(int indice) {
        mostrarEtapa(indice);
    }
}

package campo;

import nucleo.Validavel;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.function.BiConsumer;

/**
 * Seletor de período com data inicial e final.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * SeletorPeriodo periodo = new SeletorPeriodo();
 * periodo.setPeriodo(LocalDate.now().minusDays(30), LocalDate.now());
 * periodo.setAoAlterar((inicio, fim) -> filtrarDados(inicio, fim));
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class SeletorPeriodo extends JPanel implements Validavel {
    
    private SeletorData seletorInicio;
    private SeletorData seletorFim;
    private JLabel lblAte;
    
    private BiConsumer<LocalDate, LocalDate> aoAlterar;
    private boolean obrigatorio = false;
    
    public SeletorPeriodo() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        seletorInicio = new SeletorData();
        seletorInicio.setAoAlterar(d -> notificarAlteracao());
        
        lblAte = new JLabel("até");
        
        seletorFim = new SeletorData();
        seletorFim.setAoAlterar(d -> notificarAlteracao());
        
        add(seletorInicio);
        add(lblAte);
        add(seletorFim);
    }
    
    /**
     * Construtor com rótulos personalizados.
     */
    public SeletorPeriodo(String rotuloInicio, String rotuloFim) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        add(new JLabel(rotuloInicio));
        
        seletorInicio = new SeletorData();
        seletorInicio.setAoAlterar(d -> notificarAlteracao());
        add(seletorInicio);
        
        add(new JLabel(rotuloFim));
        
        seletorFim = new SeletorData();
        seletorFim.setAoAlterar(d -> notificarAlteracao());
        add(seletorFim);
    }
    
    private void notificarAlteracao() {
        if (aoAlterar != null) {
            aoAlterar.accept(getDataInicio(), getDataFim());
        }
    }
    
    /**
     * Define o período.
     */
    public void setPeriodo(LocalDate inicio, LocalDate fim) {
        seletorInicio.setData(inicio);
        seletorFim.setData(fim);
    }
    
    /**
     * Retorna a data inicial.
     */
    public LocalDate getDataInicio() {
        return seletorInicio.getData();
    }
    
    /**
     * Retorna a data final.
     */
    public LocalDate getDataFim() {
        return seletorFim.getData();
    }
    
    /**
     * Define a data inicial.
     */
    public void setDataInicio(LocalDate data) {
        seletorInicio.setData(data);
    }
    
    /**
     * Define a data final.
     */
    public void setDataFim(LocalDate data) {
        seletorFim.setData(data);
    }
    
    /**
     * Define ação ao alterar período.
     */
    public void setAoAlterar(BiConsumer<LocalDate, LocalDate> acao) {
        this.aoAlterar = acao;
    }
    
    /**
     * Define se o campo é obrigatório.
     */
    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
        seletorInicio.setObrigatorio(obrigatorio);
        seletorFim.setObrigatorio(obrigatorio);
    }
    
    @Override
    public boolean validar() {
        boolean inicioOk = seletorInicio.validar();
        boolean fimOk = seletorFim.validar();
        
        if (!inicioOk || !fimOk) {
            return false;
        }
        
        // Validar que fim >= início
        LocalDate inicio = getDataInicio();
        LocalDate fim = getDataFim();
        
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            seletorFim.mostrarErro("Data final deve ser maior ou igual à inicial");
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getMensagemErro() {
        LocalDate inicio2 = getDataInicio();
        LocalDate fim2 = getDataFim();
        if (obrigatorio && inicio2 == null) {
            return "Data inicial obrigatória";
        }
        if (obrigatorio && fim2 == null) {
            return "Data final obrigatória";
        }
        if (inicio2 != null && fim2 != null && fim2.isBefore(inicio2)) {
            return "Data final deve ser maior ou igual à inicial";
        }
        return null;
    }
    
    @Override
    public void mostrarErro() {
        seletorInicio.mostrarErro();
        seletorFim.mostrarErro();
    }
    
    public void mostrarErro(String mensagem) {
        seletorInicio.mostrarErro(mensagem);
        seletorFim.mostrarErro(mensagem);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        seletorInicio.setEnabled(enabled);
        seletorFim.setEnabled(enabled);
    }
    
    /**
     * Define período predefinido.
     */
    public void setPeriodoPredefinido(PeriodoPredefinido periodo) {
        LocalDate hoje = LocalDate.now();
        
        switch (periodo) {
            case HOJE:
                setPeriodo(hoje, hoje);
                break;
            case ONTEM:
                setPeriodo(hoje.minusDays(1), hoje.minusDays(1));
                break;
            case ULTIMOS_7_DIAS:
                setPeriodo(hoje.minusDays(6), hoje);
                break;
            case ULTIMOS_30_DIAS:
                setPeriodo(hoje.minusDays(29), hoje);
                break;
            case MES_ATUAL:
                setPeriodo(hoje.withDayOfMonth(1), hoje);
                break;
            case MES_ANTERIOR:
                LocalDate mesAnterior = hoje.minusMonths(1);
                setPeriodo(mesAnterior.withDayOfMonth(1), 
                          mesAnterior.withDayOfMonth(mesAnterior.lengthOfMonth()));
                break;
            case ANO_ATUAL:
                setPeriodo(hoje.withDayOfYear(1), hoje);
                break;
        }
    }
    
    /**
     * Limpa o período.
     */
    public void limpar() {
        seletorInicio.limpar();
        seletorFim.limpar();
    }
    
    /**
     * Períodos predefinidos.
     */
    public enum PeriodoPredefinido {
        HOJE, ONTEM, ULTIMOS_7_DIAS, ULTIMOS_30_DIAS, MES_ATUAL, MES_ANTERIOR, ANO_ATUAL
    }
}

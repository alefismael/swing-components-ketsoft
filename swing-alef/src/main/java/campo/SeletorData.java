package campo;

import componente.PainelCalendario;
import nucleo.Validavel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;

/**
 * Campo de seleção de data com popup de calendário.
 * 
 * <h3>Uso:</h3>
 * <pre>{@code
 * SeletorData seletor = new SeletorData();
 * seletor.setData(LocalDate.now());
 * seletor.setAoAlterar(data -> System.out.println("Data: " + data));
 * 
 * LocalDate data = seletor.getData();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class SeletorData extends JPanel implements Validavel {
    
    private JFormattedTextField campoData;
    private JButton btnCalendario;
    private JPopupMenu popupCalendario;
    private PainelCalendario calendario;
    
    private LocalDate data;
    private boolean obrigatorio = false;
    
    private java.util.function.Consumer<LocalDate> aoAlterar;
    
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public SeletorData() {
        setLayout(new BorderLayout(2, 0));
        
        // Campo de texto formatado
        campoData = new JFormattedTextField();
        campoData.setColumns(10);
        campoData.putClientProperty("JTextField.placeholderText", "dd/mm/aaaa");
        
        campoData.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                interpretarTexto();
            }
        });
        
        campoData.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    interpretarTexto();
                }
            }
        });
        
        add(campoData, BorderLayout.CENTER);
        
        // Botão calendário
        btnCalendario = new JButton("📅");
        btnCalendario.setMargin(new Insets(2, 6, 2, 6));
        btnCalendario.setFocusable(false);
        btnCalendario.addActionListener(e -> mostrarCalendario());
        
        add(btnCalendario, BorderLayout.EAST);
        
        // Popup com calendário
        calendario = new PainelCalendario();
        calendario.setAoSelecionar(d -> {
            setData(d);
            popupCalendario.setVisible(false);
        });
        
        popupCalendario = new JPopupMenu();
        popupCalendario.add(calendario);
    }
    
    private void mostrarCalendario() {
        if (data != null) {
            calendario.setDataSelecionada(data);
        }
        popupCalendario.show(this, 0, getHeight());
    }
    
    private void interpretarTexto() {
        String texto = campoData.getText().trim();
        
        if (texto.isEmpty()) {
            setData(null);
            return;
        }
        
        try {
            // Tentar formato completo
            LocalDate novaData = LocalDate.parse(texto, FORMATO);
            setData(novaData);
        } catch (DateTimeParseException e) {
            // Tentar outros formatos
            try {
                texto = texto.replace("-", "/").replace(".", "/");
                String[] partes = texto.split("/");
                
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mes = Integer.parseInt(partes[1]);
                    int ano = Integer.parseInt(partes[2]);
                    
                    // Ano com 2 dígitos
                    if (ano < 100) {
                        ano += ano < 50 ? 2000 : 1900;
                    }
                    
                    setData(LocalDate.of(ano, mes, dia));
                } else {
                    mostrarErro();
                }
            } catch (Exception ex) {
                mostrarErro();
            }
        }
    }
    
    /**
     * Define a data.
     */
    public void setData(LocalDate data) {
        this.data = data;
        
        if (data != null) {
            campoData.setText(data.format(FORMATO));
        } else {
            campoData.setText("");
        }
        
        campoData.setBorder(UIManager.getBorder("TextField.border"));
        
        if (aoAlterar != null) {
            aoAlterar.accept(data);
        }
    }
    
    /**
     * Retorna a data selecionada.
     */
    public LocalDate getData() {
        return data;
    }
    
    /**
     * Define se o campo é obrigatório.
     */
    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
    
    public boolean isObrigatorio() {
        return obrigatorio;
    }
    
    /**
     * Define ação ao alterar data.
     */
    public void setAoAlterar(java.util.function.Consumer<LocalDate> acao) {
        this.aoAlterar = acao;
    }
    
    /**
     * Define data mínima permitida.
     */
    public void setDataMinima(LocalDate minima) {
        // TODO: implementar validação de range
    }
    
    /**
     * Define data máxima permitida.
     */
    public void setDataMaxima(LocalDate maxima) {
        // TODO: implementar validação de range
    }
    
    @Override
    public boolean validar() {
        if (obrigatorio && data == null) {
            return false;
        }
        return true;
    }
    
    @Override
    public String getMensagemErro() {
        if (obrigatorio && data == null) {
            return "Data obrigatória";
        }
        return null;
    }
    
    @Override
    public void mostrarErro() {
        campoData.setBorder(BorderFactory.createLineBorder(new Color(0xF44336), 1));
    }
    
    public void mostrarErro(String mensagem) {
        mostrarErro();
        campoData.setToolTipText(mensagem);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        campoData.setEnabled(enabled);
        btnCalendario.setEnabled(enabled);
    }
    
    /**
     * Limpa a data.
     */
    public void limpar() {
        setData(null);
    }
}

package campo;

import javax.swing.*;
import java.awt.*;

/**
 * Campo composto para endereço completo com busca de CEP.
 * 
 * <h3>Recursos:</h3>
 * <ul>
 *   <li>Campos integrados (CEP, Logradouro, Número, etc)</li>
 *   <li>Botão de busca de CEP</li>
 *   <li>Layout responsivo</li>
 *   <li>Compatível com NetBeans GUI Builder</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * CampoEndereco endereco = new CampoEndereco();
 * endereco.setBuscaCepCallback(cep -> {
 *     // Implementar chamada à API de CEP
 * });
 * }</pre>
 * 
 * @author alefi
 */
public class CampoEndereco extends CampoForm<Void> {

    private CampoCep cep;
    private CampoTexto logradouro;
    private CampoTexto numero;
    private CampoTexto complemento;
    private CampoTexto bairro;
    private CampoTexto cidade;
    private CampoTexto estado;
    private CampoTexto pais;

    private JButton btnBuscarCep;
    private Runnable buscaCepCallback;

    /**
     * Construtor padrão para GUI Builder.
     */
    public CampoEndereco() {
        super("Endereço");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(8, 4, 12, 4));
        label.setOpaque(true);
        label.setForeground(UIManager.getColor("Label.foreground"));
        label.setBackground(UIManager.getColor("Panel.background"));

        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 2));
        buildLayout();
    }

    private void buildLayout() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Linha 1: CEP + Número
        cep = new CampoCep();
        btnBuscarCep = new JButton("Buscar");
        btnBuscarCep.setPreferredSize(new Dimension(80, 28));
        btnBuscarCep.addActionListener(e -> {
            if (buscaCepCallback != null) {
                buscaCepCallback.run();
            }
        });

        JPanel cepPanel = new JPanel(new BorderLayout(5, 0));
        cepPanel.setOpaque(false);
        cepPanel.add(cep, BorderLayout.CENTER);
        cepPanel.add(btnBuscarCep, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        content.add(cepPanel, gbc);

        numero = new CampoTexto("Número");
        numero.getField().setPreferredSize(new Dimension(100, 28));
        gbc.gridx = 1;
        content.add(numero, gbc);

        // Linha 2: Logradouro + Complemento
        logradouro = new CampoTexto("Logradouro");
        logradouro.getField().setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 0;
        gbc.gridy = 1;
        content.add(logradouro, gbc);

        complemento = new CampoTexto("Complemento");
        complemento.getField().setPreferredSize(new Dimension(150, 28));
        gbc.gridx = 1;
        content.add(complemento, gbc);

        // Linha 3: Bairro + Cidade
        bairro = new CampoTexto("Bairro");
        bairro.getField().setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 0;
        gbc.gridy = 2;
        content.add(bairro, gbc);

        cidade = new CampoTexto("Cidade");
        cidade.getField().setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        content.add(cidade, gbc);

        // Linha 4: Estado + País
        estado = new CampoTexto("Estado");
        estado.getField().setPreferredSize(new Dimension(100, 28));
        gbc.gridx = 0;
        gbc.gridy = 3;
        content.add(estado, gbc);

        pais = new CampoTexto("País");
        pais.getField().setPreferredSize(new Dimension(180, 28));
        pais.setValue("Brasil");
        gbc.gridx = 1;
        content.add(pais, gbc);

        add(content, BorderLayout.CENTER);
    }
    
    /**
     * Define o callback de busca de CEP.
     * @param callback função executada ao clicar em Buscar
     */
    public void setBuscaCepCallback(Runnable callback) {
        this.buscaCepCallback = callback;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void value) {
        // Não aplicável - usar métodos individuais
    }

    @Override
    public boolean validar() {
        if (cep == null || logradouro == null || numero == null) {
            return true; // Durante construção, considerar válido
        }
        
        boolean valido = true;
        
        if (obrigatorio) {
            valido = cep.validar() 
                && !logradouro.getValue().isBlank()
                && !numero.getValue().isBlank()
                && !cidade.getValue().isBlank();
            
            if (!valido) {
                setMensagemErro("Preencha os campos obrigatórios do endereço");
            }
        }
        
        return valido;
    }
    
    // Getters para os campos individuais
    
    public CampoCep getCep() {
        return cep;
    }
    
    public CampoTexto getLogradouro() {
        return logradouro;
    }
    
    public CampoTexto getNumero() {
        return numero;
    }
    
    public CampoTexto getComplemento() {
        return complemento;
    }
    
    public CampoTexto getBairro() {
        return bairro;
    }
    
    public CampoTexto getCidade() {
        return cidade;
    }
    
    public CampoTexto getEstado() {
        return estado;
    }
    
    public CampoTexto getPais() {
        return pais;
    }
    
    /**
     * Preenche os campos com dados do endereço.
     * @param logradouro rua/avenida
     * @param bairro bairro
     * @param cidade cidade
     * @param estado UF
     */
    public void preencherEndereco(String logradouro, String bairro, String cidade, String estado) {
        this.logradouro.setValue(logradouro);
        this.bairro.setValue(bairro);
        this.cidade.setValue(cidade);
        this.estado.setValue(estado);
    }
    
    /**
     * Limpa todos os campos.
     */
    public void limpar() {
        cep.limpar();
        logradouro.limpar();
        numero.limpar();
        complemento.limpar();
        bairro.limpar();
        cidade.limpar();
        estado.limpar();
        pais.setValue("Brasil");
        limparErro();
    }
}

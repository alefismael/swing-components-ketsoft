package exemplo;

import campo.*;
import componente.Toast;

import javax.swing.*;
import java.awt.*;

/**
 * Exemplo demonstrando todos os campos de formulário disponíveis.
 * 
 * Campos disponíveis:
 * - CampoTexto (texto livre)
 * - CampoSenha (senha com toggle)
 * - CampoEmail (validação de email)
 * - CampoTelefone (máscara de telefone)
 * - CampoCpf (validação de CPF)
 * - CampoCnpj (validação de CNPJ)
 * - CampoMoeda (formatação de moeda)
 * - CampoData (seleção de data)
 * - CampoComboBox (seleção em lista)
 * - CampoCheckBox (checkbox)
 * - CampoNumeroSpinner (números)
 * 
 * @author alefi
 */
public class ExemploCamposFormulario extends JPanel {
    
    // Campos
    private CampoTexto campoNome;
    private CampoTexto campoEmail;
    private CampoSenha campoSenha;
    private CampoTelefone campoTelefone;
    private CampoCpf campoCpf;
    private CampoCnpj campoCnpj;
    private CampoMoeda campoSalario;
    private CampoData campoNascimento;
    private CampoComboBox<String> campoEstado;
    private CampoCheckBox campoAtivo;
    private CampoNumeroSpinner campoIdade;
    private CampoCep campoCep;
    
    public ExemploCamposFormulario() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ======= Painel de campos (GridBagLayout para controle preciso) =======
        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 0, 2, 0);
        
        // --- Texto ---
        campoNome = new CampoTexto("Nome Completo");
        campoNome.setObrigatorio(true);
        campoNome.setMinLength(3);
        campoNome.setMaxLength(100);
        painelCampos.add(campoNome, gbc); gbc.gridy++;
        
        // --- Email ---
        campoEmail = new CampoTexto("Email");
        campoEmail.setObrigatorio(true);
        painelCampos.add(campoEmail, gbc); gbc.gridy++;
        
        // --- Senha ---
        campoSenha = new CampoSenha("Senha");
        campoSenha.setObrigatorio(true);
        campoSenha.setMinLength(6);
        painelCampos.add(campoSenha, gbc); gbc.gridy++;
        
        // --- Telefone ---
        campoTelefone = new CampoTelefone("Telefone");
        painelCampos.add(campoTelefone, gbc); gbc.gridy++;
        
        // --- CPF ---
        campoCpf = new CampoCpf("CPF");
        campoCpf.setObrigatorio(true);
        painelCampos.add(campoCpf, gbc); gbc.gridy++;
        
        // --- CNPJ ---
        campoCnpj = new CampoCnpj("CNPJ");
        painelCampos.add(campoCnpj, gbc); gbc.gridy++;
        
        // --- Moeda ---
        campoSalario = new CampoMoeda("Salário");
        painelCampos.add(campoSalario, gbc); gbc.gridy++;
        
        // --- CEP ---
        campoCep = new CampoCep("CEP");
        painelCampos.add(campoCep, gbc); gbc.gridy++;
        
        // --- Data ---
        campoNascimento = new CampoData("Nascimento");
        painelCampos.add(campoNascimento, gbc); gbc.gridy++;
        
        // --- ComboBox ---
        String[] estados = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", 
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", 
            "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        campoEstado = new CampoComboBox<>("Estado", estados);
        campoEstado.setObrigatorio(true);
        painelCampos.add(campoEstado, gbc); gbc.gridy++;
        
        // --- Spinner (valorInicial, minimo, maximo, passo) ---
        campoIdade = new CampoNumeroSpinner("Idade", 18, 0, 150, 1);
        painelCampos.add(campoIdade, gbc); gbc.gridy++;
        
        // --- CheckBox ---
        campoAtivo = new CampoCheckBox("Cliente Ativo");
        campoAtivo.setValue(true);
        painelCampos.add(campoAtivo, gbc); gbc.gridy++;
        
        // Espaço vertical para empurrar campos para cima
        gbc.weighty = 1.0;
        painelCampos.add(new JPanel(), gbc);
        
        // Scroll para os campos
        JScrollPane scroll = new JScrollPane(painelCampos);
        scroll.setBorder(BorderFactory.createTitledBorder("Campos de Formulário"));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        
        // ======= Botões =======
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnValidar = new JButton("Validar Tudo");
        btnValidar.addActionListener(e -> validarTudo());
        
        JButton btnLimpar = new JButton("Limpar Campos");
        btnLimpar.addActionListener(e -> limparCampos());
        
        JButton btnMostrarValores = new JButton("Mostrar Valores");
        btnMostrarValores.addActionListener(e -> mostrarValores());
        
        painelBotoes.add(btnValidar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnMostrarValores);
        
        add(painelBotoes, BorderLayout.SOUTH);
    }
    
    private void validarTudo() {
        CampoForm<?>[] campos = {
            campoNome, campoEmail, campoSenha, campoTelefone,
            campoCpf, campoCnpj, campoSalario, campoCep,
            campoNascimento, campoEstado, campoIdade, campoAtivo
        };
        
        boolean todosValidos = true;
        StringBuilder erros = new StringBuilder();
        
        for (CampoForm<?> campo : campos) {
            campo.limparErro();
            if (!campo.validar()) {
                todosValidos = false;
                campo.mostrarErro();
                String erro = campo.getMensagemErro();
                if (erro != null && !erro.isEmpty()) {
                    erros.append("• ").append(erro).append("\n");
                }
            }
        }
        
        if (todosValidos) {
            Toast.success(this, "Todos os campos estão válidos!");
        } else {
            Toast.error(this, "Há campos inválidos!");
            if (erros.length() > 0) {
                JOptionPane.showMessageDialog(this, 
                    erros.toString(), 
                    "Erros de Validação", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void limparCampos() {
        campoNome.setValue("");
        campoEmail.setValue("");
        campoSenha.setValue("");
        campoTelefone.setValue("");
        campoCpf.setValue("");
        campoCnpj.setValue("");
        campoSalario.setValue(0.0);
        campoCep.setValue("");
        campoNascimento.setValue(null);
        campoEstado.setValue(null);
        campoIdade.setValue(0);
        campoAtivo.setValue(false);
        
        // Limpar erros
        CampoForm<?>[] campos = {
            campoNome, campoEmail, campoSenha, campoTelefone,
            campoCpf, campoCnpj, campoSalario, campoCep,
            campoNascimento, campoEstado, campoIdade, campoAtivo
        };
        for (CampoForm<?> campo : campos) {
            campo.limparErro();
        }
        
        Toast.info(this, "Campos limpos!");
    }
    
    private void mostrarValores() {
        StringBuilder sb = new StringBuilder();
        sb.append("Valores atuais:\n\n");
        sb.append("Nome: ").append(campoNome.getValue()).append("\n");
        sb.append("Email: ").append(campoEmail.getValue()).append("\n");
        sb.append("Senha: ").append(campoSenha.getValue()).append("\n");
        sb.append("Telefone: ").append(campoTelefone.getValue()).append("\n");
        sb.append("CPF: ").append(campoCpf.getValue()).append("\n");
        sb.append("CNPJ: ").append(campoCnpj.getValue()).append("\n");
        sb.append("Salário: R$ ").append(String.format("%.2f", campoSalario.getValue())).append("\n");
        sb.append("CEP: ").append(campoCep.getValue()).append("\n");
        sb.append("Nascimento: ").append(campoNascimento.getValue()).append("\n");
        sb.append("Estado: ").append(campoEstado.getValue()).append("\n");
        sb.append("Idade: ").append(campoIdade.getValue()).append("\n");
        sb.append("Ativo: ").append(campoAtivo.getValue()).append("\n");
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Valores", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Método main para execução standalone.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            // Usar tema padrão
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exemplo Campos de Formulário");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 700);
            frame.setLocationRelativeTo(null);
            frame.add(new ExemploCamposFormulario());
            frame.setVisible(true);
        });
    }
}

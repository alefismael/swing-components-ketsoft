package exemplo;

import campo.*;
import componente.Toast;
import dialogo.DialogoFormulario;
import dialogo.DialogoUtil;
import nucleo.FabricaDialogo;
import painel.PainelCrud;
import tabela.TabelaBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exemplo de PainelCrud usando a nova arquitetura.
 * 
 * Este exemplo demonstra:
 * - Uso de PainelCrud com getters abstratos
 * - DialogoFormulario para entrada de dados
 * - Campos validados (CampoTexto, CampoEmail, CampoTelefone)
 * - Integração com Toast e DialogoUtil
 * 
 * NOTA: Este painel pode ser criado no NetBeans GUI Builder!
 * Basta criar um "JPanel Form" e estender de PainelCrud.
 * 
 * @author alefi
 */
public class ExemploPainelClientes extends PainelCrud {
    
    // Componentes (seriam criados pelo GUI Builder no NetBeans)
    private JTable tblClientes;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnDeletar;
    private JTextField txtFiltro;
    private JLabel lblStatus;
    private JScrollPane scrollPane;
    
    // Dados simulados
    private List<Cliente> clientes = new ArrayList<>();
    private int proximoId = 1;
    
    // Campos do diálogo (para acesso no FabricaDialogo)
    private CampoTexto campoNome;
    private CampoTexto campoEmail;
    private CampoTexto campoTelefone;
    
    // Classe interna para representar Cliente
    private static class Cliente {
        int id;
        String nome;
        String email;
        String telefone;
        
        Cliente(int id, String nome, String email, String telefone) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.telefone = telefone;
        }
        
        Object[] toArray() {
            return new Object[]{id, nome, email, telefone};
        }
    }
    
    public ExemploPainelClientes() {
        initComponents();
        configurarCrud();
        carregarDadosIniciais();
    }
    
    /**
     * Inicializa os componentes.
     * No NetBeans, este método seria gerado automaticamente pelo GUI Builder.
     */
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ======= Painel superior (filtro + botões) =======
        JPanel painelSuperior = new JPanel(new BorderLayout(10, 0));
        
        // Filtro à esquerda
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel lblFiltro = new JLabel("Filtrar:");
        txtFiltro = new JTextField();
        txtFiltro.setPreferredSize(new Dimension(200, 28));
        painelFiltro.add(lblFiltro);
        painelFiltro.add(txtFiltro);
        
        // Botões à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnNovo = new JButton("Novo");
        btnEditar = new JButton("Editar");
        btnDeletar = new JButton("Excluir");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnDeletar);
        
        painelSuperior.add(painelFiltro, BorderLayout.WEST);
        painelSuperior.add(painelBotoes, BorderLayout.EAST);
        
        add(painelSuperior, BorderLayout.NORTH);
        
        // ======= Tabela central =======
        tblClientes = new TabelaBase();
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "Email", "Telefone"}, 0
        );
        tblClientes.setModel(modelo);
        scrollPane = new JScrollPane(tblClientes);
        add(scrollPane, BorderLayout.CENTER);
        
        // ======= Status inferior =======
        JPanel painelInferior = new JPanel(new BorderLayout());
        lblStatus = new JLabel("0 registros");
        lblStatus.setHorizontalAlignment(JLabel.RIGHT);
        painelInferior.add(lblStatus, BorderLayout.EAST);
        
        add(painelInferior, BorderLayout.SOUTH);
    }
    
    private void carregarDadosIniciais() {
        adicionarCliente("João Silva", "joao@email.com", "(11) 98765-4321");
        adicionarCliente("Maria Santos", "maria@email.com", "(11) 98765-4322");
        adicionarCliente("Pedro Oliveira", "pedro@email.com", "(11) 98765-4323");
        adicionarCliente("Ana Costa", "ana@email.com", "(11) 98765-4324");
    }
    
    private void adicionarCliente(String nome, String email, String telefone) {
        Cliente c = new Cliente(proximoId++, nome, email, telefone);
        clientes.add(c);
        adicionarLinha(c.toArray());
    }
    
    // ==================== Implementação PainelCrud ====================
    
    @Override
    protected JTable getTabela() {
        return tblClientes;
    }
    
    @Override
    protected JButton getBtnNovo() {
        return btnNovo;
    }
    
    @Override
    protected JButton getBtnEditar() {
        return btnEditar;
    }
    
    @Override
    protected JButton getBtnDeletar() {
        return btnDeletar;
    }
    
    @Override
    protected JTextField getTxtFiltro() {
        return txtFiltro;
    }
    
    @Override
    protected JLabel getLblStatus() {
        return lblStatus;
    }
    
    @Override
    protected FabricaDialogo fabricaDialogo() {
        return new FabricaDialogo() {
            
            @Override
            public DialogoFormulario criarDialogoNovo(JFrame owner) {
                DialogoFormulario dialog = new DialogoFormulario(owner, "Novo Cliente");
                
                campoNome = new CampoTexto("Nome");
                campoNome.setObrigatorio(true);
                campoEmail = new CampoTexto("Email");
                campoTelefone = new CampoTexto("Telefone");
                
                dialog.adicionarCampo(campoNome);
                dialog.adicionarCampo(campoEmail);
                dialog.adicionarCampo(campoTelefone);
                
                return dialog;
            }
            
            @Override
            public DialogoFormulario criarDialogoEditar(JFrame owner, int linha, Object[] dadosLinha) {
                DialogoFormulario dialog = criarDialogoNovo(owner);
                dialog.setTitle("Editar Cliente");
                
                // Preencher campos com dados existentes
                campoNome.setValue((String) dadosLinha[1]);
                campoEmail.setValue((String) dadosLinha[2]);
                campoTelefone.setValue((String) dadosLinha[3]);
                
                return dialog;
            }
        };
    }
    
    @Override
    protected void salvarNovo() {
        adicionarCliente(
            campoNome.getValue(),
            campoEmail.getValue(),
            campoTelefone.getValue()
        );
        Toast.success(this, "Cliente cadastrado com sucesso!");
    }
    
    @Override
    protected void salvarEdicao(int linha, Object[] dadosOriginais) {
        int id = (int) dadosOriginais[0];
        
        // Atualizar na lista
        for (Cliente c : clientes) {
            if (c.id == id) {
                c.nome = campoNome.getValue();
                c.email = campoEmail.getValue();
                c.telefone = campoTelefone.getValue();
                break;
            }
        }
        
        // Atualizar na tabela
        Object[] novosDados = {id, campoNome.getValue(), campoEmail.getValue(), campoTelefone.getValue()};
        atualizarLinha(linha, novosDados);
        
        Toast.success(this, "Cliente atualizado com sucesso!");
    }
    
    @Override
    protected boolean deletar(int linha, Object[] dados) {
        int id = (int) dados[0];
        clientes.removeIf(c -> c.id == id);
        Toast.success(this, "Cliente excluído!");
        return true;
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
            JFrame frame = new JFrame("Exemplo PainelCrud - Clientes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new ExemploPainelClientes());
            frame.setVisible(true);
        });
    }
}

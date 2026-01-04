package painel;

import componente.Toast;
import dialogo.DialogoFormulario;
import dialogo.DialogoUtil;
import nucleo.FabricaDialogo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Painel CRUD visual-friendly para uso com NetBeans GUI Builder.
 * 
 * <h3>Estratégia:</h3>
 * Esta classe usa métodos abstratos para OBTER os componentes da subclasse.
 * Assim, a subclasse cria a interface visual no GUI Builder (visível no designer)
 * e esta classe fornece toda a lógica CRUD.
 * 
 * <h3>Como usar:</h3>
 * <ol>
 *   <li>Crie uma nova classe JPanel Form no NetBeans</li>
 *   <li>Mude a herança de JPanel para PainelCrud</li>
 *   <li>No GUI Builder, adicione: JTable, botões (Novo, Editar, Deletar), campo filtro, label status</li>
 *   <li>Implemente os métodos abstratos getTabela(), getBtnNovo(), etc.</li>
 *   <li>Chame configurarCrud() no final do construtor</li>
 * </ol>
 * 
 * <h3>Exemplo:</h3>
 * <pre>{@code
 * public class CadastroCliente extends PainelCrud {
 *     public CadastroCliente() {
 *         initComponents();  // GUI Builder cria os componentes
 *         configurarCrud();  // Conecta a lógica CRUD
 *     }
 *     
 *     // Retorna os componentes criados pelo GUI Builder
 *     protected JTable getTabela() { return tblClientes; }
 *     protected JButton getBtnNovo() { return btnNovo; }
 *     protected JButton getBtnEditar() { return btnEditar; }
 *     protected JButton getBtnDeletar() { return btnDeletar; }
 *     protected JTextField getTxtFiltro() { return txtFiltro; }  // opcional
 *     protected JLabel getLblStatus() { return lblStatus; }      // opcional
 *     
 *     // Implementa a lógica de negócio
 *     protected FabricaDialogo fabricaDialogo() { ... }
 *     protected void salvarNovo() { ... }
 *     protected void salvarEdicao(int linha, Object[] dados) { ... }
 *     protected boolean deletar(int linha, Object[] dados) { ... }
 * }
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 */
public abstract class PainelCrud extends JPanel {

    private boolean crudConfigurado = false;

    /**
     * Construtor padrão.
     * Subclasse deve chamar initComponents() e depois configurarCrud().
     */
    protected PainelCrud() {
        // Subclasse deve chamar initComponents() e depois configurarCrud()
    }

    // ==================== MÉTODOS ABSTRATOS PARA COMPONENTES ====================
    
    /**
     * Retorna a JTable criada pelo GUI Builder.
     * @return JTable do formulário
     */
    protected abstract JTable getTabela();
    
    /**
     * Retorna o botão Novo criado pelo GUI Builder.
     * @return JButton Novo
     */
    protected abstract JButton getBtnNovo();
    
    /**
     * Retorna o botão Editar criado pelo GUI Builder.
     * @return JButton Editar
     */
    protected abstract JButton getBtnEditar();
    
    /**
     * Retorna o botão Deletar criado pelo GUI Builder.
     * @return JButton Deletar
     */
    protected abstract JButton getBtnDeletar();
    
    /**
     * Retorna o campo de filtro (opcional).
     * Retorne null se não usar filtro.
     * @return JTextField filtro ou null
     */
    protected JTextField getTxtFiltro() {
        return null;
    }
    
    /**
     * Retorna o label de status (opcional).
     * Retorne null se não usar status.
     * @return JLabel status ou null
     */
    protected JLabel getLblStatus() {
        return null;
    }

    // ==================== CONFIGURAÇÃO ====================
    
    /**
     * Configura toda a lógica CRUD.
     * Chame este método no construtor da subclasse, APÓS initComponents().
     * 
     * <p>Este método executa na ordem:</p>
     * <ol>
     *   <li>Configura a tabela (seleção única, etc.)</li>
     *   <li>{@link #configurarModelo()} - hook para configurar TabelaModelo</li>
     *   <li>Conecta os botões</li>
     *   <li>Configura filtro</li>
     *   <li>{@link #carregarDados()} - hook para carregar dados iniciais</li>
     *   <li>Atualiza status</li>
     * </ol>
     */
    protected final void configurarCrud() {
        if (crudConfigurado) return;
        crudConfigurado = true;
        
        JTable tabela = getTabela();
        
        // Configura a tabela
        if (tabela != null) {
            tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabela.getTableHeader().setReorderingAllowed(false);
        }
        
        // Hook: subclasse configura o TabelaModelo<T> com colunas
        configurarModelo();
        
        // Conecta os botões
        conectarBotoes();
        
        // Configura filtro se existir
        JTextField filtro = getTxtFiltro();
        if (filtro != null) {
            configurarFiltro(filtro);
        }
        
        // Hook: subclasse carrega dados do banco/memória
        carregarDados();
        
        atualizarStatus();
    }
    
    /**
     * Hook para configurar o modelo da tabela.
     * 
     * <p>Sobrescreva este método para configurar um {@link tabela.TabelaModelo}
     * com as colunas desejadas:</p>
     * 
     * <pre>{@code
     * @Override
     * protected void configurarModelo() {
     *     modelo = new TabelaModelo<>();
     *     modelo.addColuna("ID", Cliente::getId)
     *           .addColuna("Nome", Cliente::getNome)
     *           .addColuna("Email", Cliente::getEmail);
     *     getTabela().setModel(modelo);
     * }
     * }</pre>
     * 
     * <p>Implementação padrão não faz nada (usa o modelo do GUI Builder).</p>
     */
    protected void configurarModelo() {
        // Hook - subclasse pode sobrescrever
    }
    
    /**
     * Hook para carregar dados iniciais na tabela.
     * 
     * <p>Sobrescreva este método para carregar dados do banco ou outra fonte:</p>
     * 
     * <pre>{@code
     * @Override
     * protected void carregarDados() {
     *     List<Cliente> clientes = clienteRepository.findAll();
     *     modelo.setDados(clientes);
     * }
     * }</pre>
     * 
     * <p>Implementação padrão não faz nada.</p>
     */
    protected void carregarDados() {
        // Hook - subclasse pode sobrescrever
    }
    
    private void conectarBotoes() {
        JButton btnNovo = getBtnNovo();
        JButton btnEditar = getBtnEditar();
        JButton btnDeletar = getBtnDeletar();
        
        if (btnNovo != null) {
            btnNovo.addActionListener(e -> onNovo());
        }
        if (btnEditar != null) {
            btnEditar.addActionListener(e -> onEditar());
        }
        if (btnDeletar != null) {
            btnDeletar.addActionListener(e -> onDeletar());
        }
        
        // Duplo clique na tabela abre edição
        JTable tabela = getTabela();
        if (tabela != null) {
            tabela.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                        onEditar();
                    }
                }
            });
        }
    }
    
    private void configurarFiltro(JTextField filtro) {
        filtro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                filtrar(filtro.getText());
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
    }

    // ==================== AÇÕES CRUD ====================
    
    private void onNovo() {
        FabricaDialogo fabrica = fabricaDialogo();
        if (fabrica == null) {
            System.out.println("fabricaDialogo() retornou null - implemente o método!");
            return;
        }
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogoFormulario dialogo = fabrica.criarDialogoNovo(owner);
        if (dialogo == null) return;
        dialogo.mostrarDialogo(this::salvarNovo);
    }

    private void onEditar() {
        Object[] dados = obterLinhaAtual();
        if (dados == null) {
            Toast.warning(this, "Selecione um registro para editar!");
            return;
        }
        FabricaDialogo fabrica = fabricaDialogo();
        if (fabrica == null) return;
        
        int linha = obterLinhaSelecionada();
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogoFormulario dialogo = fabrica.criarDialogoEditar(owner, linha, dados);
        if (dialogo == null) return;
        dialogo.mostrarDialogo(() -> salvarEdicao(linha, dados));
    }

    private void onDeletar() {
        Object[] dados = obterLinhaAtual();
        if (dados == null) {
            Toast.warning(this, "Selecione um registro para excluir!");
            return;
        }
        int linha = obterLinhaSelecionada();
        if (!DialogoUtil.confirmarExclusao(this, "Registro")) {
            return;
        }
        boolean ok = deletar(linha, dados);
        if (ok && removerLinhaAtual()) {
            System.out.println("Registro removido!");
        }
    }

    // ==================== MÉTODOS DE TABELA ====================
    
    /**
     * Define as colunas da tabela.
     * @param colunas nomes das colunas
     */
    public void definirColunas(String[] colunas) {
        JTable tabela = getTabela();
        if (tabela != null && tabela.getModel() instanceof DefaultTableModel) {
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.setColumnIdentifiers(colunas);
        }
    }
    
    /**
     * Limpa todos os dados da tabela.
     */
    public void limparTabela() {
        JTable tabela = getTabela();
        if (tabela != null && tabela.getModel() instanceof DefaultTableModel) {
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.setRowCount(0);
            atualizarStatus();
        }
    }
    
    /**
     * Adiciona uma linha à tabela.
     * @param dados dados da linha
     */
    public void adicionarLinha(Object[] dados) {
        JTable tabela = getTabela();
        if (tabela != null && tabela.getModel() instanceof DefaultTableModel) {
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.addRow(dados);
            atualizarStatus();
        }
    }
    
    /**
     * Atualiza uma linha existente.
     * @param linha índice da linha
     * @param dados novos dados
     */
    public void atualizarLinha(int linha, Object[] dados) {
        JTable tabela = getTabela();
        if (tabela != null && tabela.getModel() instanceof DefaultTableModel) {
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            for (int i = 0; i < dados.length && i < modelo.getColumnCount(); i++) {
                modelo.setValueAt(dados[i], linha, i);
            }
        }
    }
    
    /**
     * Remove a linha selecionada.
     * @return true se removeu
     */
    public boolean removerLinhaAtual() {
        JTable tabela = getTabela();
        if (tabela != null) {
            int linhaView = tabela.getSelectedRow();
            if (linhaView >= 0) {
                // Converte índice da view para o modelo (importante quando há filtro)
                int linhaModel = tabela.convertRowIndexToModel(linhaView);
                javax.swing.table.TableModel modelo = tabela.getModel();
                if (modelo instanceof DefaultTableModel) {
                    ((DefaultTableModel) modelo).removeRow(linhaModel);
                    atualizarStatus();
                    return true;
                }
                // Para TabelaModelo ou outros, a remoção deve ser feita via deletar()
            }
        }
        return false;
    }
    
    /**
     * Obtém os dados da linha selecionada.
     * @return dados ou null
     */
    public Object[] obterLinhaAtual() {
        JTable tabela = getTabela();
        if (tabela != null) {
            int linhaView = tabela.getSelectedRow();
            if (linhaView >= 0) {
                // Converte índice da view para o modelo
                int linhaModel = tabela.convertRowIndexToModel(linhaView);
                javax.swing.table.TableModel modelo = tabela.getModel();
                Object[] dados = new Object[modelo.getColumnCount()];
                for (int i = 0; i < modelo.getColumnCount(); i++) {
                    dados[i] = modelo.getValueAt(linhaModel, i);
                }
                return dados;
            }
        }
        return null;
    }
    
    /**
     * Obtém o índice da linha selecionada (no modelo, não na view).
     * @return índice ou -1
     */
    public int obterLinhaSelecionada() {
        JTable tabela = getTabela();
        if (tabela != null) {
            int linhaView = tabela.getSelectedRow();
            if (linhaView >= 0) {
                return tabela.convertRowIndexToModel(linhaView);
            }
        }
        return -1;
    }
    
    /**
     * Filtra a tabela por texto.
     * @param texto texto do filtro
     */
    public void filtrar(String texto) {
        JTable tabela = getTabela();
        if (tabela == null) return;
        
        // Configura RowSorter se ainda não existe
        if (tabela.getRowSorter() == null) {
            tabela.setRowSorter(new TableRowSorter<>(tabela.getModel()));
        }
        
        if (tabela.getRowSorter() instanceof TableRowSorter) {
            @SuppressWarnings("unchecked")
            TableRowSorter<? extends javax.swing.table.TableModel> sorter = 
                (TableRowSorter<? extends javax.swing.table.TableModel>) tabela.getRowSorter();
            
            if (texto == null || texto.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try {
                    // Filtro case-insensitive em todas as colunas
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                } catch (java.util.regex.PatternSyntaxException e) {
                    // Ignora padrão inválido
                }
            }
            atualizarStatus();
        }
    }
    
    /**
     * Atualiza o label de status.
     */
    public void atualizarStatus() {
        JLabel lblStatus = getLblStatus();
        JTable tabela = getTabela();
        
        if (lblStatus != null && tabela != null) {
            int total = tabela.getModel().getRowCount();
            int visiveis = tabela.getRowCount();
            if (total == visiveis) {
                lblStatus.setText(total + " registro" + (total != 1 ? "s" : ""));
            } else {
                lblStatus.setText(visiveis + " de " + total + " registros (filtrado)");
            }
        }
    }

    // ==================== MÉTODOS ABSTRATOS DE NEGÓCIO ====================
    
    /**
     * Factory para criar diálogos de formulário.
     * @return factory de diálogos ou null se não usar diálogos
     */
    protected abstract FabricaDialogo fabricaDialogo();
    
    /**
     * Salva um novo registro.
     * Chamado após o usuário confirmar o diálogo de novo registro.
     */
    protected abstract void salvarNovo();
    
    /**
     * Salva edição de um registro.
     * @param linha índice da linha no modelo
     * @param dadosOriginais dados originais antes da edição
     */
    protected abstract void salvarEdicao(int linha, Object[] dadosOriginais);
    
    /**
     * Deleta um registro.
     * @param linha índice da linha no modelo
     * @param dadosLinha dados da linha
     * @return true se deletou com sucesso
     */
    protected abstract boolean deletar(int linha, Object[] dadosLinha);
    
    @Override
    public Dimension getPreferredSize() {
        if (java.beans.Beans.isDesignTime()) {
            return new Dimension(1280, 720);
        }
        return super.getPreferredSize();
    }
}

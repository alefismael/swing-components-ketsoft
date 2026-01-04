package campo;

import nucleo.Validavel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Campo para seleção de arquivos.
 * 
 * <p>Permite selecionar um ou múltiplos arquivos do sistema,
 * com filtro de extensões.</p>
 * 
 * <h3>Uso básico:</h3>
 * <pre>{@code
 * CampoArquivo campoDoc = new CampoArquivo("Documento");
 * campoDoc.setFiltro("Documentos PDF", "pdf");
 * 
 * // Obter arquivo selecionado
 * File arquivo = campoDoc.getArquivo();
 * }</pre>
 * 
 * <h3>Múltiplos arquivos:</h3>
 * <pre>{@code
 * CampoArquivo campoAnexos = new CampoArquivo("Anexos");
 * campoAnexos.setMultiplo(true);
 * campoAnexos.setFiltro("Imagens", "jpg", "png", "gif");
 * 
 * List<File> arquivos = campoAnexos.getArquivos();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class CampoArquivo extends JPanel implements Validavel {
    
    private JLabel lblRotulo;
    private JTextField txtCaminho;
    private JButton btnSelecionar;
    private JButton btnLimpar;
    private JLabel lblErro;
    
    private File arquivo;
    private List<File> arquivos = new ArrayList<>();
    private boolean multiplo = false;
    private boolean obrigatorio = false;
    private boolean selecionarPasta = false;
    private FileNameExtensionFilter filtro;
    private String diretorioInicial;
    
    /**
     * Cria um campo de arquivo sem rótulo.
     */
    public CampoArquivo() {
        this(null);
    }
    
    /**
     * Cria um campo de arquivo com rótulo.
     * 
     * @param rotulo texto do rótulo
     */
    public CampoArquivo(String rotulo) {
        initComponents(rotulo);
    }
    
    private void initComponents(String rotulo) {
        setLayout(new BorderLayout(5, 2));
        setOpaque(false);
        
        // Rótulo
        if (rotulo != null && !rotulo.isEmpty()) {
            lblRotulo = new JLabel(rotulo);
            add(lblRotulo, BorderLayout.NORTH);
        }
        
        // Painel central
        JPanel painelCentro = new JPanel(new BorderLayout(5, 0));
        painelCentro.setOpaque(false);
        
        // Campo de texto (somente leitura)
        txtCaminho = new JTextField();
        txtCaminho.setEditable(false);
        txtCaminho.setBackground(UIManager.getColor("TextField.background"));
        painelCentro.add(txtCaminho, BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        painelBotoes.setOpaque(false);
        
        btnSelecionar = new JButton("...");
        btnSelecionar.setPreferredSize(new Dimension(30, txtCaminho.getPreferredSize().height));
        btnSelecionar.setToolTipText("Selecionar arquivo");
        btnSelecionar.addActionListener(e -> selecionarArquivo());
        
        btnLimpar = new JButton("✕");
        btnLimpar.setPreferredSize(new Dimension(30, txtCaminho.getPreferredSize().height));
        btnLimpar.setToolTipText("Limpar seleção");
        btnLimpar.setEnabled(false);
        btnLimpar.addActionListener(e -> limpar());
        
        painelBotoes.add(btnSelecionar);
        painelBotoes.add(btnLimpar);
        
        painelCentro.add(painelBotoes, BorderLayout.EAST);
        
        add(painelCentro, BorderLayout.CENTER);
        
        // Label de erro
        lblErro = new JLabel(" ");
        lblErro.setForeground(UIManager.getColor("TextField.errorForeground"));
        lblErro.setFont(lblErro.getFont().deriveFont(11f));
        add(lblErro, BorderLayout.SOUTH);
    }
    
    /**
     * Abre diálogo para selecionar arquivo.
     */
    public void selecionarArquivo() {
        JFileChooser chooser = new JFileChooser();
        
        if (selecionarPasta) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Selecionar Pasta");
        } else {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogTitle("Selecionar Arquivo");
            chooser.setMultiSelectionEnabled(multiplo);
            
            if (filtro != null) {
                chooser.setFileFilter(filtro);
            }
        }
        
        if (diretorioInicial != null) {
            chooser.setCurrentDirectory(new File(diretorioInicial));
        } else if (arquivo != null && arquivo.getParentFile() != null) {
            chooser.setCurrentDirectory(arquivo.getParentFile());
        }
        
        int resultado = chooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            if (multiplo && !selecionarPasta) {
                File[] selecionados = chooser.getSelectedFiles();
                arquivos.clear();
                for (File f : selecionados) {
                    arquivos.add(f);
                }
                arquivo = selecionados.length > 0 ? selecionados[0] : null;
                atualizarCampo();
            } else {
                setArquivo(chooser.getSelectedFile());
            }
            limparErro();
        }
    }
    
    /**
     * Define o arquivo selecionado.
     * 
     * @param arquivo arquivo
     */
    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
        this.arquivos.clear();
        if (arquivo != null) {
            this.arquivos.add(arquivo);
        }
        atualizarCampo();
    }
    
    /**
     * Define os arquivos selecionados.
     * 
     * @param arquivos lista de arquivos
     */
    public void setArquivos(List<File> arquivos) {
        this.arquivos = new ArrayList<>(arquivos);
        this.arquivo = arquivos.isEmpty() ? null : arquivos.get(0);
        atualizarCampo();
    }
    
    /**
     * Retorna o arquivo selecionado.
     * 
     * @return arquivo ou null
     */
    public File getArquivo() {
        return arquivo;
    }
    
    /**
     * Retorna todos os arquivos selecionados.
     * 
     * @return lista de arquivos
     */
    public List<File> getArquivos() {
        return new ArrayList<>(arquivos);
    }
    
    /**
     * Retorna o caminho do arquivo.
     * 
     * @return caminho ou null
     */
    public String getCaminho() {
        return arquivo != null ? arquivo.getAbsolutePath() : null;
    }
    
    /**
     * Limpa a seleção.
     */
    public void limpar() {
        this.arquivo = null;
        this.arquivos.clear();
        txtCaminho.setText("");
        btnLimpar.setEnabled(false);
    }
    
    private void atualizarCampo() {
        if (arquivos.isEmpty()) {
            txtCaminho.setText("");
            btnLimpar.setEnabled(false);
        } else if (arquivos.size() == 1) {
            txtCaminho.setText(arquivos.get(0).getAbsolutePath());
            btnLimpar.setEnabled(true);
        } else {
            txtCaminho.setText(arquivos.size() + " arquivos selecionados");
            btnLimpar.setEnabled(true);
        }
    }
    
    /**
     * Define se permite múltipla seleção.
     * 
     * @param multiplo true para múltipla seleção
     */
    public void setMultiplo(boolean multiplo) {
        this.multiplo = multiplo;
    }
    
    /**
     * Define se deve selecionar pasta ao invés de arquivo.
     * 
     * @param selecionarPasta true para selecionar pasta
     */
    public void setSelecionarPasta(boolean selecionarPasta) {
        this.selecionarPasta = selecionarPasta;
    }
    
    /**
     * Define se o campo é obrigatório.
     * 
     * @param obrigatorio true se obrigatório
     */
    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
    
    /**
     * Define o filtro de extensões.
     * 
     * @param descricao descrição do filtro
     * @param extensoes extensões aceitas (sem ponto)
     */
    public void setFiltro(String descricao, String... extensoes) {
        this.filtro = new FileNameExtensionFilter(descricao, extensoes);
    }
    
    /**
     * Define o diretório inicial do seletor.
     * 
     * @param diretorio caminho do diretório
     */
    public void setDiretorioInicial(String diretorio) {
        this.diretorioInicial = diretorio;
    }
    
    /**
     * Define o texto do placeholder.
     * 
     * @param placeholder texto
     */
    public void setPlaceholder(String placeholder) {
        txtCaminho.putClientProperty("JTextField.placeholderText", placeholder);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtCaminho.setEnabled(enabled);
        btnSelecionar.setEnabled(enabled);
        btnLimpar.setEnabled(enabled && arquivo != null);
    }
    
    // === Validavel ===
    
    @Override
    public boolean validar() {
        if (obrigatorio && arquivo == null) {
            return false;
        }
        return true;
    }
    
    @Override
    public String getMensagemErro() {
        if (obrigatorio && arquivo == null) {
            return selecionarPasta ? "Pasta é obrigatória" : "Arquivo é obrigatório";
        }
        return null;
    }
    
    @Override
    public void mostrarErro() {
        String mensagem = getMensagemErro();
        if (mensagem != null) {
            lblErro.setText(mensagem);
        }
        txtCaminho.putClientProperty("JComponent.outline", "error");
    }
    
    /**
     * Mostra uma mensagem de erro específica.
     * @param mensagem mensagem a exibir
     */
    public void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        txtCaminho.putClientProperty("JComponent.outline", "error");
    }
    
    @Override
    public void limparErro() {
        lblErro.setText(" ");
        txtCaminho.putClientProperty("JComponent.outline", null);
    }
}

package campo;

import nucleo.Validavel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Campo para seleção e preview de imagens.
 * 
 * <p>Permite selecionar uma imagem do sistema de arquivos,
 * exibe um preview e pode redimensionar automaticamente.</p>
 * 
 * <h3>Uso básico:</h3>
 * <pre>{@code
 * CampoImagem campoFoto = new CampoImagem("Foto");
 * campoFoto.setTamanhoMaximo(200, 200);
 * 
 * // Obter imagem
 * BufferedImage img = campoFoto.getImagem();
 * 
 * // Ou como bytes
 * byte[] bytes = campoFoto.getImagemBytes();
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class CampoImagem extends JPanel implements Validavel {
    
    private JLabel lblPreview;
    private JLabel lblRotulo;
    private JLabel lblErro;
    private JButton btnSelecionar;
    private JButton btnLimpar;
    
    private BufferedImage imagem;
    private int larguraMaxima = 150;
    private int alturaMaxima = 150;
    private int larguraPreview = 150;
    private int alturaPreview = 150;
    private boolean obrigatorio = false;
    private String textoVazio = "Clique para selecionar";
    
    /**
     * Cria um campo de imagem sem rótulo.
     */
    public CampoImagem() {
        this(null);
    }
    
    /**
     * Cria um campo de imagem com rótulo.
     * 
     * @param rotulo texto do rótulo
     */
    public CampoImagem(String rotulo) {
        initComponents(rotulo);
    }
    
    private void initComponents(String rotulo) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);
        
        // Rótulo
        if (rotulo != null && !rotulo.isEmpty()) {
            lblRotulo = new JLabel(rotulo);
            add(lblRotulo, BorderLayout.NORTH);
        }
        
        // Painel central com preview
        JPanel painelCentro = new JPanel(new BorderLayout(5, 5));
        painelCentro.setOpaque(false);
        
        // Preview
        lblPreview = new JLabel(textoVazio);
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(larguraPreview, alturaPreview));
        lblPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        lblPreview.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblPreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selecionarImagem();
                } else if (e.getClickCount() == 2 && imagem != null) {
                    visualizarImagem();
                }
            }
        });
        
        painelCentro.add(lblPreview, BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        painelBotoes.setOpaque(false);
        
        btnSelecionar = new JButton("Selecionar");
        btnSelecionar.addActionListener(e -> selecionarImagem());
        
        btnLimpar = new JButton("Limpar");
        btnLimpar.setEnabled(false);
        btnLimpar.addActionListener(e -> limparImagem());
        
        painelBotoes.add(btnSelecionar);
        painelBotoes.add(btnLimpar);
        
        painelCentro.add(painelBotoes, BorderLayout.SOUTH);
        
        add(painelCentro, BorderLayout.CENTER);
        
        // Label de erro
        lblErro = new JLabel(" ");
        lblErro.setForeground(UIManager.getColor("TextField.errorForeground"));
        lblErro.setFont(lblErro.getFont().deriveFont(11f));
        add(lblErro, BorderLayout.SOUTH);
    }
    
    /**
     * Abre diálogo para selecionar imagem.
     */
    public void selecionarImagem() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar Imagem");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Imagens (*.jpg, *.jpeg, *.png, *.gif, *.bmp)", 
            "jpg", "jpeg", "png", "gif", "bmp"
        ));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            carregarImagem(chooser.getSelectedFile());
        }
    }
    
    /**
     * Carrega imagem de um arquivo.
     * 
     * @param arquivo arquivo de imagem
     */
    public void carregarImagem(File arquivo) {
        try {
            BufferedImage img = ImageIO.read(arquivo);
            if (img != null) {
                setImagem(img);
                limparErro();
            } else {
                mostrarErro("Formato de imagem não suportado");
            }
        } catch (IOException e) {
            mostrarErro("Erro ao carregar imagem: " + e.getMessage());
        }
    }
    
    /**
     * Define a imagem atual.
     * 
     * @param img imagem
     */
    public void setImagem(BufferedImage img) {
        if (img == null) {
            limparImagem();
            return;
        }
        
        // Redimensionar se necessário
        this.imagem = redimensionar(img, larguraMaxima, alturaMaxima);
        
        // Atualizar preview
        atualizarPreview();
        btnLimpar.setEnabled(true);
    }
    
    /**
     * Define a imagem a partir de bytes.
     * 
     * @param bytes bytes da imagem
     */
    public void setImagemBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            limparImagem();
            return;
        }
        
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            setImagem(img);
        } catch (IOException e) {
            mostrarErro("Erro ao carregar imagem");
        }
    }
    
    /**
     * Define a imagem a partir de Base64.
     * 
     * @param base64 string Base64 da imagem
     */
    public void setImagemBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            limparImagem();
            return;
        }
        
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            setImagemBytes(bytes);
        } catch (IllegalArgumentException e) {
            mostrarErro("Base64 inválido");
        }
    }
    
    /**
     * Retorna a imagem atual.
     * 
     * @return imagem ou null
     */
    public BufferedImage getImagem() {
        return imagem;
    }
    
    /**
     * Retorna a imagem como bytes PNG.
     * 
     * @return bytes ou null
     */
    public byte[] getImagemBytes() {
        if (imagem == null) return null;
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagem, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Retorna a imagem como Base64.
     * 
     * @return string Base64 ou null
     */
    public String getImagemBase64() {
        byte[] bytes = getImagemBytes();
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Limpa a imagem selecionada.
     */
    public void limparImagem() {
        this.imagem = null;
        lblPreview.setIcon(null);
        lblPreview.setText(textoVazio);
        btnLimpar.setEnabled(false);
    }
    
    /**
     * Abre visualizador de imagem.
     */
    public void visualizarImagem() {
        if (imagem == null) return;
        
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner instanceof Frame ? (Frame) owner : null, "Visualizar Imagem", true);
        dialog.setLayout(new BorderLayout());
        
        JLabel lblImg = new JLabel(new ImageIcon(imagem));
        JScrollPane scroll = new JScrollPane(lblImg);
        dialog.add(scroll, BorderLayout.CENTER);
        
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dialog.dispose());
        JPanel painelBotao = new JPanel();
        painelBotao.add(btnFechar);
        dialog.add(painelBotao, BorderLayout.SOUTH);
        
        dialog.setSize(Math.min(imagem.getWidth() + 50, 800), Math.min(imagem.getHeight() + 100, 600));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
    
    private void atualizarPreview() {
        if (imagem == null) {
            lblPreview.setIcon(null);
            lblPreview.setText(textoVazio);
            return;
        }
        
        BufferedImage preview = redimensionar(imagem, larguraPreview - 10, alturaPreview - 10);
        lblPreview.setIcon(new ImageIcon(preview));
        lblPreview.setText(null);
    }
    
    private BufferedImage redimensionar(BufferedImage original, int maxLargura, int maxAltura) {
        int larguraOriginal = original.getWidth();
        int alturaOriginal = original.getHeight();
        
        if (larguraOriginal <= maxLargura && alturaOriginal <= maxAltura) {
            return original;
        }
        
        double ratio = Math.min(
            (double) maxLargura / larguraOriginal,
            (double) maxAltura / alturaOriginal
        );
        
        int novaLargura = (int) (larguraOriginal * ratio);
        int novaAltura = (int) (alturaOriginal * ratio);
        
        BufferedImage redimensionada = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = redimensionada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, novaLargura, novaAltura, null);
        g2d.dispose();
        
        return redimensionada;
    }
    
    /**
     * Define o tamanho máximo da imagem armazenada.
     * 
     * @param largura largura máxima
     * @param altura altura máxima
     */
    public void setTamanhoMaximo(int largura, int altura) {
        this.larguraMaxima = largura;
        this.alturaMaxima = altura;
    }
    
    /**
     * Define o tamanho do preview.
     * 
     * @param largura largura do preview
     * @param altura altura do preview
     */
    public void setTamanhoPreview(int largura, int altura) {
        this.larguraPreview = largura;
        this.alturaPreview = altura;
        lblPreview.setPreferredSize(new Dimension(largura, altura));
        revalidate();
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
     * Define o texto exibido quando não há imagem.
     * 
     * @param texto texto
     */
    public void setTextoVazio(String texto) {
        this.textoVazio = texto;
        if (imagem == null) {
            lblPreview.setText(texto);
        }
    }
    
    // === Validavel ===
    
    @Override
    public boolean validar() {
        if (obrigatorio && imagem == null) {
            return false;
        }
        return true;
    }
    
    @Override
    public String getMensagemErro() {
        if (obrigatorio && imagem == null) {
            return "Imagem é obrigatória";
        }
        return null;
    }
    
    @Override
    public void mostrarErro() {
        String mensagem = getMensagemErro();
        if (mensagem != null) {
            lblErro.setText(mensagem);
        }
        lblPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("TextField.errorForeground")),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    /**
     * Mostra uma mensagem de erro específica.
     * @param mensagem mensagem a exibir
     */
    public void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("TextField.errorForeground")),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    @Override
    public void limparErro() {
        lblErro.setText(" ");
        lblPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
}

package componente;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Painel de avatar circular com iniciais ou imagem.
 * 
 * <p>Exibe um avatar circular que pode mostrar as iniciais de um nome
 * ou uma imagem. Ideal para listas de usuários, perfis, etc.</p>
 * 
 * <h3>Uso com iniciais:</h3>
 * <pre>{@code
 * PainelAvatar avatar = new PainelAvatar("João Silva");
 * avatar.setTamanho(48);
 * }</pre>
 * 
 * <h3>Uso com imagem:</h3>
 * <pre>{@code
 * PainelAvatar avatar = new PainelAvatar();
 * avatar.setImagem(imagemPerfil);
 * avatar.setTamanho(64);
 * }</pre>
 * 
 * @author alefi
 * @since 1.1
 */
public class PainelAvatar extends JPanel {
    
    private String nome;
    private String iniciais;
    private BufferedImage imagem;
    private int tamanho = 40;
    private Color corFundo;
    private Color corTexto = Color.WHITE;
    private Font fonte;
    
    // Paleta de cores para avatars
    private static final Color[] CORES = {
        new Color(0xE53935), // Vermelho
        new Color(0xD81B60), // Rosa
        new Color(0x8E24AA), // Roxo
        new Color(0x5E35B1), // Roxo escuro
        new Color(0x3949AB), // Índigo
        new Color(0x1E88E5), // Azul
        new Color(0x039BE5), // Azul claro
        new Color(0x00ACC1), // Ciano
        new Color(0x00897B), // Teal
        new Color(0x43A047), // Verde
        new Color(0x7CB342), // Verde claro
        new Color(0xC0CA33), // Lima
        new Color(0xFDD835), // Amarelo
        new Color(0xFFB300), // Âmbar
        new Color(0xFB8C00), // Laranja
        new Color(0xF4511E), // Laranja escuro
    };
    
    /**
     * Cria um avatar vazio.
     */
    public PainelAvatar() {
        this(null);
    }
    
    /**
     * Cria um avatar com nome (mostra iniciais).
     * 
     * @param nome nome completo
     */
    public PainelAvatar(String nome) {
        setOpaque(false);
        setNome(nome);
        atualizarTamanho();
    }
    
    /**
     * Define o nome (gera iniciais automaticamente).
     * 
     * @param nome nome completo
     */
    public void setNome(String nome) {
        this.nome = nome;
        this.iniciais = gerarIniciais(nome);
        this.corFundo = gerarCor(nome);
        repaint();
    }
    
    /**
     * Define as iniciais diretamente.
     * 
     * @param iniciais iniciais (1-2 caracteres)
     */
    public void setIniciais(String iniciais) {
        this.iniciais = iniciais != null ? iniciais.toUpperCase() : null;
        repaint();
    }
    
    /**
     * Define a imagem do avatar.
     * 
     * @param imagem imagem
     */
    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
        repaint();
    }
    
    /**
     * Carrega imagem de um arquivo.
     * 
     * @param arquivo arquivo de imagem
     */
    public void setImagem(File arquivo) {
        try {
            this.imagem = ImageIO.read(arquivo);
            repaint();
        } catch (IOException e) {
            this.imagem = null;
        }
    }
    
    /**
     * Define a imagem a partir de bytes.
     * 
     * @param bytes bytes da imagem
     */
    public void setImagemBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            this.imagem = null;
            repaint();
            return;
        }
        
        try {
            this.imagem = ImageIO.read(new ByteArrayInputStream(bytes));
            repaint();
        } catch (IOException e) {
            this.imagem = null;
        }
    }
    
    /**
     * Define a imagem a partir de Base64.
     * 
     * @param base64 string Base64
     */
    public void setImagemBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            this.imagem = null;
            repaint();
            return;
        }
        
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            setImagemBytes(bytes);
        } catch (IllegalArgumentException e) {
            this.imagem = null;
        }
    }
    
    /**
     * Define o tamanho do avatar (largura e altura).
     * 
     * @param tamanho tamanho em pixels
     */
    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
        atualizarTamanho();
        repaint();
    }
    
    /**
     * Define a cor de fundo.
     * 
     * @param cor cor
     */
    public void setCorFundo(Color cor) {
        this.corFundo = cor;
        repaint();
    }
    
    /**
     * Define a cor do texto (iniciais).
     * 
     * @param cor cor
     */
    public void setCorTexto(Color cor) {
        this.corTexto = cor;
        repaint();
    }
    
    /**
     * Limpa o avatar.
     */
    public void limpar() {
        this.nome = null;
        this.iniciais = null;
        this.imagem = null;
        repaint();
    }
    
    private void atualizarTamanho() {
        Dimension dim = new Dimension(tamanho, tamanho);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        revalidate();
    }
    
    private String gerarIniciais(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "?";
        }
        
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        } else {
            return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
        }
    }
    
    private Color gerarCor(String nome) {
        if (nome == null || nome.isEmpty()) {
            return CORES[0];
        }
        int hash = Math.abs(nome.hashCode());
        return CORES[hash % CORES.length];
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        
        Shape clip = new Ellipse2D.Float(x, y, size, size);
        
        if (imagem != null) {
            // Desenhar imagem circular
            g2d.setClip(clip);
            g2d.drawImage(imagem, x, y, size, size, null);
        } else {
            // Desenhar círculo com iniciais
            g2d.setColor(corFundo != null ? corFundo : CORES[0]);
            g2d.fill(clip);
            
            if (iniciais != null) {
                g2d.setColor(corTexto);
                
                // Calcular tamanho da fonte
                int fontSize = size * 40 / 100;
                if (fonte != null) {
                    g2d.setFont(fonte.deriveFont((float) fontSize));
                } else {
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(iniciais);
                int textHeight = fm.getAscent();
                
                int textX = x + (size - textWidth) / 2;
                int textY = y + (size + textHeight) / 2 - fm.getDescent() / 2;
                
                g2d.drawString(iniciais, textX, textY);
            }
        }
        
        // Borda sutil
        g2d.setClip(null);
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(clip);
        
        g2d.dispose();
    }
    
    /**
     * Retorna o nome atual.
     * 
     * @return nome
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Retorna as iniciais atuais.
     * 
     * @return iniciais
     */
    public String getIniciais() {
        return iniciais;
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
     * Verifica se tem imagem definida.
     * 
     * @return true se tem imagem
     */
    public boolean temImagem() {
        return imagem != null;
    }
}

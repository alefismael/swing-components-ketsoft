package util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Utilitário para carregamento e manipulação de imagens.
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Carregar imagem do classpath
 * Image img = ImagemUtil.carregar("/icons/logo.png");
 * 
 * // Carregar como ícone
 * Icon icon = ImagemUtil.carregarIcone("/icons/botao.png");
 * 
 * // Carregar e redimensionar
 * Image imgRedim = ImagemUtil.carregar("/icons/logo.png", 32, 32);
 * 
 * // Redimensionar imagem existente
 * Image imgPequena = ImagemUtil.redimensionar(img, 16, 16);
 * }</pre>
 * 
 * @author alefi
 */
public final class ImagemUtil {
    
    private ImagemUtil() {
        // Classe utilitária - não instanciar
    }
    
    /**
     * Carrega uma imagem do classpath (resources).
     * 
     * @param caminhoRecurso Caminho do recurso (ex: "/icons/logo.png" ou "icons/logo.png")
     * @return Image carregada ou null se não encontrada
     */
    public static Image carregar(String caminhoRecurso) {
        String caminho = normalizarCaminho(caminhoRecurso);
        
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(caminho)) {
            if (is == null) {
                System.err.println("ImagemUtil: Imagem não encontrada no classpath: " + caminho);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("ImagemUtil: Erro ao carregar imagem: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carrega uma imagem do classpath e redimensiona.
     * 
     * @param caminhoRecurso Caminho do recurso
     * @param largura Largura desejada
     * @param altura Altura desejada
     * @return Image redimensionada ou null se não encontrada
     */
    public static Image carregar(String caminhoRecurso, int largura, int altura) {
        Image img = carregar(caminhoRecurso);
        if (img == null) return null;
        return redimensionar(img, largura, altura);
    }
    
    /**
     * Carrega um ícone do classpath.
     * 
     * @param caminhoRecurso Caminho do recurso
     * @return Icon carregado ou null se não encontrado
     */
    public static Icon carregarIcone(String caminhoRecurso) {
        Image img = carregar(caminhoRecurso);
        if (img == null) return null;
        return new ImageIcon(img);
    }
    
    /**
     * Carrega um ícone do classpath e redimensiona.
     * 
     * @param caminhoRecurso Caminho do recurso
     * @param largura Largura desejada
     * @param altura Altura desejada
     * @return Icon redimensionado ou null se não encontrado
     */
    public static Icon carregarIcone(String caminhoRecurso, int largura, int altura) {
        Image img = carregar(caminhoRecurso, largura, altura);
        if (img == null) return null;
        return new ImageIcon(img);
    }
    
    /**
     * Redimensiona uma imagem mantendo qualidade.
     * 
     * @param imagem Imagem original
     * @param largura Nova largura
     * @param altura Nova altura
     * @return Imagem redimensionada
     */
    public static Image redimensionar(Image imagem, int largura, int altura) {
        if (imagem == null) return null;
        return imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
    }
    
    /**
     * Redimensiona mantendo proporção (baseado na largura).
     * 
     * @param imagem Imagem original
     * @param largura Nova largura
     * @return Imagem redimensionada proporcionalmente
     */
    public static Image redimensionarProporcional(Image imagem, int largura) {
        if (imagem == null) return null;
        
        int larguraOriginal = imagem.getWidth(null);
        int alturaOriginal = imagem.getHeight(null);
        
        if (larguraOriginal <= 0 || alturaOriginal <= 0) {
            return imagem;
        }
        
        double proporcao = (double) largura / larguraOriginal;
        int novaAltura = (int) (alturaOriginal * proporcao);
        
        return redimensionar(imagem, largura, novaAltura);
    }
    
    /**
     * Carrega imagem de uma URL.
     * 
     * @param url URL da imagem
     * @return Image carregada ou null se erro
     */
    public static Image carregarDeUrl(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            System.err.println("ImagemUtil: Erro ao carregar imagem da URL: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Converte Image para BufferedImage.
     * 
     * @param imagem Image a converter
     * @return BufferedImage
     */
    public static BufferedImage paraBufferedImage(Image imagem) {
        if (imagem instanceof BufferedImage) {
            return (BufferedImage) imagem;
        }
        
        // Aguarda carregamento completo
        int largura = imagem.getWidth(null);
        int altura = imagem.getHeight(null);
        
        if (largura <= 0 || altura <= 0) {
            return null;
        }
        
        BufferedImage buffered = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        buffered.getGraphics().drawImage(imagem, 0, 0, null);
        return buffered;
    }
    
    /**
     * Verifica se um recurso de imagem existe no classpath.
     * 
     * @param caminhoRecurso Caminho do recurso
     * @return true se existe
     */
    public static boolean existe(String caminhoRecurso) {
        String caminho = normalizarCaminho(caminhoRecurso);
        return Thread.currentThread().getContextClassLoader().getResource(caminho) != null;
    }
    
    /**
     * Normaliza o caminho removendo a barra inicial se presente.
     */
    private static String normalizarCaminho(String caminho) {
        if (caminho == null) return "";
        return caminho.startsWith("/") ? caminho.substring(1) : caminho;
    }
}

package exemplo;

import campo.CampoArquivo;
import campo.CampoImagem;
import componente.PainelAvatar;
import componente.Toast;
import componente.VisualizadorImagem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Painel de exemplo dos componentes de mídia.
 */
public class ExemploMidia extends JPanel {
    
    private CampoImagem campoImagem;
    
    public ExemploMidia() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Painel esquerdo: CampoImagem e CampoArquivo
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        
        painelEsquerdo.add(criarPainelImagem());
        painelEsquerdo.add(Box.createVerticalStrut(15));
        painelEsquerdo.add(criarPainelArquivo());
        painelEsquerdo.add(Box.createVerticalGlue());
        
        // Painel direito: PainelAvatar e Visualizador
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));
        
        painelDireito.add(criarPainelAvatares());
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(criarPainelVisualizador());
        painelDireito.add(Box.createVerticalGlue());
        
        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelEsquerdo, painelDireito);
        split.setDividerLocation(350);
        split.setResizeWeight(0.4);
        
        add(split, BorderLayout.CENTER);
    }
    
    private JPanel criarPainelImagem() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "CampoImagem",
            TitledBorder.LEFT, TitledBorder.TOP));
        
        // Campo de imagem
        campoImagem = new CampoImagem("Foto do Produto");
        campoImagem.setTamanhoPreview(120, 120);
        campoImagem.setTextoVazio("Clique aqui");
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        JButton btnValidar = new JButton("Validar");
        btnValidar.addActionListener(e -> {
            campoImagem.setObrigatorio(true);
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (campoImagem.validarComFeedback()) {
                Toast.success(parent, "Imagem válida!");
            }
        });
        
        JButton btnInfo = new JButton("Info");
        btnInfo.addActionListener(e -> {
            byte[] bytes = campoImagem.getImagemBytes();
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (bytes != null) {
                Toast.info(parent, "Tamanho: " + (bytes.length / 1024) + " KB");
            } else {
                Toast.warning(parent, "Nenhuma imagem");
            }
        });
        
        JButton btnVer = new JButton("Visualizar");
        btnVer.addActionListener(e -> {
            BufferedImage img = campoImagem.getImagem();
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (img != null) {
                VisualizadorImagem.mostrar(parent, img, "Preview");
            } else {
                Toast.warning(parent, "Nenhuma imagem");
            }
        });
        
        painelBotoes.add(btnValidar);
        painelBotoes.add(btnInfo);
        painelBotoes.add(btnVer);
        
        painel.add(campoImagem, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private JPanel criarPainelArquivo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "CampoArquivo",
            TitledBorder.LEFT, TitledBorder.TOP));
        
        // Documento PDF
        CampoArquivo campoPdf = new CampoArquivo("Documento PDF");
        campoPdf.setFiltro("PDF", "pdf");
        campoPdf.setPlaceholder("Selecione um PDF...");
        campoPdf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        campoPdf.setAlignmentX(LEFT_ALIGNMENT);
        
        // Múltiplas imagens
        CampoArquivo campoImgs = new CampoArquivo("Imagens (múltiplas)");
        campoImgs.setMultiplo(true);
        campoImgs.setFiltro("Imagens", "jpg", "jpeg", "png", "gif");
        campoImgs.setPlaceholder("Selecione imagens...");
        campoImgs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        campoImgs.setAlignmentX(LEFT_ALIGNMENT);
        
        // Pasta
        CampoArquivo campoPasta = new CampoArquivo("Pasta de destino");
        campoPasta.setSelecionarPasta(true);
        campoPasta.setPlaceholder("Selecione uma pasta...");
        campoPasta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        campoPasta.setAlignmentX(LEFT_ALIGNMENT);
        
        painel.add(campoPdf);
        painel.add(Box.createVerticalStrut(5));
        painel.add(campoImgs);
        painel.add(Box.createVerticalStrut(5));
        painel.add(campoPasta);
        
        return painel;
    }
    
    private JPanel criarPainelAvatares() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "PainelAvatar",
            TitledBorder.LEFT, TitledBorder.TOP));
        
        // Avatares de exemplo
        JPanel avatares = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        String[] nomes = {"João Silva", "Maria Santos", "Pedro Costa", "Ana Lima", "Carlos"};
        for (String nome : nomes) {
            JPanel item = new JPanel(new BorderLayout(0, 3));
            item.setOpaque(false);
            
            PainelAvatar avatar = new PainelAvatar(nome);
            avatar.setTamanho(42);
            
            JLabel lbl = new JLabel(nome.split(" ")[0]);
            lbl.setFont(lbl.getFont().deriveFont(10f));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            
            item.add(avatar, BorderLayout.CENTER);
            item.add(lbl, BorderLayout.SOUTH);
            avatares.add(item);
        }
        
        // Avatar com imagem
        JPanel avatarComFoto = new JPanel(new BorderLayout(0, 5));
        avatarComFoto.setOpaque(false);
        
        PainelAvatar avatarFoto = new PainelAvatar("Usuário");
        avatarFoto.setTamanho(50);
        
        JButton btnUsarFoto = new JButton("Usar foto acima");
        btnUsarFoto.setFont(btnUsarFoto.getFont().deriveFont(11f));
        btnUsarFoto.addActionListener(e -> {
            BufferedImage img = campoImagem.getImagem();
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (img != null) {
                avatarFoto.setImagem(img);
                Toast.success(parent, "Foto aplicada!");
            } else {
                Toast.warning(parent, "Selecione uma imagem primeiro");
            }
        });
        
        avatarComFoto.add(avatarFoto, BorderLayout.CENTER);
        avatarComFoto.add(btnUsarFoto, BorderLayout.SOUTH);
        
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        wrapper.setOpaque(false);
        wrapper.add(avatares);
        wrapper.add(avatarComFoto);
        
        painel.add(wrapper, BorderLayout.CENTER);
        
        // Diferentes tamanhos
        JPanel tamanhos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        tamanhos.setOpaque(false);
        tamanhos.add(new JLabel("Tamanhos: "));
        
        int[] sizes = {24, 32, 40, 48, 64};
        for (int s : sizes) {
            PainelAvatar av = new PainelAvatar("AB");
            av.setTamanho(s);
            av.setToolTipText(s + "px");
            tamanhos.add(av);
        }
        
        painel.add(tamanhos, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private JPanel criarPainelVisualizador() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "VisualizadorImagem",
            TitledBorder.LEFT, TitledBorder.TOP));
        
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnAbrir = new JButton("Abrir arquivo...");
        btnAbrir.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens", "jpg", "jpeg", "png", "gif", "bmp"));
            
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    VisualizadorImagem.mostrar(parent, fc.getSelectedFile(), "Visualizador");
                } catch (Exception ex) {
                    Toast.error(parent, "Erro: " + ex.getMessage());
                }
            }
        });
        
        JButton btnVerSelecionada = new JButton("Ver imagem selecionada");
        btnVerSelecionada.addActionListener(e -> {
            BufferedImage img = campoImagem.getImagem();
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (img != null) {
                VisualizadorImagem.mostrar(parent, img, "Visualizador");
            } else {
                Toast.warning(parent, "Selecione uma imagem acima");
            }
        });
        
        botoes.add(btnAbrir);
        botoes.add(btnVerSelecionada);
        
        JLabel dica = new JLabel("<html><i>Scroll = zoom | Arrastar = mover | Duplo clique = ajustar</i></html>");
        dica.setForeground(Color.GRAY);
        dica.setFont(dica.getFont().deriveFont(11f));
        
        painel.add(botoes, BorderLayout.CENTER);
        painel.add(dica, BorderLayout.SOUTH);
        
        return painel;
    }
}

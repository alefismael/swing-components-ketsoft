package exemplo;

import dialogo.*;
import componente.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Exemplo dos Diálogos Prontos (Ciclo 15).
 */
public class ExemploDialogos extends JPanel {
    
    public ExemploDialogos() {
        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // DialogoLogin
        JPanel pLogin = criarPainel("DialogoLogin", "Diálogo de login com validação");
        JButton btnLogin = new JButton("Abrir Login");
        btnLogin.addActionListener(e -> {
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            DialogoLogin login = new DialogoLogin(frame);
            login.setTitulo("Sistema Demo");
            login.setValidador((u, s) -> {
                try { Thread.sleep(500); } catch (Exception ex) {}
                return u.equals("admin") && s.equals("123");
            });
            login.setAoLogar(() -> JOptionPane.showMessageDialog(this, "Login OK!"));
            login.setVisible(true);
        });
        ((JPanel) pLogin.getComponent(1)).add(btnLogin);
        add(pLogin);
        
        // TelaSplash
        JPanel pSplash = criarPainel("TelaSplash", "Tela de splash com progresso");
        JButton btnSplash = new JButton("Mostrar Splash");
        btnSplash.addActionListener(e -> {
            TelaSplash splash = new TelaSplash("Sistema Demo", "1.0.0");
            splash.simularCarregamento(10, 200);
        });
        ((JPanel) pSplash.getComponent(1)).add(btnSplash);
        add(pSplash);
        
        // DialogoSobre
        JPanel pSobre = criarPainel("DialogoSobre", "Diálogo 'Sobre' padrão");
        JButton btnSobre = new JButton("Abrir Sobre");
        btnSobre.addActionListener(e -> {
            DialogoSobre.mostrar(this, "Swing Alef", "1.1.0", 
                "Biblioteca de componentes Swing para sistemas empresariais brasileiros.");
        });
        ((JPanel) pSobre.getComponent(1)).add(btnSobre);
        add(pSobre);
        
        // DialogoConfiguracoes
        JPanel pConfig = criarPainel("DialogoConfiguracoes", "Diálogo de configurações");
        JButton btnConfig = new JButton("Abrir Configurações");
        btnConfig.addActionListener(e -> abrirConfiguracoes());
        ((JPanel) pConfig.getComponent(1)).add(btnConfig);
        add(pConfig);
        
        // DialogoBusca
        JPanel pBusca = criarPainel("DialogoBusca<T>", "Diálogo de busca genérico");
        JButton btnBusca = new JButton("Abrir Busca");
        btnBusca.addActionListener(e -> abrirBusca());
        ((JPanel) pBusca.getComponent(1)).add(btnBusca);
        add(pBusca);
        
        // Placeholder
        add(new JPanel());
    }
    
    private JPanel criarPainel(String titulo, String descricao) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        
        JLabel lbl = new JLabel("<html>" + descricao + "</html>");
        p.add(lbl, BorderLayout.NORTH);
        
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(botoes, BorderLayout.CENTER);
        
        return p;
    }
    
    private void abrirConfiguracoes() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoConfiguracoes config = new DialogoConfiguracoes(frame);
        
        // Categoria Geral
        JPanel pGeral = DialogoConfiguracoes.criarPainelOpcoes();
        DialogoConfiguracoes.adicionarOpcao(pGeral, "Idioma:", new JComboBox<>(new String[]{"Português", "English"}));
        DialogoConfiguracoes.adicionarOpcao(pGeral, "Iniciar minimizado:", new JCheckBox());
        DialogoConfiguracoes.adicionarOpcao(pGeral, "Salvar ao sair:", new JCheckBox("", true));
        config.adicionarCategoria("Geral", pGeral);
        
        // Categoria Aparência
        JPanel pAparencia = DialogoConfiguracoes.criarPainelOpcoes();
        DialogoConfiguracoes.adicionarOpcao(pAparencia, "Tema:", new JComboBox<>(new String[]{"Claro", "Escuro", "Sistema"}));
        DialogoConfiguracoes.adicionarOpcao(pAparencia, "Fonte:", new JSpinner(new SpinnerNumberModel(12, 8, 24, 1)));
        config.adicionarCategoria("Aparência", pAparencia);
        
        // Categoria Avançado
        JPanel pAvancado = DialogoConfiguracoes.criarPainelOpcoes();
        DialogoConfiguracoes.adicionarOpcao(pAvancado, "Logs:", new JCheckBox("Habilitar debug"));
        DialogoConfiguracoes.adicionarOpcao(pAvancado, "Cache:", new JTextField("100 MB"));
        config.adicionarCategoria("Avançado", pAvancado);
        
        config.setAoSalvar(() -> JOptionPane.showMessageDialog(this, "Configurações salvas!"));
        config.setVisible(true);
    }
    
    private void abrirBusca() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoBusca<String> busca = new DialogoBusca<>(frame, "Buscar Produto");
        
        busca.setColunas("ID", "Nome", "Preço");
        busca.setConversor(s -> s.split("\\|"));
        
        // Simular dados
        List<String> produtos = Arrays.asList(
            "1|Notebook|4500.00",
            "2|Mouse|150.00",
            "3|Teclado|200.00",
            "4|Monitor|1800.00",
            "5|Webcam|350.00",
            "6|Headset|400.00",
            "7|SSD 1TB|600.00",
            "8|Memória 16GB|450.00"
        );
        
        busca.setFonteDados(termo -> {
            if (termo.isEmpty()) return produtos;
            return produtos.stream()
                .filter(p -> p.toLowerCase().contains(termo.toLowerCase()))
                .toList();
        });
        
        busca.setAoSelecionar(item -> {
            JOptionPane.showMessageDialog(this, "Selecionado: " + item);
        });
        
        busca.buscarAoAbrir();
        busca.setVisible(true);
    }
}
